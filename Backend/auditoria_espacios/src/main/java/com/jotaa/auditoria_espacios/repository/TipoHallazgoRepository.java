package com.jotaa.auditoria_espacios.repository;

import com.jotaa.auditoria_espacios.entity.TipoHallazgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoHallazgoRepository extends JpaRepository<TipoHallazgo, Long> {
}
