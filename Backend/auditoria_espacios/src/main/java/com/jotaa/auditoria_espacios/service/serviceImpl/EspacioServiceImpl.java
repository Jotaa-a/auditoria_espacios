package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.dto.request.EspacioRequest;
import com.jotaa.auditoria_espacios.dto.response.EspacioResponse;
import com.jotaa.auditoria_espacios.entity.Espacio;
import com.jotaa.auditoria_espacios.exception.ConflictoException;
import com.jotaa.auditoria_espacios.exception.RecursoNoEncontradoException;
import com.jotaa.auditoria_espacios.repository.EspacioRepository;
import com.jotaa.auditoria_espacios.repository.UsuarioRepository;
import com.jotaa.auditoria_espacios.service.EspacioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspacioServiceImpl implements EspacioService {

    private final EspacioRepository espacioRepo;
    private final UsuarioRepository usuarioRepo;

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
        return EspacioResponse.from(espacioRepo.save(e));
    }

    @Override
    @Transactional
    public EspacioResponse actualizar(Long id, EspacioRequest req) {
        Espacio e = buscar(id);
        if (!e.getNombre().equals(req.nombre()) && espacioRepo.existsByNombre(req.nombre()))
            throw new ConflictoException("Ya existe un espacio con ese nombre");
        aplicar(e, req);
        return EspacioResponse.from(e);
    }

    @Override
    public void eliminar(Long id) {
        espacioRepo.delete(buscar(id));
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
}
