package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.ComponenteRequest;
import com.jotaa.auditoria_espacios.dto.response.ComponenteResponse;
import com.jotaa.auditoria_espacios.entity.Componente;

import java.util.List;

public interface ComponenteService {
    List<ComponenteResponse> listar(Long puestoId);
    ComponenteResponse obtener(Long id);
    ComponenteResponse crear(ComponenteRequest req);
    ComponenteResponse actualizar(Long id, ComponenteRequest req);
    void eliminar(Long id);
    Componente buscar(Long id);
    void aplicar(Componente c, ComponenteRequest req);

}
