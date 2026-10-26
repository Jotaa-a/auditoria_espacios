package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.request.DetallePuestoRequest;
import com.jotaa.auditoria_espacios.dto.request.InspeccionRequest;
import com.jotaa.auditoria_espacios.dto.response.DetalleResponse;
import com.jotaa.auditoria_espacios.dto.response.InspeccionResponse;
import com.jotaa.auditoria_espacios.service.InspeccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inspecciones")
@RequiredArgsConstructor
public class InspeccionController {

    private final InspeccionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InspeccionResponse crear (@Valid @RequestBody InspeccionRequest req){
        return service.crear(req);
    }

    @GetMapping
    public List<InspeccionResponse> listar(@RequestParam(required = false) Long espacioId,
                                           @RequestParam(required = false) LocalDate fecha){
        return service.listar(espacioId, fecha);
    }

    @GetMapping("/{id}")
    public InspeccionResponse obtener(@PathVariable Long id){
        return service.obtener(id);
    }

    @PutMapping("/{id}/puestos/{puestoId}")
    public DetalleResponse registrarPuesto(@PathVariable Long id, @PathVariable Long puestoId,
                                           @Valid @RequestBody DetallePuestoRequest req){
        return service.registrarPuesto(id, puestoId, req);
    }

    @PostMapping("/{id}/cerrar")
    public InspeccionResponse cerrar(@PathVariable Long id){
        return service.cerrar(id);
    }
}
