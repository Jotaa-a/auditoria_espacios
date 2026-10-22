package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.CambiarPasswordRequest;
import com.jotaa.auditoria_espacios.dto.request.UsuarioActualizarRequest;
import com.jotaa.auditoria_espacios.dto.request.UsuarioCrearRequest;
import com.jotaa.auditoria_espacios.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar();
    UsuarioResponse obtener(Long id);
    UsuarioResponse crear(UsuarioCrearRequest req);
    UsuarioResponse actualizar(Long id, UsuarioActualizarRequest req);
    void cambiarPassword(Long id, CambiarPasswordRequest req);
}
