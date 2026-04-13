import { useMemo } from "react";
import { X } from "lucide-react";
import Modal from "../../../../../components/Modal/Modal";
import Button from "../../../../../components/Button/Button";
import exitoSvg from "../../../../../assets/exito.svg";
import titeDodoriaSvg from "../../../../../assets/tite-dodoria.svg";
import "./ImportResultModal.css";

/**
 * Parsea el message del backend y extrae las filas de error.
 * Formato esperado: "Fila 4: La etiqueta 'ABC' ya existe."
 */
function parseImportMessage(message) {
  if (!message) return { summary: "", rows: [] };

  const lines = message.split("\n").filter(Boolean);
  const rows = [];
  const summaryLines = [];

  for (const line of lines) {
    const match = line.match(/^Fila\s+(\d+):\s*(.+)$/i);
    if (match) {
      rows.push({ fila: match[1], motivo: match[2] });
    } else if (!line.startsWith("Detalle de rechazos")) {
      summaryLines.push(line);
    }
  }

  return { summary: summaryLines.join("\n"), rows };
}

export default function ImportResultModal({
  open,
  onClose,
  type = "success",
  message = "",
}) {
  const isError = type === "error";
  const icon = isError ? titeDodoriaSvg : exitoSvg;
  const title = isError ? "Importación con errores" : "Importación exitosa";

  const { summary, rows } = useMemo(() => parseImportMessage(message), [message]);
  const hasTable = rows.length > 0;

  return (
    <Modal open={open} className={`import-result-modal ${hasTable ? "import-result-modal--wide" : ""}`}>
      <div className="irm d-flex flex-column">

        {/* Header */}
        <header className="irm__header d-flex align-items-center justify-content-between flex-shrink-0">
          <h2 className="irm__title m-0">{title}</h2>
          <button
            type="button"
            className="irm__close d-flex align-items-center justify-content-center p-0 border-0 bg-transparent rounded-3"
            onClick={onClose}
            aria-label="Cerrar"
          >
            <X size={24} strokeWidth={2.5} />
          </button>
        </header>

        {/* Scrollable body */}
        <div className="irm__body d-flex flex-column align-items-center text-center gap-4">

          <div className="irm__icon flex-shrink-0">
            <img src={icon} alt="" aria-hidden width={200} height={200} />
          </div>

          <p className="irm__message m-0">{summary}</p>

          {hasTable && (
            <div className="irm__table-wrapper">
              <table className="irm__table">
                <thead>
                  <tr>
                    <th>Fila</th>
                    <th>Motivo del rechazo</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((r, i) => (
                    <tr key={i}>
                      <td className="irm__cell-fila">{r.fila}</td>
                      <td>{r.motivo}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Footer */}
        <footer className="irm__footer d-flex justify-content-end flex-shrink-0">
          <Button variant="primary" size="medium" onClick={onClose}>
            Aceptar
          </Button>
        </footer>
      </div>
    </Modal>
  );
}
