package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.entity.enumerated.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByRol(Rol rol);
}
