package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.request.ComponenteRequest;
import com.jotaa.auditoria_espacios.dto.response.ComponenteResponse;
import com.jotaa.auditoria_espacios.entity.Componente;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.ComponenteRepository;
import com.jotaa.auditoria_espacios.repository.PuestoTrabajoRepository;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import com.jotaa.auditoria_espacios.service.ComponenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComponenteServiceImpl implements ComponenteService {

    private final ComponenteRepository componenteRepo;
    private final PuestoTrabajoRepository puestoRepo;
    private final AuditoriaService auditoria;

    @Override
    @Transactional(readOnly = true)
    public List<ComponenteResponse> listar(Long puestoId) {
        List<Componente> lista = puestoId == null
                ? componenteRepo.findAll() : componenteRepo.findByPuestoId(puestoId);
        return lista.stream().map(ComponenteResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComponenteResponse obtener(Long id) {
        return ComponenteResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public ComponenteResponse crear(ComponenteRequest req) {
        Componente c = new Componente();
        aplicar(c, req);
        Componente g = componenteRepo.save(c);
        auditoria.registrar(Accion.CREAR, "componente", g.getId(),
                Map.of("tipo", g.getTipo(), "puestoId", req.puestoId()));

        return ComponenteResponse.from(g);
    }

    @Override
    @Transactional
    public ComponenteResponse actualizar(Long id, ComponenteRequest req) {
        Componente c = buscar(id);
        aplicar(c, req);
        auditoria.registrar(Accion.ACTUALIZAR, "componente", id,
                Map.of("tipo", c.getTipo().name(), "puestoId", req.puestoId()));
        return ComponenteResponse.from(c);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Componente c = buscar(id);
        componenteRepo.delete(c);
        auditoria.registrar(Accion.ELIMINAR, "componente", id, Map.of("tipo", c.getTipo().name()));
    }

    @Override
    public Componente buscar(Long id) {
        return componenteRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Componente no encontrado; " + id));
    }

    @Override
    public void aplicar(Componente c, ComponenteRequest req) {
        c.setPuesto(puestoRepo.findById(req.puestoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto no encontrado: " + req.puestoId())));

        c.setTipo(req.tipo());
        c.setSerial(req.serial());
        c.setDescripcion(req.descripcion());
    }
}
