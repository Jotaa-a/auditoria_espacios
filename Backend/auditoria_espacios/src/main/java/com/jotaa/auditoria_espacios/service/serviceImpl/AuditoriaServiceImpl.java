package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.response.AuditoriaResponse;
import com.jotaa.auditoria_espacios.entity.AuditoriaLog;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import com.jotaa.auditoria_espacios.repository.AuditoriaLogRepository;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaLogRepository auditoriaRepo;
    private final UsuarioRepository usuarioRepo;

    @Override
    @Transactional
    public void registrar(Accion accion, String entidad, Long entidadId, Map<String, Object> detalle) {
        guardar(actorActual(), accion, entidad, entidadId, detalle);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarIndependiente(Long usuarioId, Accion accion, String entidad, Long entidadId, Map<String, Object> detalle) {
        guardar(usuarioId, accion, entidad, entidadId, detalle);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<AuditoriaResponse> buscar(String entidad, Long usuarioId,
                                                LocalDate desde, LocalDate hasta, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Order.desc("fecha"), Sort.Order.desc("id")));
        Page<AuditoriaResponse> resultado = auditoriaRepo.buscar(
                entidad, usuarioId,
                desde != null ? desde.atStartOfDay() : null,
                hasta != null ? hasta.plusDays(1).atStartOfDay() : null,
                pageable).map(AuditoriaResponse::from);
        return new PagedModel<>(resultado);
    }

    private void guardar(Long usuarioId, Accion accion, String entidad,
                         Long entidadId, Map<String, Object> detalle) {
        AuditoriaLog l = new AuditoriaLog();
        l.setUsuario(usuarioId != null ? usuarioRepo.getReferenceById(usuarioId) : null);
        l.setAccion(accion.name());
        l.setEntidad(entidad);
        l.setEntidadId(entidadId);
        l.setDetalle(detalle);
        auditoriaRepo.save(l);
    }

    private Long actorActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) return Long.valueOf(jwt.getSubject());
        return null;
    }
}
