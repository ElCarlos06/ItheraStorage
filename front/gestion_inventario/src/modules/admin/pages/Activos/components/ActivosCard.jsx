import { useState } from "react";
import Card from "../../../../../components/Card/Card";
import AssetInfo from "./AssetInfo";
import AssetActions from "./AssetActions";
import QrAssetModal from "./QrAssetModal";

export default function ActivosCard({
  item,
  onEliminar,
  onEditar,
  onHistorial,
  onDetalles,
}) {
  const [qrModalOpen, setQrModalOpen] = useState(false);

  return (
    <div className="activos-view__asset-card-wrap">
      <Card
        padding="medium"
        className="activos-view__asset-card"
        onClick={() => setQrModalOpen(true)}
        style={{ cursor: "pointer" }}
      >
        <AssetInfo item={item} />
        <AssetActions
          item={item}
          onEliminar={onEliminar}
          onEditar={onEditar}
          onHistorial={onHistorial}
          onDetalles={onDetalles}
        />
      </Card>

      {qrModalOpen && (
        <QrAssetModal
          open={qrModalOpen}
          onClose={() => setQrModalOpen(false)}
          item={item}
        />
      )}
    </div>
  );
}
