package com.jotaa.auditoria_espacios.dto.response;

import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.entity.enumerated.Rol;

public record UsuarioResponse(Long id, String nombre, String correo, Rol rol, Boolean activo) {
    public static UsuarioResponse from(Usuario u){
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol(), u.getActivo());
    }
}
