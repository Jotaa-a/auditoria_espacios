package com.jotaa.auditoria_espacios.dto.response;

import java.time.Instant;

public record LoginResponse (
        String token, String tipo, Instant espiraEn, UsuarioResponse usuario
) {
}
