package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entidad.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {
    boolean existsByNombre(String nombre);
}
