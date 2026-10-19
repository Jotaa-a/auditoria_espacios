package com.jotaa.auditoria_espacios.entidad;

import com.jotaa.auditoria_espacios.entidad.enumerated.TipoComponente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "componente")
@Getter @Setter @NoArgsConstructor
public class Componente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private PuestoTrabajo puesto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComponente tipo;

    @Column(length = 80)
    private String serial;

    @Column(length = 80)
    private String descripcion;
}
