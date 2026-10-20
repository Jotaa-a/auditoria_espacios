package com.jotaa.auditoria_espacios.dto.request;

import com.jotaa.auditoria_espacios.entidad.enumerated.Resultado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DetallePuestoRequest(
        @NotNull Resultado resultado,
        @Valid List<HallazgoRequest> hallazgos
) {
}
