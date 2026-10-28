package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inspecciones")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id){
        byte[] pdf = service.generarPdfInspeccion(id);
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_inspeccion_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/{id}/enviar-correo")
    public ResponseEntity<Map<String, String>> enviarCorreo(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");
        service.enviarReportePorCorreo(id, correo);

        // Devolvemos un JSON válido para que el cliente lo lea sin problemas
        return ResponseEntity.ok(Map.of("mensaje", "Correo enviado con éxito"));
    }

}
