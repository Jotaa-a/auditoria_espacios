package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.Componente;
import com.jotaa.auditoria_espacios.entity.enumerated.TipoComponente;

public record ComponenteResponse(
        Long id, Long puestoId, String puestCodigo, TipoComponente tipo,
        String serial, String descripcion
) {
    public static ComponenteResponse from(Componente c){
        return new ComponenteResponse(c.getId(), c.getPuesto().getId(), c.getPuesto().getCodigo(),
                c.getTipo(), c.getSerial(), c.getDescripcion());
    }
}
