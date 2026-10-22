package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entity.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {
    List<Evidencia> findByHallazgoId(Long hallazgoId);
    List<Evidencia> findByReporteId(Long reporteId);
}
