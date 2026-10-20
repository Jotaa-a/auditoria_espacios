package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.request.ComponenteRequest;
import com.jotaa.auditoria_espacios.dto.response.ComponenteResponse;
import com.jotaa.auditoria_espacios.service.ComponenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/componentes")
@RequiredArgsConstructor
public class ComponenteController {

    private final ComponenteService service;

    @GetMapping
    public List<ComponenteResponse> listar(@RequestParam(required = false) Long puestoId){
        return service.listar(puestoId);
    }

    @GetMapping("/{id}")
    public ComponenteResponse obtener(@PathVariable Long id){
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComponenteResponse crear (@Valid @RequestBody ComponenteRequest req){
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public ComponenteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ComponenteRequest req){
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id){
        service.eliminar(id);
    }


}
