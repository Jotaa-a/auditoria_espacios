package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.configuration.JwtService;
import com.jotaa.auditoria_espacios.dto.request.LoginRequest;
import com.jotaa.auditoria_espacios.dto.response.LoginResponse;
import com.jotaa.auditoria_espacios.dto.response.UsuarioResponse;
import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.exception.CredencialesInvalidasException;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import com.jotaa.auditoria_espacios.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarioRepo.findByCorreo(req.correo().trim().toLowerCase())
                .filter(Usuario::getActivo)
                .filter(x -> encoder.matches(req.password(), x.getPasswordHash()))
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));
        JwtService.Token t = jwtService.generar(u);
        return new LoginResponse(t.valor(), "Bearer", t.expiraEn(), UsuarioResponse.from(u));
    }
}
