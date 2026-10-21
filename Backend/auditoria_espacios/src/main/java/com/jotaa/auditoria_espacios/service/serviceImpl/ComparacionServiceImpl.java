package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.response.*;
import com.jotaa.auditoria_espacios.entidad.Hallazgo;
import com.jotaa.auditoria_espacios.entidad.Inspeccion;
import com.jotaa.auditoria_espacios.entidad.InspeccionDetalle;
import com.jotaa.auditoria_espacios.entidad.PuestoTrabajo;
import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoComparacion;
import com.jotaa.auditoria_espacios.entidad.enumerated.EstadoInspeccion;
import com.jotaa.auditoria_espacios.entidad.enumerated.Resultado;
import com.jotaa.auditoria_espacios.entidad.enumerated.TipoInspeccion;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.exception.SolicitudInvalidaException;
import com.jotaa.auditoria_espacios.repository.InspeccionDetalleRepository;
import com.jotaa.auditoria_espacios.repository.InspeccionRepository;
import com.jotaa.auditoria_espacios.service.ComparacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparacionServiceImpl implements ComparacionService {

    private final InspeccionRepository inspeccionRepo;
    private final InspeccionDetalleRepository detalleRepo;

    private record Clave(Long tipoId, Long componenteId){
        static Clave de(Hallazgo h){
            return new Clave(h.getTipoHallazgo().getId(),
            h.getComponente() != null ? h.getComponente().getId() : null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ComparacionResponse comparar(Long lunesId, Long viernesId) {
        Inspeccion lunes = buscar(lunesId);
        if (lunes.getTipo() != TipoInspeccion.LUNES_RECEPCION)
            throw new SolicitudInvalidaException("La inspeccion indicada no es de tipo LUNES_RECEPCION");
        exigirCerrada(lunes);

        Inspeccion viernes = (viernesId != null)
                ? buscar(viernesId)
                : inspeccionRepo
                .findFirstByEspacioIdAndTipoAndEstadoAndFechaLessThanOrderByFechaDesc(
                        lunes.getEspacio().getId(), TipoInspeccion.VIERNES_ENTREGA,
                        EstadoInspeccion.CERRADA, lunes.getFecha())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay inspeccion de viernes anterior a esta para el espacio"
                        )
                );

        if (viernes.getTipo() != TipoInspeccion.VIERNES_ENTREGA)
            throw new SolicitudInvalidaException("La inspeccion de viernes indicada no es de tipo VIERNES_ENTREGA");
        if (!viernes.getEspacio().getId().equals(lunes.getEspacio().getId()))
            throw new SolicitudInvalidaException("Las inspeccions son de espacios distintos");
        if (!viernes.getFecha().isBefore(lunes.getFecha()))
            throw new SolicitudInvalidaException("El viernes debe ser anterior al lunes");
        exigirCerrada(viernes);

        Map<Long, InspeccionDetalle> dv = porPuesto(viernes.getId());
        Map<Long, InspeccionDetalle> dl = porPuesto(lunes.getId());

        Set<Long> ids = new LinkedHashSet<>(dl.keySet());

        List<PuestoComparacion> puestos = ids.stream()
                .map(id -> {
                    InspeccionDetalle v = dv.get(id), l = dl.get(id);
                    PuestoTrabajo p = (l != null ? l : v).getPuesto();
                    return compararPuesto(p, v, l);
                })
                .sorted(Comparator.comparing(PuestoComparacion::puestoCodigo))
                .toList();

        ResumenComparacion resumen = new ResumenComparacion(
                puestos.size(),
                contar(puestos, EstadoComparacion.SIN_CAMBIOS),
                contar(puestos, EstadoComparacion.NUEVA_NOVEDAD),
                contar(puestos, EstadoComparacion.PERSISTE),
                contar(puestos, EstadoComparacion.RESUELTO),
                contar(puestos, EstadoComparacion.SIN_REGISTRO));

        return new ComparacionResponse(
                lunes.getEspacio().getId(), lunes.getEspacio().getNombre(),
                ref(viernes), ref(lunes), resumen, puestos);
    }

    private PuestoComparacion compararPuesto(PuestoTrabajo p, InspeccionDetalle v, InspeccionDetalle l){
        Resultado rv = v != null ? v.getResultado() : null;
        Resultado rl =l != null ? l.getResultado() : null;

        if(v == null || l == null)
            return new PuestoComparacion(p.getId(), p.getCodigo(), EstadoComparacion.SIN_REGISTRO,
                    rv, rl, List.of(), List.of(), List.of());

        Map<Clave, Hallazgo> hv = indexar(v);
        Map<Clave, Hallazgo> hl = indexar(l);

        List<HallazgoResponse> nuevos = hl.entrySet().stream()
                .filter(e -> !hv.containsKey(e.getKey()))
                .map(e -> HallazgoResponse.from(e.getValue())).toList();
        List<HallazgoResponse> persistentes = hl.entrySet().stream()
                .filter(e -> hv.containsKey(e.getKey()))
                .map(e -> HallazgoResponse.from(e.getValue())).toList();
        List<HallazgoResponse> resueltos = hv.entrySet().stream()
                .filter(e -> !hl.containsKey(e.getKey()))
                .map(e -> HallazgoResponse.from(e.getValue())).toList();

        EstadoComparacion estado;
        if (!nuevos.isEmpty())              estado = EstadoComparacion.NUEVA_NOVEDAD;
        else if (!persistentes.isEmpty())   estado = EstadoComparacion.PERSISTE;
        else if (!resueltos.isEmpty())      estado = EstadoComparacion.RESUELTO;
        else                                estado = EstadoComparacion.SIN_CAMBIOS;

        return new PuestoComparacion(p.getId(), p.getCodigo(), estado, rv, rl,
                nuevos, persistentes, resueltos);

    }

    private Map<Clave, Hallazgo> indexar(InspeccionDetalle d){
        Map<Clave, Hallazgo> mapa = new LinkedHashMap<>();
        d.getHallazgos().forEach(h -> mapa.putIfAbsent(Clave.de(h), h));
        return mapa;
    }

    private Map<Long, InspeccionDetalle> porPuesto(Long inspeccionId){
        return detalleRepo.findByInspeccionId(inspeccionId).stream()
                .collect(Collectors.toMap(d -> d.getPuesto().getId(), Function.identity()));
    }

    private long contar(List<PuestoComparacion> lista, EstadoComparacion estado){
        return lista.stream().filter(p -> p.estado() == estado).count();
    }

    private InspeccionRef ref (Inspeccion i){
        return new InspeccionRef(i.getId(), i.getFecha(), i.getInspector().getNombre());
    }

    private Inspeccion buscar(Long id){
        return inspeccionRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inspeccion no encontrada: " + id));
    }

    private void exigirCerrada(Inspeccion i){
        if (i.getEstado() != EstadoInspeccion.CERRADA)
            throw new ConflictoException(
                    "La inspeccion " + i.getId() + " debe estar cerrada para poder compararla"
            );
    }
}
