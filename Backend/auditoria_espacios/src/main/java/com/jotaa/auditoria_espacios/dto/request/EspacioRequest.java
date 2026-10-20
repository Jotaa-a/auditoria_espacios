package com.jotaa.auditoria_espacios.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspacioRequest(
        @NotBlank @Size(max = 80) String nombre,
        Long representanteId
) {
}
