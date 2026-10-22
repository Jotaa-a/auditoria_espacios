package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.PuestoRequest;
import com.jotaa.auditoria_espacios.dto.response.PuestoResponse;
import com.jotaa.auditoria_espacios.entity.Espacio;
import com.jotaa.auditoria_espacios.entity.PuestoTrabajo;

import java.util.List;

public interface PuestoService {
    List<PuestoResponse> listar(Long espacioId);
    PuestoResponse obtener(Long id);
    PuestoResponse crear(PuestoRequest req);
    PuestoResponse actualizar(Long id, PuestoRequest req);
    void desactivar(Long id);
    PuestoTrabajo buscar(Long id);
    Espacio buscarEspacio(Long id);
}
