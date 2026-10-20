package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoInspeccion;
import com.jotaa.auditoria_espacios.entidad.enumerated.TipoInspeccion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InspeccionResponse(
        Long id, Long espacioId, String espacioNombre,
        TipoInspeccion tipo, LocalDate fecha,
        Long inspectorId, String inspectorNombre,
        EstadoInspeccion estado, String observaciones,
        LocalDateTime creadaEn, Long puestosRegistrados,
        Long puestosTotales, List<DetalleResponse> detalles
) {
}
