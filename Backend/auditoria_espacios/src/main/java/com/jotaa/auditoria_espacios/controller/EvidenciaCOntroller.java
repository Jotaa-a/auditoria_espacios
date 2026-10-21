package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.response.EvidenciaResponse;
import com.jotaa.auditoria_espacios.service.EvidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvidenciaCOntroller {

    private final EvidenciaService service;

    @PostMapping(path = "/hallazgos/{id}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaResponse subirHallazgo(@PathVariable Long id,
                                           @RequestParam("archivo") MultipartFile archivo){
        return service.subirAHallazgo(id, archivo);
    }

    @PostMapping(path = "/reportes-dano/{id}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaResponse subirAReporte(@PathVariable Long id,
                                           @RequestParam("archivo") MultipartFile archivo){
        return service.subirAReporte(id, archivo);
    }

    @GetMapping("/hallazgos/{id}/evidencias")
    public List<EvidenciaResponse> lisatrDeHallazgo(@PathVariable Long id){
        return service.listarDeHallazgo(id);
    }

    @GetMapping("/evidencias/{id}/archivo")
    public ResponseEntity<Resource> archivo(@PathVariable Long id){
        Resource recurso = service.cargarArchivo(id);
        MediaType tipo = MediaTypeFactory.getMediaType(recurso).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(tipo).body(recurso);
    }
}
