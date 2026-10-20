package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entidad.enumerated.TipoComponente;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComponenteRequest(
        @NotNull Long puestoId,
        @NotNull TipoComponente tipo,
        @Size(max = 80) String serial,
        @Size(max = 150) String descipcion
        ) {
}
