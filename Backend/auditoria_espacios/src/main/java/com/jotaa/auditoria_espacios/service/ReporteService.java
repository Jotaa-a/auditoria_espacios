package com.jotaa.auditoria_espacios.service;

public interface ReporteService {
    void enviarReportePorCorreo(Long inspeccionId, String correoDestino);

    byte[] generarPdfInspeccion(Long inpeccionId);
}
