package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entity.enumerated.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCrearRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 120) String correo,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull Rol rol
) {
}
