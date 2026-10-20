package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entidad.PuestoTrabajo;

public record PuestoResponse(
        Long id, Long espacioId, String espacioNombre,
        String codigo, Boolean activo
) {
    public static PuestoResponse from(PuestoTrabajo p){
        return new PuestoResponse(p.getId(), p.getEspacio().getId(),
                p.getEspacio().getNombre(), p.getCodigo(), p.getActivo());
    }
}
