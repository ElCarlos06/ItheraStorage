package mx.edu.utez.modules.assets;

/**
 * Interfaz de proyección JPA utilizada para recuperar estadísticas agregadas de los Activos.
 * Consolidación rápida de sumatorias de estado.
 *
 * @author Ithera Team
 */
public interface AssetsProjection {

    /** @return Total de activos registrados y habilitados. */
    Long getTotal();

    /** @return Cantidad de activos cuyo estado de custodia es disponible. */
    Long getDisponibles();

    /** @return Cantidad de activos que se encuentran resguardados por algún usuario. */
    Long getResguardados();

    /** @return Cantidad de activos bajo estado de mantenimiento operativo. */
    Long getEnMantenimiento();

    /** @return Cantidad de activos que tienen reportes por incidencias o daños. */
    Long getReportados();

}
