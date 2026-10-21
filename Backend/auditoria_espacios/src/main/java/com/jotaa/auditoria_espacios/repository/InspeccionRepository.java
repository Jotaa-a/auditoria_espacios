package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entidad.Inspeccion;
import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoInspeccion;
import com.jotaa.auditoria_espacios.entidad.enumerated.TipoInspeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InspeccionRepository extends JpaRepository<Inspeccion, Long> {
    boolean existsByEspacioIdAndTipoAndFecha(Long espacioId, TipoInspeccion tipo, LocalDate fecha);

    @Query("""
        select i from Inspeccion i
        where (:espacioId is null or i.espacio.id = :espacioId)
            and (:fecha is null or i.fecha = :fecha)
        order by i.fecha desc, i.id desc
        """)
    List<Inspeccion> buscar(@Param("espacioId") Long espacioId, @Param("fecha") LocalDate fecha);
    Optional<Inspeccion> findFirstByEspacioIdAndTipoAndEstadoAndFechaLessThanOrderByFechaDesc(
            Long espacioId, TipoInspeccion tipo, EstadoInspeccion estado, LocalDate fecha
    );
}
