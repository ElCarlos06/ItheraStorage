package mx.edu.utez.modules.bitacora;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para eventos de bitácora.
 * Realiza las operaciones de guardado de historiales y búsqueda por filtros.
 *
 * @author Ithera Team
 */
@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {

    /**
     * Recupera todos los eventos asociados con un determinado activo.
     *
     * @param activoId Valor del campo id de tipo Activo asignado.
     * @return Lista completa y ordenada por omisión con las entradas correspondientes.
     */
    List<Bitacora> findByActivoId(Long activoId);

    /**
     * Localiza todos los registros generados mediante las interacciones de un usuario en específico.
     *
     * @param usuarioId Clave foránea del empleado u operador evaluado.
     * @return Elementos correspondientes en el registro.
     */
    List<Bitacora> findByUsuarioId(Long usuarioId);

    /**
     * Retorna listado de acuerdo al nombre o título categorizado del evento de sistema generado (Ej: "Baja", "Asignación").
     *
     * @param tipoEvento Clasificación buscada a filtrar.
     * @return Entradas homologadas.
     */
    List<Bitacora> findByTipoEvento(String tipoEvento);
}
