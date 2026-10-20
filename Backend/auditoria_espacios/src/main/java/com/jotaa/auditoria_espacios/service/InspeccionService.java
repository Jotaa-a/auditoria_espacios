package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.DetallePuestoRequest;
import com.jotaa.auditoria_espacios.dto.request.InspeccionRequest;
import com.jotaa.auditoria_espacios.dto.response.DetalleResponse;
import com.jotaa.auditoria_espacios.dto.response.InspeccionResponse;
import com.jotaa.auditoria_espacios.entidad.Inspeccion;

import java.time.LocalDate;
import java.util.List;

public interface InspeccionService {
    InspeccionResponse crear (InspeccionRequest req);
    List<InspeccionResponse> listar(Long espacioId, LocalDate fecha);
    InspeccionResponse obtener(Long id);
    DetalleResponse registrarPuesto(Long inspeccionId, Long puestoId, DetallePuestoRequest req);
    InspeccionResponse cerrar(Long id);
    Inspeccion buscar(Long id);
    void exigirBorrador(Inspeccion i);
    InspeccionResponse toResponse(Inspeccion i, boolean conDetalles);

}
