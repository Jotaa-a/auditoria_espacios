package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.request.PuestoRequest;
import com.jotaa.auditoria_espacios.dto.response.PuestoResponse;
import com.jotaa.auditoria_espacios.service.PuestoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puestos")
@RequiredArgsConstructor
public class PuestoTrabajoController {

    private final PuestoService service;

    @GetMapping
    public List<PuestoResponse> listar(@RequestParam(required = false) Long espacioId){
        return service.listar(espacioId);
    }

    @GetMapping("/{id}")
    public PuestoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PuestoResponse crear(@Valid @RequestBody PuestoRequest req){
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public PuestoResponse actualizar (@PathVariable Long id, @Valid @RequestBody PuestoRequest req){
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar (@PathVariable Long id){
        service.desactivar(id);
    }
}
