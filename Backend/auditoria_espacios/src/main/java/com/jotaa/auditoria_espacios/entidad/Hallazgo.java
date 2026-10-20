package com.jotaa.auditoria_espacios.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hallazgo")
@Getter @Setter @NoArgsConstructor
public class Hallazgo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_id", nullable = false)
    private InspeccionDetalle detalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_hallazgo_id", nullable = false)
    private TipoHallazgo tipoHallazgo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componente_id")
    private Componente componente;

    @Column(length = 500)
    private String descripcion;
}
