package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.configuration.UsuarioActual;
import com.jotaa.auditoria_espacios.dto.request.*;
import com.jotaa.auditoria_espacios.dto.response.UsuarioResponse;
import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import com.jotaa.auditoria_espacios.entity.enumerated.Rol;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import com.jotaa.auditoria_espacios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
    private final UsuarioActual usuarioActual;
    private final AuditoriaService auditoria;
    private final JavaMailSender mailSender;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepo.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.from(buscar(id));
    }

    @Override
    public UsuarioResponse crear(UsuarioCrearRequest req) {
        String correo = normalizar(req.correo());
        if (usuarioRepo.existsByCorreo(correo))
            throw new ConflictoException("Ya existe un usuario con ese correo");
        Usuario u = new Usuario();
        u.setNombre(req.nombre());
        u.setCorreo(correo);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRol(req.rol());
        u.setActivo(true);
        Usuario g = usuarioRepo.save(u);
        auditoria.registrar(Accion.CREAR, "usuario", g.getId(),
                Map.of("correo", g.getCorreo(), "rol", g.getRol().name()));
        return UsuarioResponse.from(g);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizarRequest req) {
        Usuario u = buscar(id);
        String correo = normalizar(req.correo());
        if (!u.getCorreo().equals(correo) && usuarioRepo.existsByCorreo(correo))
            throw new ConflictoException("Ya existe un usuario con ese correo");
        if (usuarioActual.obtener().getId().equals(id)
            && (!req.activo() || req.rol() != Rol.ADMIN))
            throw new ConflictoException(
                    "No puedes desactivarte ni quitarte el rol de administrador a ti mismo"
            );
        u.setNombre(req.nombre());
        u.setCorreo(correo);
        u.setRol(req.rol());
        u.setActivo(req.activo());
        auditoria.registrar(Accion.ACTUALIZAR, "usuario", id,
                Map.of("correo", u.getCorreo(), "rol", u.getRol().name(), "activo", u.getActivo()));
        return UsuarioResponse.from(u);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest req) {
        buscar(id).setPasswordHash(encoder.encode(req.password()));
        auditoria.registrar(Accion.CAMBIAR_PASSWORD, "usuario", id, Map.of());
    }

    @Override
    @Transactional
    public void cambiarPasswordPropia(Long usuarioId, CambiarPasswordPropiaRequest req){
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (!encoder.matches(req.passwordActual(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        usuario.setPasswordHash(encoder.encode(req.nuevaPassword()));
        usuarioRepo.save(usuario);
    }

    @org.springframework.beans.factory.annotation.Value("${MAIL_REMITENTE}")
    private String mailRemitente;

    @Override
    @Transactional
    public void solicitarRecuperacionPassword(OlvidoPasswordRequest req){
        Usuario usuario = usuarioRepo.findByCorreo(req.correo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Correo no registrado"));

        String token = java.util.UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);

        usuario.setExpiracionToken(java.time.LocalDateTime.now().plusHours(1));
        usuarioRepo.save(usuario);

        try {
            org.springframework.mail.SimpleMailMessage mensaje = new org.springframework.mail.SimpleMailMessage();
            mensaje.setFrom(mailRemitente);
            mensaje.setTo(usuario.getCorreo());
            mensaje.setSubject("Recuperación de contraseña - Auditoria de Espacios");
            mensaje.setText("Hola" + usuario.getNombre() + ", \n\n" +
                    "Has solicitado restablecer tu contraseña. Utiliza el siguiente token de recuperacion:\n\n" +
                    token + "\n\n" +
                    "Este token expira en 1 hora. \n\n" +
                    "si tú no solicitaste esto, puedes ignorar este mensaje.");
            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo de recuperación: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void restablecerPasswordConToken(RestablecerPasswordTokenRequest req) {

        System.out.println("token:");
        System.out.println(req.token());
        System.out.println("-----------------");
        Usuario usuario = usuarioRepo.findByTokenRecuperacion(req.token())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        if (usuario.getExpiracionToken() == null || usuario.getExpiracionToken().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("El token de recuperacion ha expirado");
        }

        usuario.setPasswordHash(encoder.encode(req.nuevaPassword()));
        usuario.setTokenRecuperacion(null);
        usuario.setExpiracionToken(null);
        usuarioRepo.save(usuario);
    }
    private Usuario buscar(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    private String normalizar(String correo){
        return correo.trim().toLowerCase();
    }
}
