package mx.edu.utez.modules.bitacora;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import mx.edu.utez.security.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;
    private final AssetsRepository assetsRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Bitacora> list = bitacoraRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Bitacora> found = bitacoraRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Registro de bitácora no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Bitacora> list = bitacoraRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByUsuario(Long usuarioId) {
        List<Bitacora> list = bitacoraRepository.findByUsuarioId(usuarioId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    // Bitácora es INSERT ONLY — no se actualiza ni elimina
    @Transactional
    public ApiResponse save(BitacoraDTO dto) {
        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());
        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<User> usuario = userRepository.findById(dto.getIdUsuario());
        if (usuario.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);

        Bitacora entity = new Bitacora();
        entity.setActivo(activo.get());
        entity.setUsuario(usuario.get());
        entity.setTipoEvento(dto.getTipoEvento());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEstadoCustodiaAnterior(dto.getEstadoCustodiaAnterior());
        entity.setEstadoCustodiaNuevo(dto.getEstadoCustodiaNuevo());
        entity.setEstadoOperativoAnterior(dto.getEstadoOperativoAnterior());
        entity.setEstadoOperativoNuevo(dto.getEstadoOperativoNuevo());
        bitacoraRepository.save(entity);
        return new ApiResponse("Registro de bitácora creado", entity, HttpStatus.CREATED);
    }

    /**
     * Registra un evento en la bitácora sin lanzar excepciones.
     * Si usuarioId es null, usa el usuario autenticado actual.
     */
    @Transactional
    public void registrarEvento(
            Long activoId,
            Long usuarioId,
            String tipoEvento,
            String descripcion,
            String estadoCustodiaAnterior,
            String estadoCustodiaNuevo,
            String estadoOperativoAnterior,
            String estadoOperativoNuevo
    ) {
        Long uid = usuarioId;
        if (uid == null && jwtProvider != null && jwtProvider.getCurrentUser() != null) {
            uid = jwtProvider.getCurrentUser().getId();
        }
        if (uid == null) {
            log.warn("Bitácora: no se pudo registrar evento '{}' (sin usuario)", tipoEvento);
            return;
        }
        Optional<Assets> activo = assetsRepository.findById(activoId);
        if (activo.isEmpty()) return;
        Optional<User> usuario = userRepository.findById(uid);
        if (usuario.isEmpty()) return;

        Bitacora b = new Bitacora();
        b.setActivo(activo.get());
        b.setUsuario(usuario.get());
        b.setTipoEvento(tipoEvento);
        b.setDescripcion(descripcion);
        b.setEstadoCustodiaAnterior(estadoCustodiaAnterior);
        b.setEstadoCustodiaNuevo(estadoCustodiaNuevo);
        b.setEstadoOperativoAnterior(estadoOperativoAnterior);
        b.setEstadoOperativoNuevo(estadoOperativoNuevo);
        bitacoraRepository.save(b);
    }

}

