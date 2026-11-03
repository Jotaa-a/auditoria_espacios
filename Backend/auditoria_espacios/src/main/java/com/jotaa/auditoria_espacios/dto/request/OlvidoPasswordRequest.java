package com.jotaa.auditoria_espacios.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OlvidoPasswordRequest (
        @NotBlank @Email String correo
){
}
