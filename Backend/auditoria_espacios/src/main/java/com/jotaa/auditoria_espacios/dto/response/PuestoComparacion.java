package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoComparacion;
import com.jotaa.auditoria_espacios.entidad.enumerated.Resultado;

import java.util.List;

public record PuestoComparacion(
        Long puestoId, String puestoCodigo,
        EstadoComparacion estado, Resultado resultadoViernes,
        Resultado resultadoLunes, List<HallazgoResponse> hallazgosNuevos,
        List<HallazgoResponse> hallazgosPersistentes, List<HallazgoResponse> hallazgosResueltos
) {
}
