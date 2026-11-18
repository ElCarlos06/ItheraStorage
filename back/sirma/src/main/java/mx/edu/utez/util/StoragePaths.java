package mx.edu.utez.util;

/**
 * Clase utilitaria para centralizar las rutas/prefijos de almacenamiento en la nube (AWS S3).
 * Facilita el mantenimiento y asegura la consistencia en la estructura de archivos.
 *
 * @author Ithera Team
 */
public class StoragePaths {

    // Rutas base
    public static final String BASE_SIRMA = "sirma";

    // Módulos
    public static final String ACTIVOS = BASE_SIRMA + "/activos";

    /** Subcarpeta para códigos QR de activos. */
    public static final String ACTIVOS_QR = ACTIVOS + "/qr";

    public static final String REPORTES = BASE_SIRMA + "/reportes";
    public static final String MANTENIMIENTOS = BASE_SIRMA + "/mantenimientos";
    public static final String PERFILES = BASE_SIRMA + "/perfiles";

    public static String activos(Long id) {
        return ACTIVOS + "/" + id + "/imagenes";
    }

    public static String reportes(Long id) {
        return REPORTES + "/" + id;
    }

    public static String mantenimientos(Long id) {
        return MANTENIMIENTOS + "/" + id;
    }

    protected StoragePaths() {}
}
