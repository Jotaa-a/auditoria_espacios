package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.ReporteRequest;
import com.jotaa.auditoria_espacios.dto.response.ReporteResponse;
import com.jotaa.auditoria_espacios.entity.ReporteDano;
import com.jotaa.auditoria_espacios.entity.enumerated.EstadoReporte;

import java.util.List;

public interface ReporteDanoService {
    ReporteResponse crear(ReporteRequest req);
    List<ReporteResponse> listar(EstadoReporte estado, Long espacioId);
    ReporteResponse cambiarEstado(Long id, EstadoReporte nuevo);
    ReporteDano buscar(Long id);
    ReporteResponse toResponse(ReporteDano r);
    ReporteResponse obtener(Long id);

}
