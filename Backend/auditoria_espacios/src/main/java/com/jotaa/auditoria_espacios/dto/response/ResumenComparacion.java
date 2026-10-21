package com.jotaa.auditoria_espacios.dto.response;

public record ResumenComparacion(
        int totalPuestos, long sinCambios,
        long nuevasNovedades, long persisten,
        long resueltos, long sinRegistro
) {
}
