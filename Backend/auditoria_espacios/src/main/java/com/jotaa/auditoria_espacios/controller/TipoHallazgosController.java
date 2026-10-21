package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.response.TipoHallazgoResponse;
import com.jotaa.auditoria_espacios.repository.TipoHallazgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-hallazgos")
@RequiredArgsConstructor
public class TipoHallazgosController {

    private final TipoHallazgoRepository repo;

    @GetMapping
    public List<TipoHallazgoResponse> listar() {
        return repo.findAll().stream().map(TipoHallazgoResponse::from).toList();
    }
}
