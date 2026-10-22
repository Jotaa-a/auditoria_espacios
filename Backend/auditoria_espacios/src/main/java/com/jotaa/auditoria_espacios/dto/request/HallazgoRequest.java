package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entity.enumerated.Gravedad;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HallazgoRequest(
        @NotNull Long tipoHallazgoId,
        Long componenteId,
        @Size(max = 500) String descripcion,
        Gravedad gravedad
) {
}
