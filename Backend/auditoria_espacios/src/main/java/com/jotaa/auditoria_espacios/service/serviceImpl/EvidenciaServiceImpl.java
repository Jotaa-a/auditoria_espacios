package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.response.EvidenciaResponse;
import com.jotaa.auditoria_espacios.entidad.Hallazgo;
import com.jotaa.auditoria_espacios.entidad.ReporteDano;
import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoInspeccion;
import com.jotaa.auditoria_espacios.entidad.Evidencia;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.exception.SolicitudInvalidaException;
import com.jotaa.auditoria_espacios.repository.EvidenciaRepository;
import com.jotaa.auditoria_espacios.repository.HallazgoRepository;
import com.jotaa.auditoria_espacios.repository.ReporteDanoRepository;
import com.jotaa.auditoria_espacios.service.EvidenciaService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EvidenciaServiceImpl implements EvidenciaService {

    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp"
    );

    private final Path base;
    private final EvidenciaRepository evidenciaRepo;
    private final HallazgoRepository hallazgoRepo;
    private final ReporteDanoRepository reporteRepo;

    public EvidenciaServiceImpl(@Value("${app.uploads.dir}") String dir,
                            EvidenciaRepository evidenciaRepo,
                            HallazgoRepository hallazgoRepo,
                            ReporteDanoRepository reporteRepo) throws IOException{
        this.base = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(this.base);
        this.evidenciaRepo = evidenciaRepo;
        this.hallazgoRepo = hallazgoRepo;
        this.reporteRepo = reporteRepo;
    }

    @Override
    @Transactional
    public EvidenciaResponse subirAHallazgo(Long hallazgoId, MultipartFile archivo) {
        Hallazgo h = hallazgoRepo.findById(hallazgoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Hallazgo no encontrado: " + hallazgoId));
        if (h.getDetalle().getInspeccion().getEstado() == EstadoInspeccion.CERRADA)
            throw new ConflictoException("La inspeccion está cerrada y no admite más evidencias");
        Evidencia e = new Evidencia();
        e.setHallazgo(h);
        e.setRutaArchivo(almacenar(archivo));
        return EvidenciaResponse.from(evidenciaRepo.save(e));
    }

    @Override
    @Transactional
    public EvidenciaResponse subirAReporte(Long reporteId, MultipartFile archivo) {
        ReporteDano r = reporteRepo.findById(reporteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado: " + reporteId));
        Evidencia e = new Evidencia();
        e.setReporte(r);
        e.setRutaArchivo(almacenar(archivo));
        return EvidenciaResponse.from(evidenciaRepo.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaResponse> listarDeHallazgo(Long hallazgoId) {
        return evidenciaRepo.findByHallazgoId(hallazgoId).stream().map(EvidenciaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Resource cargarArchivo(Long id) {
        Evidencia e = evidenciaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evidencia no encontrada: " + id));
        Path ruta = base.resolve(e.getRutaArchivo()).normalize();
        Resource recurso = new FileSystemResource(ruta);
        if (!ruta.startsWith(base) || !recurso.exists())
            throw new RecursoNoEncontradoException("Archivo no encontrado");
        return recurso;
    }

    @Override
    public String almacenar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty())
            throw new SolicitudInvalidaException("Deves enviar un archivo");
        String extension = EXTENSIONES.get(archivo.getContentType());
        if (extension == null)
            throw new SolicitudInvalidaException("Solo se permiten imágenes JPG, PNG o WEPB");
        String nombre = UUID.randomUUID() + extension;
        try (InputStream in = archivo.getInputStream()) {
            Files.copy(in, base.resolve(nombre));
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo guardar el archivo", ex);
        }
        return nombre;
    }
}
