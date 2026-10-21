package com.jotaa.auditoria_espacios.entidad.enumerated;

import com.jotaa.auditoria_espacios.entidad.Hallazgo;
import com.jotaa.auditoria_espacios.entidad.ReporteDano;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidencia")
@Getter @Setter @NoArgsConstructor
public class Evidencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ruta_archivo", nullable = false)
    private String rutaArchivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hallazgo_id")
    private Hallazgo hallazgo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporte_id")
    private ReporteDano reporte;

    @Column(name = "subida_en", nullable = false, updatable = false)
    private LocalDateTime subidaEn;

    @PrePersist
    void alCrear() {
        subidaEn = LocalDateTime.now();
    }
}
