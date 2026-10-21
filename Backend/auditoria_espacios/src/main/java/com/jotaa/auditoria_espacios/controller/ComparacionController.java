package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.response.ComparacionResponse;
import com.jotaa.auditoria_espacios.service.ComparacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspecciones")
@RequiredArgsConstructor
public class ComparacionController {

    private final ComparacionService service;

    @GetMapping("/{lunesId}/comparacion")
    public ComparacionResponse comparar(@PathVariable Long lunesId,
                                        @RequestParam(required = false) Long viernesId){
        return service.comparar(lunesId, viernesId);
    }
}
