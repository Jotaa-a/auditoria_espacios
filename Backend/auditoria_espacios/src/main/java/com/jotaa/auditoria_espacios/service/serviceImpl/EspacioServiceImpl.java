package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.request.EspacioRequest;
import com.jotaa.auditoria_espacios.dto.response.EspacioResponse;
import com.jotaa.auditoria_espacios.entity.Espacio;
import com.jotaa.auditoria_espacios.entity.Usuario;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.EspacioRepository;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import com.jotaa.auditoria_espacios.service.EspacioService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EspacioServiceImpl implements EspacioService {

    private final EspacioRepository espacioRepo;
    private final UsuarioRepository usuarioRepo;
    private final AuditoriaService auditoria;

    @Override
    @Transactional(readOnly = true)
    public List<EspacioResponse> listar() {
        return espacioRepo.findAll().stream().map(EspacioResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EspacioResponse obtener(Long id) {
        return EspacioResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public EspacioResponse crear(EspacioRequest req) {
        if (espacioRepo.existsByNombre(req.nombre()))
            throw new ConflictoException("Ya existe un espacio con ese nombre");
        Espacio e = new Espacio();
        aplicar(e, req);
        Espacio g = espacioRepo.save(e);
        auditoria.registrar(Accion.CREAR, "espacio", g.getId(), Map.of("nombre", g.getNombre()));
        return EspacioResponse.from(g);
    }

    @Override
    @Transactional
    public EspacioResponse actualizar(Long id, EspacioRequest req) {
        Espacio e = buscar(id);
        if (!e.getNombre().equals(req.nombre()) && espacioRepo.existsByNombre(req.nombre()))
            throw new ConflictoException("Ya existe un espacio con ese nombre");
        String antes = e.getNombre();
        aplicar(e, req);
        auditoria.registrar(Accion.ACTUALIZAR, "espacio", id,
                Map.of("nombreAntes", antes, "nombreDespues", e.getNombre()));
        return EspacioResponse.from(e);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Espacio e = buscar(id);
        espacioRepo.delete(e);
        auditoria.registrar(Accion.ELIMINAR, "espacio", id,
                Map.of("nombre", e.getNombre()));
    }

    @Override
    public Espacio buscar(Long id) {
        return espacioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado"));
    }

    @Override
    public void aplicar(Espacio e, EspacioRequest req) {
        e.setNombre(req.nombre());
        if(req.representanteId() == null) {
            e.setRepresentante(null);
        } else {
            e.setRepresentante(usuarioRepo.findById(req.representanteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Usuario no encontrado: " + req.representanteId()
                    )));
        }
    }

    @Override
    public int procesarExel(MultipartFile file) throws IOException {
        int contador = 0;
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)){
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null) continue;

                Cell cellNombre = row.getCell(0);
                if(cellNombre == null) continue;
                String nombre = cellNombre.getStringCellValue().trim();

                Cell cellRepresentante = row.getCell(1);
                if (cellRepresentante == null) continue;

                Long idRepresentante = obtenerLongDeCelda(cellRepresentante);
                if(idRepresentante == null) continue;

                Usuario representante = usuarioRepo.findById(idRepresentante).orElse(null);

                Espacio espacio = new Espacio();
                espacio.setNombre(nombre);
                espacio.setRepresentante(representante);

                espacioRepo.save(espacio);
                contador++;
            }
        }
        return contador;
    }

    private Long obtenerLongDeCelda(Cell cell){
        if (cell.getCellType() == CellType.NUMERIC){
            return (long) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.parseLong(cell.getStringCellValue().trim());
            } catch(NumberFormatException e){
                return null;
            }
        }

        return null;
    }
}
