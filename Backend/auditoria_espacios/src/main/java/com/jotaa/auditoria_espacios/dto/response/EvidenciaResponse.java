package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.Evidencia;

import java.time.LocalDateTime;

public record EvidenciaResponse(
        Long id,
        LocalDateTime subidaEn,
        String url
) {
    public static EvidenciaResponse from(Evidencia e){
        return new EvidenciaResponse(
                e.getId(), e.getSubidaEn(),
                "/api/evidencias" + e.getId() + "/archivo"
        );
    }
}
