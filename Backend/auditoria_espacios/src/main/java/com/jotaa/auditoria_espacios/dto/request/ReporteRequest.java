package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entity.enumerated.Gravedad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReporteRequest(
        @NotNull Long componenteId,
        Long inspeccionId,
        @NotBlank String descripcion,
        @NotNull Gravedad gravedad
) {
}
