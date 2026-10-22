package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entity.ReporteDano;
import com.jotaa.auditoria_espacios.entity.enumerated.EstadoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteDanoRepository extends JpaRepository<ReporteDano, Long> {
    @Query("""
        select r from ReporteDano r
        where (:estado is null or r.estado = :estado)
          and (:espacioId is null or r.componente.puesto.espacio.id = :espacioId)
        order by r.creadoEn desc
        """)
    List<ReporteDano> buscar(@Param("estado")EstadoReporte esatdo,
                             @Param("espacioId") Long espacioId);
}
