package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entity.enumerated.EstadoReporte;
import jakarta.validation.constraints.NotNull;

public record EstadoReporteRequest(
        @NotNull EstadoReporte estado
        ) {
}
