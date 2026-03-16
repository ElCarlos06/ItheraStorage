import React from 'react';
import Icon from "../../../../../components/Icon/Icon";
import {
  GenericDelete,
  GenericEdit,
  TimeTime,
  SecurityPassport,
} from "@heathmont/moon-icons";

export default function AssetActions({
  item,
  onEliminar,
  onEditar,
  onHistorial,
  onDetalles,
}) {
  return (
    <div
      className="activos-view__asset-actions"
      aria-label="Acciones del activo"
    >
      <button
        type="button"
        className="activos-view__action-btn activos-view__action-btn--delete"
        title="Eliminar"
        aria-label="Eliminar"
        onClick={(e) => {
          e.stopPropagation();
          onEliminar?.(item);
        }}
      >
        <Icon icon={GenericDelete} size={30} />
      </button>
      <button
        type="button"
        className="activos-view__action-btn"
        title="Editar"
        aria-label="Editar"
        onClick={(e) => {
          e.stopPropagation();
          onEditar?.(item);
        }}
      >
        <Icon icon={GenericEdit} size={30} />
      </button>
      <button
        type="button"
        className="activos-view__action-btn"
        title="Historial"
        aria-label="Historial"
        onClick={(e) => {
          e.stopPropagation();
          onHistorial?.(item);
        }}
      >
        <Icon icon={TimeTime} size={30} />
      </button>
      <button
        type="button"
        className="activos-view__action-btn"
        title="Detalles"
        aria-label="Detalles"
        onClick={(e) => {
          e.stopPropagation();
          onDetalles?.(item);
        }}
      >
        <Icon icon={SecurityPassport} size={30} />
      </button>
    </div>
  );
}
