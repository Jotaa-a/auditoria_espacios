package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entity.AuditoriaLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;

@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

    @Query("""
        select a from AuditoriaLog a
        where (:entidad is null or a.entidad = :entidad)
          and (:usuarioId is null or a.usuario.id = :usuarioId)
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha < :hasta)
        """)
    Page<AuditoriaLog> buscar(@Param("entidad") String entidad,
                              @Param("usuarioId") Long usuarioId,
                              @Param("desde") LocalDateTime desde,
                              @Param("hasta") LocalDateTime hasta,
                              Pageable pageable);
}
