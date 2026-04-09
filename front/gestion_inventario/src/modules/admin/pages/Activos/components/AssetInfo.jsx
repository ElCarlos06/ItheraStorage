import StatusBadge, { normalizeStatus } from "../../../../../components/StatusBadge/StatusBadge";

const LABELS_OPERATIVO = {
  disponible: "Disponible",
  reportado: "Reportado",
  mantenimiento: "En Mantenimiento",
  "en-proceso": "En Proceso",
  baja: "Baja",
};

const LABELS_CUSTODIA = {
  disponible: "Disponible",
  "en-proceso": "En Proceso",
  resguardado: "Resguardado",
  baja: "Baja",
  neutral: "—",
};

/** Devuelve la etiqueta legible para un estado */
function badgeLabel(estado, labelMap) {
  const norm = normalizeStatus(estado);
  return labelMap[norm] ?? estado ?? "—";
}

export default function AssetInfo({ item }) {
  const normOp  = normalizeStatus(item.estadoOperativo);
  const normCus = normalizeStatus(item.estadoCustodia);

  // Mostramos "operativo" solo cuando añade información adicional a la custodia
  const showOperativo = normOp !== "disponible" && normOp !== normCus;
  // Siempre mostramos custodia
  const showCustodia  = true;

  return (
    <div className="activos-view__asset-content">
      <div className="activos-view__asset-row activos-view__asset-row--1">
        <div className="activos-view__asset-col">
          <p className="activos-view__asset-code">{item.codigo ?? "—"}</p>
          <p className="activos-view__asset-desc">{item.descripcionCorta ?? "—"}</p>
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
          <div className="d-flex flex-wrap gap-2 align-items-center">
            {showCustodia && (
              <StatusBadge status={normCus} size="small">
                {badgeLabel(item.estadoCustodia, LABELS_CUSTODIA)}
              </StatusBadge>
            )}
            {showOperativo && (
              <StatusBadge status={normOp} size="small">
                {badgeLabel(item.estadoOperativo, LABELS_OPERATIVO)}
              </StatusBadge>
            )}
          </div>
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
