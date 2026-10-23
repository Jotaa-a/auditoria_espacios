package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.configuration.UsuarioActual;
import com.jotaa.auditoria_espacios.dto.request.DetallePuestoRequest;
import com.jotaa.auditoria_espacios.dto.request.HallazgoRequest;
import com.jotaa.auditoria_espacios.dto.request.InspeccionRequest;
import com.jotaa.auditoria_espacios.dto.response.DetalleResponse;
import com.jotaa.auditoria_espacios.dto.response.InspeccionResponse;
import com.jotaa.auditoria_espacios.entity.*;
import com.jotaa.auditoria_espacios.entity.enumerated.Accion;
import com.jotaa.auditoria_espacios.entity.enumerated.Categoria;
import com.jotaa.auditoria_espacios.entity.enumerated.EstadoInspeccion;
import com.jotaa.auditoria_espacios.entity.enumerated.Resultado;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.exception.SolicitudInvalidaException;
import com.jotaa.auditoria_espacios.repository.*;
import com.jotaa.auditoria_espacios.service.AuditoriaService;
import com.jotaa.auditoria_espacios.service.InspeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InspeccionServiceImpl implements InspeccionService {

    private final InspeccionRepository inspeccionRepo;
    private final InspeccionDetalleRepository detalleRepo;
    private final EspacioRepository espacioRepo;
    private final PuestoTrabajoRepository puestoRepo;
    private final ComponenteRepository componenteRepo;
    private final TipoHallazgoRepository tipoRepo;
    private final ReporteDanoRepository reporteRepo;
    private final EvidenciaRepository evidenciaRepo;
    private final UsuarioActual usuarioActual;
    private final AuditoriaService auditoria;

    @Override
    @Transactional
    public InspeccionResponse crear(InspeccionRequest req) {
        Espacio espacio = espacioRepo.findById(req.espacioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado: " + req.espacioId()));
        Usuario inspector = usuarioActual.obtener();
        if (inspeccionRepo.existsByEspacioIdAndTipoAndFecha(req.espacioId(), req.tipo(), req.fecha()))
            throw new ConflictoException("Ya existe una inspeccion de ese tipo para el espacio en esa fecha");

        Inspeccion i = new Inspeccion();
        i.setEspacio(espacio);
        i.setInspector(inspector);
        i.setTipo(req.tipo());
        i.setFecha(req.fecha());
        i.setObservaciones(req.observaciones());
        Inspeccion g = inspeccionRepo.save(i);

        auditoria.registrar(Accion.CREAR, "inspeccion", g.getId(),
                Map.of("espacioId", req.espacioId(), "tipo", req.tipo().name(), "fecha", req.fecha().toString()));

        return toResponse(g, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspeccionResponse> listar(Long espacioId, LocalDate fecha) {
        return inspeccionRepo.buscar(espacioId, fecha).stream()
                .map(i -> toResponse(i, false)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InspeccionResponse obtener(Long id) {
        return toResponse(buscar(id), true);
    }

    @Override
    @Transactional
    public DetalleResponse registrarPuesto(Long inspeccionId, Long puestoId, DetallePuestoRequest req) {
        Inspeccion insp = buscar(inspeccionId);
        exigirBorrador(insp);

        PuestoTrabajo puesto = puestoRepo.findById(puestoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto No encontrado: " + puestoId));
        if(!puesto.getEspacio().getId().equals(insp.getEspacio().getId()))
                throw new SolicitudInvalidaException("El puesto no pertenece al espacio de esta inspeccion");
        if(!puesto.getActivo())
            throw new SolicitudInvalidaException("El puesto está desactivado");

        List<HallazgoRequest> hs = req.hallazgos() == null ? List.of() : req.hallazgos();
        if (req.resultado() == Resultado.OK && !hs.isEmpty())
            throw new SolicitudInvalidaException("Un puesto con resultado OK no puede tener hallazgos");
        if(req.resultado() == Resultado.CON_NOVEDADES && hs.isEmpty())
            throw new SolicitudInvalidaException("Un puesto con novedades debe tener un hallazgo");

        InspeccionDetalle d = detalleRepo.findByInspeccionIdAndPuestoId(inspeccionId, puestoId)
                .orElseGet(() -> {
                    InspeccionDetalle nuevo = new InspeccionDetalle();
                    nuevo.setInspeccion(insp);
                    nuevo.setPuesto(puesto);
                    return nuevo;
                });
        d.setResultado(req.resultado());
        d.getHallazgos().clear();

        for (HallazgoRequest hr : hs) {
            TipoHallazgo tipo = tipoRepo.findById(hr.tipoHallazgoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tipo de hallazgo no encontrado: " + hr.tipoHallazgoId()
                    ));

            if ("OTRO".equals(tipo.getCodigo())
                && (hr.descripcion() == null || hr.descripcion().isBlank()))
                    throw new SolicitudInvalidaException("El hallazgo de tipo OTRO requiere una descripcion");

            if (tipo.getCategoria() == Categoria.DANO_FISICO
                    && (hr.componenteId() == null || hr.gravedad() == null))
                throw new SolicitudInvalidaException(
                        "Un hallazgo de daño físico debe indicar el componente afectado y la gravedad");

            Componente comp = null;
            if (hr.componenteId() != null) {
                comp = componenteRepo.findById(hr.componenteId())
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Componente no encontrado: " + hr.componenteId()
                        ));
                if(!comp.getPuesto().getId().equals(puestoId))
                    throw new SolicitudInvalidaException("El componente no pertenece a este puesto");
            }
            Hallazgo h = new Hallazgo();
            h.setDetalle(d);
            h.setTipoHallazgo(tipo);
            h.setComponente(comp);
            h.setDescripcion(hr.descripcion());
            h.setGravedad(hr.gravedad());
            d.getHallazgos().add(h);
        }

        auditoria.registrar(Accion.REGISTRAR_PUESTO, "inspeccion", inspeccionId,
                Map.of("puestoId", puestoId, "resultado", req.resultado().name(), "hallazgos", hs.size()));
        return DetalleResponse.from(detalleRepo.save(d));
    }

    @Override
    @Transactional
    public InspeccionResponse cerrar(Long id) {
        Inspeccion insp = buscar(id);
        exigirBorrador(insp);
        long total = puestoRepo.countByEspacioIdAndActivoTrue(insp.getEspacio().getId());
        long registrados = detalleRepo.countByInspeccionId(id);
        if (registrados < total)
            throw new ConflictoException("Faltan " + (total - registrados) + " puestos por registrar");

        for (InspeccionDetalle d : detalleRepo.findByInspeccionId(id)) {
            for (Hallazgo h : d.getHallazgos()) {
                if (h.getTipoHallazgo().getCategoria() != Categoria.DANO_FISICO) continue;
                ReporteDano r = new ReporteDano();
                r.setComponente(h.getComponente());
                r.setReportadoPor(insp.getInspector());
                r.setInspeccion(insp);
                r.setDescripcion(h.getDescripcion() != null && !h.getDescripcion().isBlank()
                        ? h.getDescripcion() : h.getTipoHallazgo().getNombre());
                r.setGravedad(h.getGravedad());
                reporteRepo.save(r);
                auditoria.registrar(Accion.CREAR, "reporte_dano", r.getId(),
                        Map.of("origen", "INSPECCION", "inspeccionId", id));
                evidenciaRepo.findByHallazgoId(h.getId()).forEach(ev -> ev.setReporte(r));
            }
        }
        insp.setEstado(EstadoInspeccion.CERRADA);
        auditoria.registrar(Accion.CERRAR, "inspeccion", id, Map.of("puestos", registrados));
        return toResponse(insp, true);
    }

    @Override
    public Inspeccion buscar(Long id) {
        return inspeccionRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inspeccion no encontrada: " + id));
    }

    @Override
    public void exigirBorrador(Inspeccion i) {
        if (i.getEstado() == EstadoInspeccion.CERRADA)
            throw new ConflictoException("La inspeccion ya esta cerrada y no se puede modificar");
    }

    @Override
    public InspeccionResponse toResponse(Inspeccion i, boolean conDetalles) {
        List<DetalleResponse> detalles = conDetalles
                ? detalleRepo.findByInspeccionId(i.getId()).stream().map(DetalleResponse::from).toList()
                : List.of();
        return new InspeccionResponse(i.getId(), i.getEspacio().getId(), i.getEspacio().getNombre(),
                i.getTipo(), i.getFecha(), i.getInspector().getId(), i.getInspector().getNombre(),
                i.getEstado(), i.getObservaciones(), i.getCreadaEn(),
                detalleRepo.countByInspeccionId(i.getId()),
                puestoRepo.countByEspacioIdAndActivoTrue(i.getEspacio().getId()),
                detalles);
    }
}
