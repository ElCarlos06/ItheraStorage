package mx.edu.utez.modules.core.assets.utils;

import mx.edu.utez.modules.core.assets.AssetsDTO;
import mx.edu.utez.modules.core.assets.projections.AssetsProjection;
import mx.edu.utez.modules.location.campus.Campus;
import mx.edu.utez.modules.location.edificios.Edificio;
import mx.edu.utez.modules.location.espacios.Espacio;
import mx.edu.utez.modules.core.tipo_activos.TipoActivo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssetsUtils {

    /**
     * Pone en un Map los resultados que obtuvimos de la qiuery y ya, si lo dejaba en getAssetStats se iba a ver bien largo XD
     * @param global Proyecciones globales de los activos actualmente
     * @param lastWeek Proyecciones de los activos de la semana pasada a la actual
     * @return Un <code>Map<String, Long></code> con los resultados mapeaos
     */
    public static Map<String, Long> getJson(AssetsProjection global, AssetsProjection lastWeek) {
        Map<String, Long> json = new HashMap<>();

        // Valores actuales globales
        json.put("total", global.getTotal());
        json.put("disponibles", global.getDisponibles());
        json.put("resguardados", global.getResguardados());
        json.put("enMantenimiento", global.getEnMantenimiento());
        json.put("reportados", global.getReportados());

        // % cambio semanal por categoría
        json.put("pctTotal",        calcPct(global.getTotal(), lastWeek.getTotal()));
        json.put("pctResguardados", calcPct(global.getResguardados(), lastWeek.getResguardados()));
        json.put("pctMantenimiento",calcPct(global.getEnMantenimiento(), lastWeek.getEnMantenimiento()));
        json.put("pctReportados",   calcPct(global.getReportados(), lastWeek.getReportados()));

        return json;
    }

    /**
     * Retorna el porcentaje de las proyecciones actual vs anterior, redondeado al entero más cercano.
     * Si el valor anterior es 0, retorna 100% si el actual es >0, o 0% si el actual es 0.
     * @param actual Total de proyecciones para la semana actual
     * @param anterior Total de proyecciones para la semana pasada
     * @return <code>Long</code> con el "porcentaje" XD
     */
    private static long calcPct(long actual, long anterior) {
        if (anterior == 0) return actual > 0 ? 100 : 0;
        return Math.round(((actual - anterior) * 100.0) / anterior);
    }

    /**
     * La etiqueta del producto se genera automáticamente a partir de los dos primeros caracteres
     * del nombre seguido de los dos primeros caracteres de la marca seguido de los primeros
     * caracteres de cada elemento de la ubicación.
     * @param dto DTO que contiene la información del Activo.
     * @return <code>String</code> con la etiqueta generada automáticamente.
     */
    public static String generateProductTag(AssetsDTO dto) {

        if (dto == null || dto.getTipoActivo() == null || dto.getEspacio() == null) return "XXXX-XXXX-XXXX";

        // Se desglosan los objetos intermedios para evitar nulos xd
        TipoActivo tipoActivo = dto.getTipoActivo();
        Espacio espacio       = dto.getEspacio();
        Edificio edificio     = espacio.getEdificio();
        Campus campus         = (edificio != null) ? edificio.getCampus() : null;

        // Se esxtraen los valores finales
        String nombreStr   = tipoActivo.getNombre();
        String marcaStr    = tipoActivo.getMarca();
        String espacioStr  = espacio.getNombreEspacio();
        String edificioStr = (edificio != null)  ? edificio.getNombre() : null;
        String campusStr   = (campus != null)    ? campus.getNombre() : null;

        // Se generan los tags finales
        String tagSerie    = getTwoChars(nombreStr);
        String tagMarca    = getTwoChars(marcaStr);
        char tagEspacio    = getFirstChar(espacioStr);
        char tagEdificio   = getFirstChar(edificioStr);
        char tagCampus     = getFirstChar(campusStr);

        // Opción más segura: UUID corto
        String base = tagSerie + tagMarca + tagEspacio + tagEdificio + tagCampus;
        String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();

        return base + "-" + uid;
    }

    /**
     * Helper que ayuda a obtener las 2 primeras letras del string que recibe.
     * @param s <code>String</code> que vamos a obtener las 2 letras.
     * @return <code>String</code> con las 2 letras :D.
     */
    private static String getTwoChars(String s) {
        if (s == null || s.isEmpty()) return "XX";
        if (s.length() < 2) return s.toUpperCase() + "X";

        return s.substring(0, 2).toUpperCase();
    }

    /**
     * Helper que nos ayuda a obtener la primera letra del string que recibimos
     * @param s <code>String</code> del cual vamos a obtener la primera letra
     * @return <code>char</code> con la primera letra :D
     */
    private static char getFirstChar(String s) {
        if (s == null || s.isEmpty()) return 'X';
        return Character.toUpperCase(s.charAt(0));
    }


    /** Trunca un string al máximo de caracteres para evitar Data truncated en MySQL.
     *
     * @param s Cadena original a truncar.
     * @param maxLen Límite máximo permitido.
     * @return Cadena truncada de acuerdo con el límite estipulado.
     */
    public static String truncate(String s, int maxLen) {
        if (s == null) return null;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    /** Normaliza estado_custodia a valores exactos del ENUM en BD: Disponible | En Proceso | Resguardado | Baja
     *
     * @param s Cadena de estatus recibida desde el cliente.
     * @return Equivalente normalizado acorde a las constantes reales.
     */
    public static String normalizeEstadoCustodia(String s) {
        if (s == null) return "Disponible";
        String lower = s.trim().toLowerCase();
        if (lower.contains("disponible") || "disp".equals(lower)) return "Disponible";
        if (lower.contains("proceso") || "proc".equals(lower)) return "En Proceso";
        if (lower.contains("resguard") || "resg".equals(lower)) return "Resguardado";
        if (lower.contains("baja")) return "Baja";
        return s;
    }

    /** Redondea costo a 2 decimales y limita a DECIMAL(10,2) para evitar Data truncated.
     *
     * @param costo Valor original de tipo BigDecimal.
     * @return Valor decimal formateado de forma segura.
     */
    public static BigDecimal safeCosto(BigDecimal costo) {
        if (costo == null) return null;
        return costo.setScale(2, RoundingMode.HALF_UP);
    }

}
