package com.jotaa.auditoria_espacios.configuration;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/me").permitAll()
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // catálogos: todos pueden consultar, solo admin modifica
                        .requestMatchers(HttpMethod.GET, "/api/espacios/**", "/api/puestos/**",
                                "/api/componentes/**", "/api/tipos-hallazgo/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/espacios/**", "/api/puestos/**",
                                "/api/componentes/**").hasRole("ADMIN")

                        //inspeccion y comparacion
                        .requestMatchers(HttpMethod.GET, "/api/inspecciones/**").authenticated()
                        .requestMatchers("/api/inspecciones/**").hasAnyRole("ADMIN", "TECNICO")

                        // evidencias
                        .requestMatchers(HttpMethod.POST, "/api/hallazgos/*/evidencias").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/hallazgos/**", "/api/ecidencias/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reportes-dano/*/evidencias").authenticated()

                        // reportes de daño
                        .requestMatchers(HttpMethod.GET, "/api/reportes-dano/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reportes-dano").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/reportes-dano/*/estado").hasAnyRole("ADMIN", "TECNICO")

                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // lo demás queda bloqueado
                        .anyRequest().denyAll())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(convertidor())));
            return http.build();
    }

    private JwtAuthenticationConverter convertidor() {
        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("rol");
        autoridades.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter c = new JwtAuthenticationConverter();
        c.setJwtGrantedAuthoritiesConverter(autoridades);
        return c;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecretKey jwtKey(@Value("${app.jwt.secret}") String secreto) {
        return new SecretKeySpec(
                Base64.getDecoder().decode(secreto.trim()),
                "HmacSHA256"
        );
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.origins}")List<String> origenes
            ){
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(origenes);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", cfg);
        return fuente;
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtKey) {
        return NimbusJwtDecoder
                .withSecretKey(jwtKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
