package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.response.AuditoriaResponse;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import org.springframework.data.web.PagedModel;

import java.time.LocalDate;
import java.util.Map;

public interface AuditoriaService {
    void registrar(Accion accion, String entidad, Long entidadId, Map<String, Object> detalle);
    void registrarIndependiente (Long usuarioId, Accion accion, String entidad,
                                 Long entidadId, Map<String, Object> detalle);
    PagedModel<AuditoriaResponse> buscar(String entidad, Long usuarioId,
                                         LocalDate desde, LocalDate hasta, int page, int size);
}
