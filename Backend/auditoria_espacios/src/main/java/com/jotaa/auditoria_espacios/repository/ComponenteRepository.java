package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entidad.Componente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long> {
    List<Componente> findByPuestoId(Long puestoId);
}
