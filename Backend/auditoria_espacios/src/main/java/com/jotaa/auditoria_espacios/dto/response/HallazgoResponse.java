package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.Componente;
import com.jotaa.auditoria_espacios.entity.Hallazgo;
import com.jotaa.auditoria_espacios.entity.enumerated.Categoria;
import com.jotaa.auditoria_espacios.entity.enumerated.Gravedad;
import com.jotaa.auditoria_espacios.entity.enumerated.TipoComponente;

public record HallazgoResponse(
        Long id, Long tipoHallazgo, String tipoCodigo, String tipoNombre,
        Categoria categoria, Long componenteId, TipoComponente componenteTipo,
        String descripcion, Gravedad gravedad
) {
    public static HallazgoResponse from(Hallazgo h) {
        Componente c = h.getComponente();
        return new HallazgoResponse(h.getId(), h.getTipoHallazgo().getId(),
                h.getTipoHallazgo().getCodigo(), h.getTipoHallazgo().getNombre(),
                h.getTipoHallazgo().getCategoria(),
                c != null ? c.getId() : null, c != null ? c.getTipo() : null,
                h.getDescripcion(), h.getGravedad());
    }
}
