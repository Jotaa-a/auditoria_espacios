package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.configuration.UsuarioActual;
import com.jotaa.auditoria_espacios.dto.request.LoginRequest;
import com.jotaa.auditoria_espacios.dto.response.LoginResponse;
import com.jotaa.auditoria_espacios.dto.response.UsuarioResponse;
import com.jotaa.auditoria_espacios.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioActual usuarioActual;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req){
        return authService.login(req);
    }

    @GetMapping("/me")
    public UsuarioResponse me() {
        return UsuarioResponse.from(usuarioActual.obtener());
    }
}
