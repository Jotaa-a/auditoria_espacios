package com.jotaa.auditoria_espacios.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puesto_trabajo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"espacio_id", "codigo"}))
@Getter @Setter @NoArgsConstructor
public class PuestoTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false)
    private Boolean activo = true;
}
