package mx.edu.utez.modules.edificios;

import mx.edu.utez.modules.espacios.Espacio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de base de datos JPA para interactuar con la entidad Edificio.
 * Previene duplicados filtrándolos según el Campus emparentado.
 * 
 * @author Ithera Team
 */
@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {
    
    /**
     * Agrupa todas las instancias donde edificio se expone como activo de forma paginada.
     * @param pageable Orientación de datos.
     * @return <code>Page&lt;Edificio&gt;</code> filtrado lógicamente.
     */
    Page<Edificio> findAllByEsActivoTrue(Pageable pageable);
    
    /**
     * Entrega todos los elementos anidados de determinado campus independientemente de su estado lógico.
     * @param campusId ID particular.
     * @return Lista completa.
     */
    List<Edificio> findByCampusId(Long campusId);
    
    /**
     * Entrega todos los elementos anidados de determinado campus que solo están habilitados.
     * @param campusId ID particular del campus padre.
     * @return Sub-lista activa.
     */
    List<Edificio> findByCampusIdAndEsActivoTrue(Long campusId);
    
    /**
     * Corrobora si un edificio con idéntico nombre y asociado al mismo campus ya está de alta en sistema.
     * @param campusId Base del campus subyacente.
     * @param nombre Condición o rubro comparado.
     * @return Positiva o negativa dependiente.
     */
    boolean existsByCampusIdAndNombreAndEsActivoTrue(Long campusId, String nombre);
    
    /**
     * Examina si para updates existe otro Edificio interrumpiendo las constraints sin contarse él mismo.
     * @param campusId Conjunto campus.
     * @param nombre Cadena.
     * @param id Clave del actual o principal a omitir de consulta.
     * @return Flag.
     */
    boolean existsByCampusIdAndNombreAndEsActivoTrueAndIdNot(Long campusId, String nombre, Long id);
    
    /**
     * Regresa la primera coincidencia que esté dada de baja lista para ser reactivada.
     * @param campusId El campus emparentado.
     * @param nombre Nombre del edificio que había.
     * @return Un Optional conteniendo la entidad inactiva en caso.
     */
    Optional<Edificio> findFirstByCampusIdAndNombreAndEsActivoFalse(Long campusId, String nombre);
    
    /**
     * Consulta con nombre y campus exacto independientemente de su estipulación de alta.
     * @param campusId ID Campus padre.
     * @param nombre Nombre edificio.
     * @return El edificio como optativo.
     */
    Optional<Edificio> findByCampusIdAndNombre(Long campusId, String nombre);

    /**
     * Mapea listas en bloque a fin de encontrar correspondencias basándose en Listados de IDs de Campus.
     * @param campusIds Rango de búsqueda inyectada por colección IN.
     * @return Una <code>List&lt;Edificio&gt;</code> con lo reunido de esos campus.
     */
    List<Edificio> findByCampusIdIn(Collection<Long> campusIds);
}
