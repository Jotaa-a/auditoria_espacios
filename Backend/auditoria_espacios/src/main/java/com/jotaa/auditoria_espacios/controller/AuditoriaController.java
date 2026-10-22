package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.response.AuditoriaResponse;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService service;

    @GetMapping
    public PagedModel<AuditoriaResponse> buscar(
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ){
        return service.buscar(entidad, usuarioId, desde, hasta, page, size);
    }
}
