package mx.edu.utez.modules.assets;

public final class AssetEstados {

    private AssetEstados() {}

    /** En almacén / sin empleado asignado. */
    public static final String CUSTODIA_DISPONIBLE = "Disponible";
    /** Resguardo creado, empleado aún no confirma. */
    public static final String CUSTODIA_EN_PROCESO = "En Proceso";
    /** Empleado ya confirmó custodia. */
    public static final String CUSTODIA_RESGUARDADO = "Resguardado";

    /** Sin incidencia abierta en operativo. */
    public static final String OPERATIVO_OK = "OK";
    /** Daño reportado (custodia puede seguir Resguardado). */
    public static final String OPERATIVO_REPORTADO = "Reportado";
    /** Técnico asignado; si es irreparable queda aquí hasta baja. */
    public static final String OPERATIVO_MANTENIMIENTO = "Mantenimiento";
    /** Baja del bien. */
    public static final String OPERATIVO_BAJA = "Baja";
}
