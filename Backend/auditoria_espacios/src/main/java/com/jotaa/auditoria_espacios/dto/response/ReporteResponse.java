package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoReporte;
import com.jotaa.auditoria_espacios.entidad.enumerated.Gravedad;
import com.jotaa.auditoria_espacios.entidad.enumerated.TipoComponente;

import java.time.LocalDateTime;
import java.util.List;

public record ReporteResponse(
        Long id, Long componenteId, TipoComponente componenteTipo,
        String puestoCodigo, String espacioNombre, Long reportadoPorId,
        String reportadoPorNombre, Long inspeccionId, String descripcion,
        Gravedad gravedad, EstadoReporte estado, LocalDateTime creadoEn,
        LocalDateTime resueltoEn, List<EvidenciaResponse> evidencias
) {
}
