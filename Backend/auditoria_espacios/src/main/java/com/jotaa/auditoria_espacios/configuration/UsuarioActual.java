package com.jotaa.auditoria_espacios.configuration;

import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.exception.CredencialesInvalidasException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.net.Authenticator;

@Component
@RequiredArgsConstructor
public class UsuarioActual {

    private final UsuarioRepository usuarioRepo;

    public Usuario obtener() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof  Jwt jwt))
            throw new CredencialesInvalidasException("No auntenticado");
        return usuarioRepo.findById(Long.valueOf(jwt.getSubject()))
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no valido"));
    }
}
