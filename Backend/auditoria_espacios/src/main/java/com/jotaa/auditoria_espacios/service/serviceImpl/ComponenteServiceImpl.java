package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.request.ComponenteRequest;
import com.jotaa.auditoria_espacios.dto.response.ComponenteResponse;
import com.jotaa.auditoria_espacios.entidad.Componente;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.ComponenteRepository;
import com.jotaa.auditoria_espacios.repository.PuestoTrabajoRepository;
import com.jotaa.auditoria_espacios.service.ComponenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponenteServiceImpl implements ComponenteService {

    private final ComponenteRepository componenteRepo;
    private final PuestoTrabajoRepository puestoRepo;

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
    public ComponenteResponse crear(ComponenteRequest req) {
        Componente c = new Componente();
        aplicar(c, req);
        return ComponenteResponse.from(componenteRepo.save(c));
    }

    @Override
    public ComponenteResponse actualizar(Long id, ComponenteRequest req) {
        Componente c = buscar(id);
        aplicar(c, req);
        return ComponenteResponse.from(c);
    }

    @Override
    public void eliminar(Long id) {
        componenteRepo.delete(buscar(id));
    }

    @Override
    public Componente buscar(Long id) {
        return componenteRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto no encontrado; " + id));
    }

    @Override
    public void aplicar(Componente c, ComponenteRequest req) {
        c.setPuesto(puestoRepo.findById(req.puestoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto no encontrado: " + req.puestoId())));

        c.setTipo(req.tipo());
        c.setSerial(req.serial());
        c.setDescripcion(req.descipcion());
    }
}
