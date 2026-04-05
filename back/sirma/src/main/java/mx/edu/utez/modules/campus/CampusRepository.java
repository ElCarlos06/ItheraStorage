package mx.edu.utez.modules.campus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repositorio JPA para la entidad Campus.
 * Proporciona operaciones CRUD para campus universitarios.
 *
 * @author Ithera Team
 */
@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {

    /**
     * Obtiene todos los campus que están activos actualmente dentro de un formato de págincación.
     * @param pageable Configuración de paginación para la salida.
     * @return Página de resultados coincidentes.
     */
    Page<Campus> findAllByEsActivoTrue(Pageable pageable);

    /**
     * Verifica la disponibilidad de la nomenclatura del campus tomando en cuenta los registros activos.
     * @param nombre Título o nombre de campus a encontrar en base de datos.
     * @return booleano en respuesta.
     */
    boolean existsByNombreAndEsActivoTrue(String nombre);

    /**
     * Revisa si existe otro campo con el mismo nombre y activo. Evita contar el actual que se busca actualizar.
     * @param nombre Título comparativo para chocar que no existan duplicados.
     * @param id Clave del actual o principal a omitir.
     * @return verdadero cuando si existen otros activos del mismo nombre.
     */
    boolean existsByNombreAndEsActivoTrueAndIdNot(String nombre, Long id);

    /**
     * Localiza mediante nombre exacto a una instancia de Campus.
     * @param nombre Llave con la que realizará coincidir.
     * @return Entidad completa empaquetada.
     */
    Optional<Campus> findByNombre(String nombre);

    /**
     * Rastrea al primer campus que coincida con el nombre y se encuentre inactivo o de baja.
     * Utilizado para reactivación eficiente de entidades.
     * @param nombre Con el que empatan.
     * @return Instancia contenida si es encontrado.
     */
    Optional<Campus> findFirstByNombreAndEsActivoFalse(String nombre);

    /**
     * Recupera un lote particular de entidades de Campus cuyos nombres coinciden con elementos en el Set provisto.
     * @param campusLote Lista excluyente de nombres de campus buscando asimilar.
     * @return Todos los campus que pasen la condición del IN Clause.
     */
    List<Campus> findByNombreIn(Set<String> campusLote);
}
