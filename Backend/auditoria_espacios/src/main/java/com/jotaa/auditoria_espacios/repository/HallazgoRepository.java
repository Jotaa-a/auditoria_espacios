package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entidad.enumerated.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallazgoRepository extends JpaRepository<Evidencia, Long> {
}
