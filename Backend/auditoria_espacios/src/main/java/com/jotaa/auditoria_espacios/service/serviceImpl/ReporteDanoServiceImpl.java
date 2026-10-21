package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.request.ReporteRequest;
import com.jotaa.auditoria_espacios.dto.response.EvidenciaResponse;
import com.jotaa.auditoria_espacios.dto.response.ReporteResponse;
import com.jotaa.auditoria_espacios.entidad.Componente;
import com.jotaa.auditoria_espacios.entidad.ReporteDano;
import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoReporte;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.*;
import com.jotaa.auditoria_espacios.service.ReporteDanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteDanoServiceImpl implements ReporteDanoService {

    private final ReporteDanoRepository reporteRepo;
    private final ComponenteRepository componenteRepo;
    private final UsuarioRepository usuarioRepo;
    private final InspeccionRepository inspeccionRepo;
    private final EvidenciaRepository evidenciaRepo;

    @Override
    @Transactional
    public ReporteResponse crear(ReporteRequest req) {
        ReporteDano r = new ReporteDano();
        r.setComponente(componenteRepo.findById(req.componenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Componente no encontrado: " + req.componenteId())));
        r.setReportadoPor(usuarioRepo.findById(req.reportadoPorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + req.reportadoPorId())));
        if (req.inspeccionID() != null){
            r.setInspeccion(inspeccionRepo.findById(req.inspeccionID())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inpeccion no encontrado: " + req.inspeccionID())));
        }
        r.setDescripcion(req.descripcion());
        r.setGravedad(req.gravedad());
        return toResponse(reporteRepo.save(r));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponse> listar(EstadoReporte estado, Long espacioId) {
        return reporteRepo.buscar(estado, espacioId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponse cambiarEstado(Long id, EstadoReporte nuevo) {
        ReporteDano r = buscar(id);
        if(nuevo.ordinal() <= r.getEstado().ordinal())
            throw new ConflictoException("No se puede pasar de " + r.getEstado() + " a " + nuevo);
        r.setEstado(nuevo);
        if (nuevo == EstadoReporte.RESUELTO) r.setResueltoEn(LocalDateTime.now());
        return toResponse(r);
    }

    @Override
    public ReporteDano buscar(Long id) {
        return reporteRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado: " + id));
    }

    @Override
    public ReporteResponse toResponse(ReporteDano r) {
        Componente c = r.getComponente();
        List<EvidenciaResponse> ev = evidenciaRepo.findByReporteId(r.getId())
                .stream().map(EvidenciaResponse::from).toList();
        return new ReporteResponse(r.getId(), c.getId(), c.getTipo(), c.getPuesto().getCodigo(),
                c.getPuesto().getEspacio().getNombre(),
                r.getReportadoPor().getId(), r.getReportadoPor().getNombre(),
                r.getInspeccion() != null ? r.getInspeccion().getId() : null,
                r.getDescripcion(), r.getGravedad(), r.getEstado(),
                r.getCreadoEn(), r.getResueltoEn(), ev);
    }

    @Override
    public ReporteResponse obtener(Long id) {
        return toResponse(buscar(id));
    }
}
