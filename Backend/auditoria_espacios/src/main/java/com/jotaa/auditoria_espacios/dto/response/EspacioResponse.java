package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.Espacio;
import com.jotaa.auditoria_espacios.entity.Usuario;

public record EspacioResponse(
        Long id, String nombre, Long representanteId, String representanteNombre
) {
    public static EspacioResponse from(Espacio e){
        Usuario r = e.getRepresentante();
        return new EspacioResponse(e.getId(), e.getNombre(),
                r != null ? r.getId() : null, r != null ? r.getNombre() : null);
    }
}
