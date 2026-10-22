package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.AuditoriaLog;
import com.jotaa.auditoria_espacios.entity.Usuario;

import java.time.LocalDateTime;
import java.util.Map;

public record AuditoriaResponse(Long id, Long usuarioId, String usuarioNombre,
                                String accion, String entidad, Long entidadId,
                                Map<String, Object> detalle, LocalDateTime fecha) {
    public static AuditoriaResponse from(AuditoriaLog a) {
        Usuario u = a.getUsuario();
        return new AuditoriaResponse(a.getId(),
                u != null ? u.getId() : null, u != null ? u.getNombre() : null,
                a.getAccion(), a.getEntidad(), a.getEntidadId(), a.getDetalle(), a.getFecha());
    }
}
