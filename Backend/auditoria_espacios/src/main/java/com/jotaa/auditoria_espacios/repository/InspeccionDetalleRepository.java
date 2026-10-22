package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entity.InspeccionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspeccionDetalleRepository extends JpaRepository<InspeccionDetalle, Long> {
    List<InspeccionDetalle> findByInspeccionId(Long inspeccionId);
    Optional<InspeccionDetalle> findByInspeccionIdAndPuestoId(Long inspeccionId, Long puestoId);
    long countByInspeccionId(Long inspeccionId);
}
