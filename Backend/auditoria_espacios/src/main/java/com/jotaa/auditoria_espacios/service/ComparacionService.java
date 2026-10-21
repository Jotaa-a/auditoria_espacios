package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.response.ComparacionResponse;

public interface ComparacionService {
    ComparacionResponse comparar(Long lunesId, Long viernesId);
}
