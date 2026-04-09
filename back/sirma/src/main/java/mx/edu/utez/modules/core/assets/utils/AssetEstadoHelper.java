package mx.edu.utez.modules.core.assets.utils;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.edu.utez.modules.core.assets.Assets;

/**
 * Clase auxiliar que proporciona métodos de utilidad para verificar y manipular
 * el estado operativo y de custodia de un Activo (Assets).
 * Esta clase no puede instanciarse directamente ya que está diseñada para contener métodos estáticos.
 *
 * @author Ithera Team
 */
@Log4j2
@NoArgsConstructor
public final class AssetEstadoHelper {

    /**
     * Obtiene el activo y aplica una comparación que determina si el activo operativo está dado de baja.
     * @param a Activo al que se aplicara la comparación
     * @return <code>boolean</code> con <code>true</code> si el operativo está dado de baja y <code>false</code> si no esta dado de baja
     */
    public static boolean esActivoOperativoDadoDeBaja(Assets a) {
        if (a == null) return true;
        if (Boolean.FALSE.equals(a.getEsActivo())) return true;
        String op = a.getEstadoOperativo();
        return op != null && AssetEstados.OPERATIVO_BAJA.equalsIgnoreCase(op.trim());
    }

    // operativo tiene que estar en Reportado (y no baja)
    /**
     * Evalúa si se le puede asignar un mantenimiento al activo proporcionado.
     * El activo debe estar habilitado, tener un estado operativo de 'Reportado' y no estar dado de baja.
     *
     * @param a El activo a evaluar.
     * @return <code>true</code> si se le puede asignar mantenimiento; <code>false</code> en caso contrario.
     */
    public static boolean puedeAsignarMantenimiento(Assets a) {
        if (a == null) return false;
        if (Boolean.FALSE.equals(a.getEsActivo())) return false;
        String op = a.getEstadoOperativo();
        if (op == null) return false;
        if (AssetEstados.OPERATIVO_BAJA.equalsIgnoreCase(op.trim())) return false;
        return AssetEstados.OPERATIVO_REPORTADO.equalsIgnoreCase(op.trim());
    }

    // texto de conclusión = Irreparable
    /**
     * Determina si un texto de conclusión indica que el activo es irreparable.
     *
     * @param conclusion El texto de conclusión de una tarea de mantenimiento.
     * @return <code>true</code> si la conclusión es 'Irreparable', ignorando mayúsculas y minúsculas; <code>false</code> en caso contrario.
     */
    public static boolean esConclusionIrreparable(String conclusion) {
        return conclusion != null && "Irreparable".equalsIgnoreCase(conclusion.trim());
    }

    // irreparable → sigue Mantenimiento; si no → OK
    /**
     * Determina el nuevo estado operativo de un activo tras cerrar una tarea de mantenimiento.
     * Si la conclusión se consideró irreparable, el activo permanece en mantenimiento; de lo contrario, su estado operativo pasa a ser OK.
     *
     * @param conclusion El texto de conclusión de la tarea de mantenimiento.
     * @return Una cadena que representa el estado operativo resultante.
     */
    public static String operativoTrasCierreMantenimiento(String conclusion) {
        return esConclusionIrreparable(conclusion)
                ? AssetEstados.OPERATIVO_MANTENIMIENTO
                : AssetEstados.OPERATIVO_OK;
    }

    // warn: Disponible+Reportado/Mantenimiento o En proceso+Reportado
    /**
     * Revisa los estados operativo y de custodia combinados de un activo y registra una advertencia
     * en caso de encontrar una combinación inusual o potencialmente conflictiva.
     * Dicha combinación podría implicar que un activo está disponible pero en mantenimiento/reportado,
     * o en proceso y a la vez reportado.
     *
     * @param a El activo cuyos estados serán evaluados.
     */
    public static void advertirSiCombinacionInusual(Assets a) {
        if (a == null) return;
        String c = a.getEstadoCustodia();
        String o = a.getEstadoOperativo();
        if (c == null || o == null) return;
        boolean cust = AssetEstados.CUSTODIA_DISPONIBLE.equalsIgnoreCase(c.trim());
        boolean rep = AssetEstados.OPERATIVO_REPORTADO.equalsIgnoreCase(o.trim());
        boolean mant = AssetEstados.OPERATIVO_MANTENIMIENTO.equalsIgnoreCase(o.trim());
        if (cust && (rep || mant)) {
            log.warn("Activo {}: combinación inusual Disponible + {} (revisar datos)", a.getId(), o);
        }
        if (AssetEstados.CUSTODIA_EN_PROCESO.equalsIgnoreCase(c.trim()) && rep) {
            log.warn("Activo {}: Reportado con custodia En Proceso (revisar datos)", a.getId());
        }
    }
}
