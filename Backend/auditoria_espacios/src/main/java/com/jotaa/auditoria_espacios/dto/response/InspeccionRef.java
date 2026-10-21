package com.jotaa.auditoria_espacios.dto.response;

import java.time.LocalDate;

public record InspeccionRef(
        Long id,
        LocalDate fecha,
        String inspectorNombre
) {
}
