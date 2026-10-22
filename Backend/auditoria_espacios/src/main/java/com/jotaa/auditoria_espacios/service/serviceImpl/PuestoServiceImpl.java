package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.request.PuestoRequest;
import com.jotaa.auditoria_espacios.dto.response.PuestoResponse;
import com.jotaa.auditoria_espacios.entity.Espacio;
import com.jotaa.auditoria_espacios.entity.PuestoTrabajo;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.EspacioRepository;
import com.jotaa.auditoria_espacios.repository.PuestoTrabajoRepository;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import com.jotaa.auditoria_espacios.service.PuestoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PuestoServiceImpl implements PuestoService {

    private final PuestoTrabajoRepository puestoRepo;
    private final EspacioRepository espacioRepo;
    private final AuditoriaService auditoria;

    @Override
    @Transactional(readOnly = true)
    public List<PuestoResponse> listar(Long espacioId) {
        List<PuestoTrabajo> lista = espacioId == null
                ? puestoRepo.findAll() : puestoRepo.findByEspacioId(espacioId);
        return lista.stream().map(PuestoResponse::from).toList();
    }

    @Override
    public PuestoResponse obtener(Long id) {
        return PuestoResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public PuestoResponse crear(PuestoRequest req) {
        if (puestoRepo.existsByEspacioIdAndCodigo(req.espacioId(), req.codigo()))
            throw new ConflictoException("Ya existe un puesto con ese codigo en el espacio");
        PuestoTrabajo p = new PuestoTrabajo();
        p.setEspacio(buscarEspacio(req.espacioId()));
        p.setCodigo(req.codigo());
        p.setActivo(req.activo() == null || req.activo());
        auditoria.registrar(Accion.CREAR, "puesto_trabajo", p.getId(),
                Map.of("codigo", p.getCodigo(), "espacio_id", req.espacioId()));
        return PuestoResponse.from(puestoRepo.save(p));
    }

    @Override
    @Transactional
    public PuestoResponse actualizar(Long id, PuestoRequest req) {
        PuestoTrabajo p = buscar(id);
        boolean cambioClave = !p.getEspacio().getId().equals(req.espacioId())
                || !p.getCodigo().equals(req.codigo());
        if (cambioClave && puestoRepo.existsByEspacioIdAndCodigo(req.espacioId(), req.codigo()))
            throw new ConflictoException("Ya existe un puesto con ese codigo en el espacio");
        p.setEspacio(buscarEspacio(req.espacioId()));
        p.setCodigo(req.codigo());
        if(req.activo() != null) p.setActivo(req.activo());
        auditoria.registrar(Accion.ACTUALIZAR, "puesto_trabajo", id,
                Map.of("codigo", p.getCodigo(), "espacioId", req.espacioId(), "activo", p.getActivo()));
        return PuestoResponse.from(p);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        PuestoTrabajo p = buscar(id);
        p.setActivo(false);
        auditoria.registrar(Accion.DESACTIVAR, "puesto_trabajo", id, Map.of("codigo", p.getCodigo()));
    }

    @Override
    public PuestoTrabajo buscar(Long id) {
        return puestoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto no encontrado: " + id));
    }

    @Override
    public Espacio buscarEspacio(Long id) {
        return espacioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado: " + id));
    }
}
