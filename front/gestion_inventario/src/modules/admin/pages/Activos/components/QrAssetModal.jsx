import { useState, useEffect } from "react";
import Modal from "../../../../../components/Modal/Modal";
import Button from "../../../../../components/Button/Button";
import { qrApi } from "../../../../../api/qrApi";
import { X, Download, FileDown, Printer, AlertCircle } from "lucide-react";
import "./QrAssetModal.css";

export default function QrAssetModal({ open, onClose, item }) {
  const [qrSrc, setQrSrc] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pdfLoading, setPdfLoading] = useState(false);

  useEffect(() => {
    if (!open || !item?.id) return;

    setLoading(true);
    setError(null);
    setQrSrc(null);

    qrApi
      .getQrByActivo(item.id)
      .then((res) => {
        if (!res.error && res.data) {
          setQrSrc(`data:image/png;base64,${res.data}`);
        } else {
          setError(res.message ?? "No se pudo obtener el QR.");
        }
      })
      .catch((err) => setError(err.message ?? "Error al cargar el QR."))
      .finally(() => setLoading(false));
  }, [open, item?.id]);

  const handleClose = () => {
    setQrSrc(null);
    setError(null);
    onClose?.();
  };

  const handleDownloadPng = () => {
    if (!qrSrc) return;
    const link = document.createElement("a");
    link.href = qrSrc;
    link.download = `qr_activo_${item?.codigo ?? item?.id ?? "activo"}.png`;
    link.click();
  };

  const handleDownloadPdf = () => {
    if (!item?.id) return;
    setPdfLoading(true);

    qrApi
      .getPdfByActivo(item.id)
      .then((res) => {
        if (!res.error && res.data) {
          const link = document.createElement("a");
          link.href = `data:application/pdf;base64,${res.data}`;
          link.download = `qr_activo_${item?.codigo ?? item.id}.pdf`;
          link.click();
        } else {
          setError(res.message ?? "No se pudo descargar el PDF.");
        }
      })
      .catch((err) => setError(err.message ?? "Error al descargar el PDF."))
      .finally(() => setPdfLoading(false));
  };

  const handlePrint = () => {
    if (!qrSrc) return;
    const win = window.open("", "_blank");
    win.document.write(`
      <html><head><title>QR - ${item?.codigo ?? ""}</title>
      <style>
        body { display:flex; flex-direction:column; align-items:center; justify-content:center; min-height:100vh; margin:0; font-family:sans-serif; }
        img { width: 280px; height: 280px; }
        p { margin: 12px 0 0; font-size: 18px; font-weight: bold; letter-spacing:0.05em; }
        span { font-size: 11px; color: #888; text-transform: uppercase; letter-spacing: 0.1em; display:block; margin-top:4px; }
      </style></head>
      <body>
        <img src="${qrSrc}" alt="QR" />
        <span>Código del activo</span>
        <p>${item?.codigo ?? item?.id ?? ""}</p>
      </body></html>
    `);
    win.document.close();
    win.focus();
    win.print();
  };

  return (
    <Modal open={open} onClose={handleClose} className="qr-asset-modal">
      <div className="qr-asset-modal__inner">

        <header className="qr-asset-modal__header">
          <div>
            <h2 className="qr-asset-modal__title">Código QR del Activo</h2>
            <p className="qr-asset-modal__subtitle">Escanea, descarga o imprime</p>
          </div>
          <button
            type="button"
            className="qr-asset-modal__close"
            onClick={handleClose}
            aria-label="Cerrar"
          >
            <X size={22} strokeWidth={2.5} />
          </button>
        </header>

        <div className="qr-asset-modal__card">
          <div className="qr-asset-modal__img-wrap">
            {loading && (
              <div className="qr-asset-modal__loading">
                <div className="qr-asset-modal__loading-dots">
                  <div className="qr-asset-modal__loading-dot" />
                  <div className="qr-asset-modal__loading-dot" />
                  <div className="qr-asset-modal__loading-dot" />
                </div>
                <p className="qr-asset-modal__state-text">Generando QR…</p>
              </div>
            )}
            {!loading && error && (
              <div className="qr-asset-modal__error">
                <div className="qr-asset-modal__error-icon">
                  <AlertCircle size={20} strokeWidth={2} />
                </div>
                <p className="qr-asset-modal__state-text qr-asset-modal__state-text--error">{error}</p>
              </div>
            )}
            {!loading && qrSrc && (
              <img
                className="qr-asset-modal__image"
                src={qrSrc}
                alt={`QR del activo ${item?.codigo ?? item?.id}`}
              />
            )}
          </div>

          <div className="qr-asset-modal__code-wrap">
            <span className="qr-asset-modal__code-label">Código del activo</span>
            <strong className="qr-asset-modal__code-value">
              {item?.codigo ?? item?.etiqueta ?? "—"}
            </strong>
          </div>
        </div>

        <div className="qr-asset-modal__actions">
          <Button
            variant="primary"
            fullWidth
            iconLeft={<Download size={18} strokeWidth={2} />}
            disabled={!qrSrc}
            onClick={handleDownloadPng}
          >
            Descargar Imagen (PNG)
          </Button>
          <Button
            variant="primary"
            fullWidth
            iconLeft={<FileDown size={18} strokeWidth={2} />}
            disabled={!qrSrc || pdfLoading}
            onClick={handleDownloadPdf}
          >
            {pdfLoading ? "Descargando…" : "Descargar PDF"}
          </Button>
          <Button
            variant="outline"
            fullWidth
            iconLeft={<Printer size={18} strokeWidth={2} />}
            disabled={!qrSrc}
            onClick={handlePrint}
          >
            Imprimir
          </Button>
        </div>

      </div>
    </Modal>
  );
}
