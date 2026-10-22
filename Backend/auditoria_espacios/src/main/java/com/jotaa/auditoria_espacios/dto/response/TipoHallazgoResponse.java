package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.TipoHallazgo;
import com.jotaa.auditoria_espacios.entity.enumerated.Categoria;

public record TipoHallazgoResponse(
        Long id, String codigo, String nombre, Categoria categoria
) {
    public static TipoHallazgoResponse from(TipoHallazgo t) {
        return new TipoHallazgoResponse(t.getId(), t.getCodigo(), t.getNombre(), t.getCategoria());
    }
}
