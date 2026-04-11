package mx.edu.utez.modules.reporting.bitacora;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.core.assets.AssetsRepository;
import mx.edu.utez.modules.security.auth.user_details.UserDetailsImp;
import mx.edu.utez.modules.security.users.User;
import mx.edu.utez.modules.security.users.UserRepository;
import mx.edu.utez.security.jwt.JwtProvider;
import mx.edu.utez.util.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar y procesar todas las operaciones relacionadas
 * a la Bitácora del sistema y sus auditorías.
 *
 * @author Ithera Team
 */
@Log4j2
@Service
@AllArgsConstructor
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;
    private final AssetsRepository assetsRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    /**
     * Consulta para extraer todo el historial presente en el registro de auditoría.
     *
     * @return <code>ApiResponse</code> detallando el arreglo de eventos junto al código HTTP.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Bitacora> list = bitacoraRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Proporciona los detalles particulares de un evento guardado.
     *
     * @param id Identificador que funge como llave del evento en la bitácora.
     * @return Transforma u opta un error si no fue localizado, incrustado en el <code>ApiResponse</code>.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Bitacora> found = bitacoraRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Registro de bitácora no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Revisa todas las alteraciones por las que ha atravesado o afectado a un activo.
     *
     * @param activoId Id del objeto <code>Assets</code> al que se examina.
     * @return Lista completa envuelta en un <code>ApiResponse</code>.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Bitacora> list = bitacoraRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Retorna todas las interacciones realizadas en su momento o instigadas por un autor identificado.
     *
     * @param usuarioId Clave única del <code>User</code> que ejecutó estos eventos.
     * @return <code>ApiResponse</code> con el arreglo de estos elementos en bitácora.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByUsuario(Long usuarioId) {
        List<Bitacora> list = bitacoraRepository.findByUsuarioId(usuarioId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    // Bitácora es INSERT ONLY — no se actualiza ni elimina
    /**
     * Conforma y guarda un nuevo registro manual en la tabla.
     * Al funcionar puramente de historial y auditoría, está diseñado para ser INSERT ONLY y sin Updates/Deletes.
     *
     * @param dto Molde de datos que viene directamente de la capa de control.
     * @return Representación del éxito o el fallo si el activo/usuario subyacente no figura en sistema.
     */
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
     *
     * @param activoId Identificador del activo físico modificado.
     * @param usuarioId Clave del usuario ejecutor, u obtenida remotamente del contexto token de no especificarse.
     * @param tipoEvento Cadena descriptiva categórica del asunto actual.
     * @param descripcion Detalles adicionales que enriquecen la descripción del movimiento.
     * @param estadoCustodiaAnterior Anterior situación que ostentaba el campo.
     * @param estadoCustodiaNuevo Entorno o estatus actualizado tras el suceso.
     * @param estadoOperativoAnterior Lo análogo del estado custodia, pero a nivel operativo.
     * @param estadoOperativoNuevo Última situación operativa resultante para guardar.
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
        Long currentUserId = jwtProvider.getCurrentUser().map(UserDetailsImp::getId).orElse(null);
        Long uid = (currentUserId != null) ? currentUserId : usuarioId;

        if (uid == null) {
            log.warn("Bitácora: no se pudo registrar evento '{}' (sin usuario)", tipoEvento);
            return;
        }

        Assets activo = assetsRepository.getReferenceById(activoId);
        User usuario = userRepository.getReferenceById(uid);

        Bitacora b = new Bitacora();
        b.setActivo(activo);
        b.setUsuario(usuario);
        b.setTipoEvento(tipoEvento);
        b.setDescripcion(descripcion);
        b.setEstadoCustodiaAnterior(estadoCustodiaAnterior);
        b.setEstadoCustodiaNuevo(estadoCustodiaNuevo);
        b.setEstadoOperativoAnterior(estadoOperativoAnterior);
        b.setEstadoOperativoNuevo(estadoOperativoNuevo);

        bitacoraRepository.save(b);
    }

    /**
     * Sobrecarga del metodo registrarEvento, este es con ell fin de registrar activos de forma masiva
     * @param activos <code>List<Assets></code> que son los activos que vamos a mapear
     */
    @Transactional
    public void registrarEvento(List<Assets> activos) {

        User user = handleUser();

        List<Bitacora> registros = activos.stream()
                .map(a -> {
                    Bitacora b = new Bitacora();
                    b.setActivo(a);
                    b.setUsuario(user);
                    b.setTipoEvento("Registro Activo");
                    b.setDescripcion("Activo " + a.getEtiqueta() + " importado desde Excel");
                    b.setEstadoCustodiaAnterior(null);
                    b.setEstadoCustodiaNuevo("Disponible");
                    b.setEstadoOperativoAnterior(null);
                    b.setEstadoOperativoNuevo("OK");
                    return b;
                })
                .toList();

        bitacoraRepository.saveAll(registros);
    }

    /**
     * Auxiliar que intercepta el entorno actual y resuelve la instancia de User en base al token en sesión.
     *
     * @return Entidad completa e inferida del autor.
     * @throws CustomException Si las credenciales o la persistencia es inválida.
     */
    private User handleUser() {
        UserDetailsImp current = jwtProvider.getCurrentUser()
                .orElseThrow(() -> new CustomException("Usuario no encontrado", HttpStatus.UNAUTHORIZED));

        return userRepository.findById(current.getId())
                .orElseThrow(() -> new CustomException(
                        "Usuario autenticado no encontrado en BD", HttpStatus.UNAUTHORIZED)
                );
    }

}
