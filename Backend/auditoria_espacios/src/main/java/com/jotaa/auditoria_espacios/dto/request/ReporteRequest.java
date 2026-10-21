package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entidad.enumerated.Gravedad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReporteRequest(
        @NotNull Long componenteId,
        @NotNull Long reportadoPorId,
        Long inspeccionID,
        @NotBlank String descripcion,
        @NotNull Gravedad gravedad
) {
}
