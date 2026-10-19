package com.jotaa.auditoria_espacios.entidad;

import com.jotaa.auditoria_espacios.entidad.enumerated.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter @Setter @NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;
}
