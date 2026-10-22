package com.jotaa.auditoria_espacios.configuration;

import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.entity.enumerated.Rol;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInicial implements ApplicationRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final String correo;
    private final String password;

    public AdminInicial(UsuarioRepository repo, PasswordEncoder encoder,
                        @Value("${app.admin.correo}") String correo,
                        @Value("${app.admin.password}") String password){
        this.repo = repo;
        this.encoder = encoder;
        this.correo = correo;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (correo.isBlank() || password.isBlank() || repo.existsByRol(Rol.ADMIN)) return;
        Usuario u = new Usuario();
        u.setNombre("Administrador");
        u.setCorreo(correo.trim().toLowerCase());
        u.setPasswordHash(encoder.encode(password));
        u.setRol(Rol.ADMIN);
        u.setActivo(true);
        repo.save(u);
    }
}
