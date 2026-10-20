package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entidad.enumerated.TipoInspeccion;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InspeccionRequest(
        @NotNull Long espacioId,
        @NotNull TipoInspeccion tipo,
        @NotNull LocalDate fecha,
        @NotNull Long inspectorId,
        String observaciones
) {
}
