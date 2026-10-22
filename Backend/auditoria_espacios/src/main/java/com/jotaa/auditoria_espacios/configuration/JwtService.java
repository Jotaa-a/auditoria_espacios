package com.jotaa.auditoria_espacios.configuration;

import com.jotaa.auditoria_espacios.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    public record Token(String valor, Instant expiraEn){}

    private final JwtEncoder encoder;
    private final long minutos;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.jwt.expiracion-minutos}") long minutos){
        this.encoder = encoder;
        this.minutos = minutos;
    }

    public Token generar(Usuario u){
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutos, ChronoUnit.MINUTES);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auditoria-espacios")
                .issuedAt(ahora)
                .expiresAt(expira)
                .subject(u.getId().toString())
                .claim("rol", u.getRol().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String valor = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new Token(valor, expira);
    }

}
