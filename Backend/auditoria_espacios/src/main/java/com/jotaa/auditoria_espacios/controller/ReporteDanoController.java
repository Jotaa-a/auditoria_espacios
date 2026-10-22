package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.request.EstadoReporteRequest;
import com.jotaa.auditoria_espacios.dto.request.ReporteRequest;
import com.jotaa.auditoria_espacios.dto.response.ReporteResponse;
import com.jotaa.auditoria_espacios.entity.enumerated.EstadoReporte;
import com.jotaa.auditoria_espacios.service.ReporteDanoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes-dano")
@RequiredArgsConstructor
public class ReporteDanoController {

    private final ReporteDanoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteResponse crear(@Valid @RequestBody ReporteRequest req){
        return service.crear(req);
    }

    @GetMapping
    public List<ReporteResponse> listar(@RequestParam(required = false)EstadoReporte estado,
                                        @RequestParam(required = false) Long espacioId){
        return  service.listar(estado, espacioId);
    }

    @GetMapping("/{id}")
    public ReporteResponse obtener(@PathVariable Long id){
        return service.obtener(id);
    }

    @PatchMapping("/{id}/estado")
    public ReporteResponse cambiarEstado(@PathVariable Long id,
                                         @Valid @RequestBody EstadoReporteRequest req){
        return service.cambiarEstado(id, req.estado());
    }
}
