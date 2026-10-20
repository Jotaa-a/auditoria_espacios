package com.jotaa.auditoria_espacios.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HallazgoRequest(
        @NotNull Long tipoHallazgoId,
        Long componenteId,
        @Size(max = 500) String descripcion
) {
}
