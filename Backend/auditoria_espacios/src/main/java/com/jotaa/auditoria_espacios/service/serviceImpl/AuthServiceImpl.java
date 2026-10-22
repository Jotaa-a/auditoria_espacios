package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.configuration.JwtService;
import com.jotaa.auditoria_espacios.dto.request.LoginRequest;
import com.jotaa.auditoria_espacios.dto.response.LoginResponse;
import com.jotaa.auditoria_espacios.dto.response.UsuarioResponse;
import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import com.jotaa.auditoria_espacios.exception.CredencialesInvalidasException;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import com.jotaa.auditoria_espacios.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuditoriaService auditoria;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        String correo = req.correo().trim().toLowerCase();
        Optional<Usuario> encontrado = usuarioRepo.findByCorreo(correo).filter(Usuario::getActivo);
        Usuario u = encontrado
                .filter(x -> encoder.matches(req.password(), x.getPasswordHash()))
                .orElse(null);

        if (u == null) {
            Long id = encontrado.map(Usuario::getId).orElse(null);
            auditoria.registrarIndependiente(id, Accion.LOGIN_FALLIDO, "usuario", id,
                    Map.of("correo", correo));
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }
        JwtService.Token t = jwtService.generar(u);
        auditoria.registrarIndependiente(u.getId(), Accion.LOGIN, "usuario", u.getId(), Map.of());
        return new LoginResponse(t.valor(), "Bearer", t.expiraEn(), UsuarioResponse.from(u));
    }
}
