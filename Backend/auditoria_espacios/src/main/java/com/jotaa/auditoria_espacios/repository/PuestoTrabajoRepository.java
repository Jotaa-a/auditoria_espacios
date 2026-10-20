package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entidad.PuestoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoTrabajoRepository extends JpaRepository<PuestoTrabajo, Long> {
    List<PuestoTrabajo> findByEspacioId(Long espacioId);
    boolean existsByEspacioIdAndCodigo(Long espacioId, String codigo);
}
