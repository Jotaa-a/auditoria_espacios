package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entidad.Componente;
import com.jotaa.auditoria_espacios.entidad.Hallazgo;
import com.jotaa.auditoria_espacios.entidad.enumerated.Categoria;
import com.jotaa.auditoria_espacios.entidad.enumerated.TipoComponente;

public record HallazgoResponse(
        Long id, Long tipoHallazgo, String tipoCodigo, String tipoNombre,
        Categoria categoria, Long componenteId, TipoComponente componenteTipo,
        String descripcion
) {
    public static HallazgoResponse from(Hallazgo h) {
        Componente c = h.getComponente();
        return new HallazgoResponse(h.getId(), h.getTipoHallazgo().getId(),
                h.getTipoHallazgo().getCodigo(), h.getTipoHallazgo().getNombre(),
                h.getTipoHallazgo().getCategoria(),
                c != null ? c.getId() : null, c != null ? c.getTipo() : null,
                h.getDescripcion());
    }
}
