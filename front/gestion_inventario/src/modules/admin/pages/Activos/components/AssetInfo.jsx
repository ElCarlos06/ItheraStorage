import { useEffect, useState } from "react";
import StatusBadge from "../../../../../components/StatusBadge/StatusBadge";
import { resguardosApi } from "../../../../../api/resguardosApi";

export const statusLabel = (s) => {
  const labels = {
    disponible: "Disponible",
    resguardado: "Resguardado",
    mantenimiento: "Mantenimiento",
    "en proceso": "En proceso",
    baja: "Baja",
    reportado: "Reportado",
  };
  return labels[s] ?? s;
};

export default function AssetInfo({ item }) {
  const [asignadoA, setAsignadoA] = useState("—");

  useEffect(() => {
    if (!item?.id) return;

    resguardosApi
      .getByActivo(item.id)
      .then((res) => {
        const list = res?.data ?? res ?? [];
        const activo = list.find(
          (r) =>
            r.estadoResguardo === "Pendiente" ||
            r.estadoResguardo === "Confirmado",
        );
        const nombre =
          activo?.usuarioEmpleado?.nombreCompleto ??
          activo?.usuarioEmpleado?.nombre ??
          null;
        setAsignadoA(nombre ?? "—");
      })
      .catch(() => setAsignadoA("—"));
  }, [item?.id]);

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
          <p className="activos-view__asset-value">{asignadoA ?? "—"}</p>
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
