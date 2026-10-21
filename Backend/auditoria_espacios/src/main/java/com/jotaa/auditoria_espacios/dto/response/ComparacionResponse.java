package com.jotaa.auditoria_espacios.dto.response;

import java.util.List;

public record ComparacionResponse (
        Long espacioId, String espacioNombre,
        InspeccionRef viernes, InspeccionRef lunes,
        ResumenComparacion resumen, List<PuestoComparacion> puestos
){
}
