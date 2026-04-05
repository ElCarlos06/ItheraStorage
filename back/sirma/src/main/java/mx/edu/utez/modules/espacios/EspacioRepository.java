package mx.edu.utez.modules.espacios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio contenedor de métodos para transacciones hacia los Espacios registrados.
 * Impide colisiones de idénticos nombres dentro de un mismo techo (edificio).
 *
 * @author Ithera Team
 */
@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {

    /**
     * Agrupa todas las instancias donde el ambiente o área se expone como activo. Presentación paginada.
     * @param pageable Orientación de datos.
     * @return <code>Page&lt;Espacio&gt;</code> filtrado lógicamente.
     */
    Page<Espacio> findAllByEsActivoTrue(Pageable pageable);

    /**
     * Entrega todos los elementos anidados de determinado edificio omitiendo su estado.
     * @param edificioId ID particular.
     * @return Lista completa emparentada.
     */
    List<Espacio> findByEdificioId(Long edificioId);

    /**
     * Listado específico por contenedor pero devolviendo puros datos funcionales en activo.
     * @param edificioId Llave del padre jerárquico.
     * @return Áreas disponibles.
     */
    List<Espacio> findByEdificioIdAndEsActivoTrue(Long edificioId);

    /**
     * Veracidad si en el edificio señalado existe el espacio con el nombre dado y activo.
     * @param edificioId Base edificio contenedor.
     * @param nombreEspacio Valor igualitario expuesto.
     * @return booleano de respuesta rápida.
     */
    boolean existsByEdificioIdAndNombreEspacioAndEsActivoTrue(Long edificioId, String nombreEspacio);

    /**
     * Coteja si para un renombrado (update), el nuevo título confligiría con algún aula ya alojada activa que NO sea el aula objetivo original.
     * @param edificioId Contexto base.
     * @param nombreEspacio Cadena texto evaluada.
     * @param id Clave del actual o principal a perdonar.
     * @return Si se interrumpe la constraint.
     */
    boolean existsByEdificioIdAndNombreEspacioAndEsActivoTrueAndIdNot(Long edificioId, String nombreEspacio, Long id);

    /**
     * Consigue en modo First Result la recuperación de una inactividad exacta lista a reactivar.
     * @param edificioId Base edificio.
     * @param nombreEspacio Título a emparejar y que yació apagado.
     * @return Entidad oponente encapsulable.
     */
    Optional<Espacio> findFirstByEdificioIdAndNombreEspacioAndEsActivoFalse(Long edificioId, String nombreEspacio);

    /**
     * Extracción certera sea o no activa y mediante clave foránea + nombre local.
     * @param edificioId Dependiente asertado.
     * @param nombreEspacio Denominación de sala.
     * @return Formato asilado del dato persistido.
     */
    Optional<Espacio> findByEdificioIdAndNombreEspacio(Long edificioId, String nombreEspacio);

    /**
     * Rastreo colectivo apoyado del IN clause de BD sobre una lista paramétrica de parent-id.
     * @param edificioIds Varias claves inyectadas simultáneamente.
     * @return Matriz de Espacios subyacentes.
     */
    List<Espacio> findByEdificioIdIn(Collection<Long> edificioIds);
}
