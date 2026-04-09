package mx.edu.utez.modules.maintenance.mantenimientos.projections;

public interface TiempoPromedioProjection {
    String getMes();
    String getTipoMantenimiento();
    Double getPromedioHoras();
}
