package com.jotaa.auditoria_espacios.controller;

import com.jotaa.auditoria_espacios.dto.request.EspacioRequest;
import com.jotaa.auditoria_espacios.dto.response.EspacioResponse;
import com.jotaa.auditoria_espacios.service.EspacioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/espacios")
@RequiredArgsConstructor
public class EspacioController {
    @Autowired
    private final EspacioService service;


    @PostMapping("/carga-masiva")
    public ResponseEntity<?> cargarExel(@RequestParam("file")MultipartFile file){
        if (file.isEmpty()){
            return ResponseEntity.badRequest().body("Por favor, sube un archivo válido.");
        }

        try {
            int registroCreados = service.procesarExel(file);
            return ResponseEntity.ok("Se importaron exitosamente " + registroCreados + " espacios");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesas el archivo: " + e.getMessage());
        }
    }

    @GetMapping
    public List<EspacioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EspacioResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EspacioResponse crear(@Valid @RequestBody EspacioRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public EspacioResponse actualizar(@PathVariable Long id, @Valid @RequestBody EspacioRequest req){
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
