package com.jotaa.auditoria_espacios.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestablecerPasswordTokenRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8, max = 72, message = "La nueva contraseña sebe tener entre 8 y 72 caracteres") String nuevaPassword
) {
}
