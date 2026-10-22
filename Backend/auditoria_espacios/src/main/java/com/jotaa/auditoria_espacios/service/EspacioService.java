package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.EspacioRequest;
import com.jotaa.auditoria_espacios.dto.response.EspacioResponse;
import com.jotaa.auditoria_espacios.entity.Espacio;

import java.util.List;

public interface EspacioService {
    List<EspacioResponse> listar();
    EspacioResponse obtener(Long id);
    EspacioResponse crear(EspacioRequest req);
    EspacioResponse actualizar(Long id, EspacioRequest req);
    void eliminar(Long id);
    Espacio buscar(Long id);
    void aplicar(Espacio e, EspacioRequest req);
}
