package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.response.EvidenciaResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EvidenciaService {
    EvidenciaResponse subirAHallazgo(Long hallazgoId, MultipartFile archivo);
    EvidenciaResponse subirAReporte(Long reporteId, MultipartFile archivo);
    List<EvidenciaResponse> listarDeHallazgo(Long hallazgoId);
    Resource cargarArchivo(Long id);
    String almacenar(MultipartFile archivo);

}
