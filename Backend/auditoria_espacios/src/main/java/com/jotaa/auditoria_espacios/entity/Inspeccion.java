package com.jotaa.auditoria_espacios.entity;

import com.jotaa.auditoria_espacios.entity.enumerated.EstadoInspeccion;
import com.jotaa.auditoria_espacios.entity.enumerated.TipoInspeccion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspeccion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"espacio_id", "tipo", "fecha"}))
@Getter @Setter @NoArgsConstructor
public class Inspeccion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInspeccion tipo;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspector_id", nullable = false)
    private Usuario inspector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInspeccion estado = EstadoInspeccion.BORRADOR;

    @Column(columnDefinition = "text")
    private String observaciones;

    @Column(name = "creada_en", nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @PrePersist
    void alClear() { creadaEn = LocalDateTime.now();}
}
