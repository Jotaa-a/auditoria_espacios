package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.*;
import com.jotaa.auditoria_espacios.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar();
    UsuarioResponse obtener(Long id);
    UsuarioResponse crear(UsuarioCrearRequest req);
    UsuarioResponse actualizar(Long id, UsuarioActualizarRequest req);
    void cambiarPassword(Long id, CambiarPasswordRequest req);
    void cambiarPasswordPropia(Long UsuarioId, CambiarPasswordPropiaRequest req);
    void solicitarRecuperacionPassword(OlvidoPasswordRequest req);
    void restablecerPasswordConToken(RestablecerPasswordTokenRequest req);
}
