package com.jotaa.auditoria_espacios.service;

import com.jotaa.auditoria_espacios.dto.request.LoginRequest;
import com.jotaa.auditoria_espacios.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest req);
}
