import StatusBadge from "../../../../../components/StatusBadge/StatusBadge";

export const statusLabel = (s) => {
  const labels = {
    disponible: "Disponible",
    disp: "Disponible",
    resguardado: "Resguardado",
    resguardo: "Resguardado",
    resg: "Resguardado",
    mantenimiento: "Mantenimiento",
    "en proceso": "En proceso",
    enproceso: "En proceso",
    proc: "En proceso",
    baja: "Baja",
    reportado: "Reportado",
    rep: "Reportado",
  };
  return (
    labels[
      String(s || "")
        .toLowerCase()
        .trim()
    ] ?? s
  );
};

export default function AssetInfo({ item }) {
  return (
    <div className="activos-view__asset-content">
      <div className="activos-view__asset-row activos-view__asset-row--1">
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-code">{item.codigo ?? "—"}</p>
          <p className="activos-view__asset-desc">
            {item.descripcionCorta ?? "—"}
          </p>
        </div>
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-label">Activo</p>
          <p className="activos-view__asset-value">{item.nombre ?? "—"}</p>
        </div>
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-label">Asignado a</p>
          <p className="activos-view__asset-value">{item.asignadoA ?? "—"}</p>
        </div>
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-label">Tipo de activo</p>
          <p className="activos-view__asset-value">{item.tipoActivo ?? "—"}</p>
        </div>
      </div>
      <div className="activos-view__asset-row activos-view__asset-row--2">
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-label">Estado</p>
          <StatusBadge status={item.status ?? "disponible"} size="small">
            {statusLabel(item.status ?? "disponible")}
          </StatusBadge>
        </div>
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-label">Campus</p>
          <p className="activos-view__asset-value">{item.campus ?? "—"}</p>
        </div>
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-label">Edificio</p>
          <p className="activos-view__asset-value">{item.edificio ?? "—"}</p>
        </div>
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-label">Aula</p>
          <p className="activos-view__asset-value">{item.aula ?? "—"}</p>
        </div>
      </div>
    </div>
  );
}
