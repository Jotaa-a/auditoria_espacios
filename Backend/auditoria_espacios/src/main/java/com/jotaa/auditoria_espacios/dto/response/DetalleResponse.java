package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.InspeccionDetalle;
import com.jotaa.auditoria_espacios.entity.enumerated.Resultado;

import java.util.List;

public record DetalleResponse(
        Long id, Long puestoId, String puestoCodigo,
        Resultado resultado, List<HallazgoResponse> hallazgos
) {
    public static DetalleResponse from(InspeccionDetalle d) {
        return new DetalleResponse(d.getId(), d.getPuesto().getId(), d.getPuesto().getCodigo(),
                d.getResultado(), d.getHallazgos().stream().map(HallazgoResponse::from).toList());
    }
}
