package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.configuration.UsuarioActual;
import com.jotaa.auditoria_espacios.dto.request.ReporteRequest;
import com.jotaa.auditoria_espacios.dto.response.EvidenciaResponse;
import com.jotaa.auditoria_espacios.dto.response.ReporteResponse;
import com.jotaa.auditoria_espacios.entity.Componente;
import com.jotaa.auditoria_espacios.entity.ReporteDano;
import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.entity.enumerated.EstadoReporte;
import com.jotaa.auditoria_espacios.entity.enumerated.Rol;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.*;
import com.jotaa.auditoria_espacios.service.ReporteDanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteDanoServiceImpl implements ReporteDanoService {

    private final ReporteDanoRepository reporteRepo;
    private final ComponenteRepository componenteRepo;
    private final UsuarioRepository usuarioRepo;
    private final InspeccionRepository inspeccionRepo;
    private final EvidenciaRepository evidenciaRepo;
    private final UsuarioActual usuarioActual;

    @Override
    @Transactional
    public ReporteResponse crear(ReporteRequest req) {
        Usuario actor = usuarioActual.obtener();
        Componente componente = componenteRepo.findById(req.componenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Componente no encontrado: " + req.componenteId()));

        if (actor.getRol() == Rol.REPRESENTANTE) {
            Usuario rep = componente.getPuesto().getEspacio().getRepresentante();
            if (rep == null || !rep.getId().equals(actor.getId()))
                throw new AccessDeniedException("Solo puedes reportar daños en el espacio que representas");
        }

        ReporteDano r = new ReporteDano();
        r.setComponente(componente);
        r.setReportadoPor(actor);
        if (req.inspeccionId() != null) {
            r.setInspeccion(inspeccionRepo.findById(req.inspeccionId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inspección no encontrada: " + req.inspeccionId())));
        }
        r.setDescripcion(req.descripcion());
        r.setGravedad(req.gravedad());
        return toResponse(reporteRepo.save(r));
    }
    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponse> listar(EstadoReporte estado, Long espacioId) {
        return reporteRepo.buscar(estado, espacioId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponse cambiarEstado(Long id, EstadoReporte nuevo) {
        ReporteDano r = buscar(id);
        if(nuevo.ordinal() <= r.getEstado().ordinal())
            throw new ConflictoException("No se puede pasar de " + r.getEstado() + " a " + nuevo);
        r.setEstado(nuevo);
        if (nuevo == EstadoReporte.RESUELTO) r.setResueltoEn(LocalDateTime.now());
        return toResponse(r);
    }

    @Override
    public ReporteDano buscar(Long id) {
        return reporteRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado: " + id));
    }

    @Override
    public ReporteResponse toResponse(ReporteDano r) {
        Componente c = r.getComponente();
        List<EvidenciaResponse> ev = evidenciaRepo.findByReporteId(r.getId())
                .stream().map(EvidenciaResponse::from).toList();
        return new ReporteResponse(r.getId(), c.getId(), c.getTipo(), c.getPuesto().getCodigo(),
                c.getPuesto().getEspacio().getNombre(),
                r.getReportadoPor().getId(), r.getReportadoPor().getNombre(),
                r.getInspeccion() != null ? r.getInspeccion().getId() : null,
                r.getDescripcion(), r.getGravedad(), r.getEstado(),
                r.getCreadoEn(), r.getResueltoEn(), ev);
    }

    @Override
    public ReporteResponse obtener(Long id) {
        return toResponse(buscar(id));
    }
}
