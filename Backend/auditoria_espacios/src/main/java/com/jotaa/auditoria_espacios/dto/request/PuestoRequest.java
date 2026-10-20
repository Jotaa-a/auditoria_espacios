package com.jotaa.auditoria_espacios.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PuestoRequest(
        @NotNull Long espacioId,
        @NotBlank @Size(max = 30) String codigo,
        Boolean activo
) {
}
