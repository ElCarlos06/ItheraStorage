package mx.edu.utez.modules.assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AssetEstadoHelper {

    private static final Logger log = LoggerFactory.getLogger(AssetEstadoHelper.class);

    private AssetEstadoHelper() {}

    // esActivo=false o operativo Baja
    public static boolean esActivoOperativoDadoDeBaja(Assets a) {
        if (a == null) return true;
        if (Boolean.FALSE.equals(a.getEsActivo())) return true;
        String op = a.getEstadoOperativo();
        return op != null && AssetEstados.OPERATIVO_BAJA.equalsIgnoreCase(op.trim());
    }

    // operativo tiene que estar en Reportado (y no baja)
    public static boolean puedeAsignarMantenimiento(Assets a) {
        if (a == null) return false;
        if (Boolean.FALSE.equals(a.getEsActivo())) return false;
        String op = a.getEstadoOperativo();
        if (op == null) return false;
        if (AssetEstados.OPERATIVO_BAJA.equalsIgnoreCase(op.trim())) return false;
        return AssetEstados.OPERATIVO_REPORTADO.equalsIgnoreCase(op.trim());
    }

    // texto de conclusión = Irreparable
    public static boolean esConclusionIrreparable(String conclusion) {
        return conclusion != null && "Irreparable".equalsIgnoreCase(conclusion.trim());
    }

    // irreparable → sigue Mantenimiento; si no → OK
    public static String operativoTrasCierreMantenimiento(String conclusion) {
        return esConclusionIrreparable(conclusion)
                ? AssetEstados.OPERATIVO_MANTENIMIENTO
                : AssetEstados.OPERATIVO_OK;
    }

    // warn: Disponible+Reportado/Mantenimiento o En proceso+Reportado
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
