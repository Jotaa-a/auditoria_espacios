package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.request.CambiarPasswordRequest;
import com.jotaa.auditoria_espacios.dto.request.UsuarioActualizarRequest;
import com.jotaa.auditoria_espacios.dto.request.UsuarioCrearRequest;
import com.jotaa.auditoria_espacios.dto.response.UsuarioResponse;
import com.jotaa.auditoria_espacios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public List<UsuarioResponse> listar(){ return service.listar(); }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id){
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody UsuarioCrearRequest req){
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody UsuarioActualizarRequest req){
        return service.actualizar(id, req);
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(@PathVariable Long id,
                                @Valid @RequestBody CambiarPasswordRequest req){
        service.cambiarPassword(id, req);
    }
}
