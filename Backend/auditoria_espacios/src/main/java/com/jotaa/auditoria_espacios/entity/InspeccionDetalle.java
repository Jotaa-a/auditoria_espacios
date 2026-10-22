package com.jotaa.auditoria_espacios.entity;

import com.jotaa.auditoria_espacios.entity.enumerated.Resultado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inspeccion_detalle",
    uniqueConstraints = @UniqueConstraint(columnNames = {"inspeccion_id", "puesto_id"}))
@Getter @Setter @NoArgsConstructor
public class InspeccionDetalle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspeccion_id", nullable = false)
    private Inspeccion inspeccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private PuestoTrabajo puesto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Resultado resultado;

    @OneToMany(mappedBy = "detalle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hallazgo> hallazgos = new ArrayList<>();
}
