import { useMemo } from "react";
import * as AlertDialog from "@radix-ui/react-alert-dialog";
import Button from "../../../../../components/Button/Button";
import exitoSvg from "../../../../../assets/exito.svg";
import fantasmitaSvg from "../../../../../assets/fantasmita.svg";
import "./ImportResultModal.css";

/**
 * Parsea el message del backend y extrae las filas de error.
 * Formato esperado: "Fila 4: La etiqueta 'ABC' ya existe."
 * @param {string} message
 * @returns {{ summary: string, rows: { fila: string, motivo: string }[] }}
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
  const icon = isError ? fantasmitaSvg : exitoSvg;
  const title = isError ? "Importación con Errores" : "Importación Exitosa";

  const { summary, rows } = useMemo(() => parseImportMessage(message), [message]);
  const hasTable = rows.length > 0;

  return (
    <AlertDialog.Root open={open} onOpenChange={(o) => !o && onClose?.()}>
      <AlertDialog.Portal>
        <AlertDialog.Overlay className="import-result-modal__overlay" />
        <AlertDialog.Content
          className={`import-result-modal__content ${hasTable ? "import-result-modal__content--wide" : ""}`}
        >
          <div className="import-result-modal__inner d-flex flex-column align-items-center text-center gap-4">
            <div className="import-result-modal__icon d-flex align-items-center justify-content-center flex-shrink-0">
              <img src={icon} alt="" width={280} height={280} aria-hidden />
            </div>

            <AlertDialog.Title className="import-result-modal__title">
              {title}
            </AlertDialog.Title>

            <AlertDialog.Description asChild>
              <p className="import-result-modal__message">{summary}</p>
            </AlertDialog.Description>

            {hasTable && (
              <div className="import-result-modal__table-wrapper">
                <table className="import-result-modal__table">
                  <thead>
                    <tr>
                      <th>Fila</th>
                      <th>Motivo del rechazo</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rows.map((r, i) => (
                      <tr key={i}>
                        <td className="import-result-modal__cell-fila">{r.fila}</td>
                        <td>{r.motivo}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            <div className="import-result-modal__actions d-flex gap-3 justify-content-center align-items-center mt-2 w-100">
              <AlertDialog.Cancel asChild>
                <Button variant={isError ? "outline" : "primary"} size="medium" onClick={onClose}>
                  Aceptar
                </Button>
              </AlertDialog.Cancel>
            </div>
          </div>
        </AlertDialog.Content>
      </AlertDialog.Portal>
    </AlertDialog.Root>
  );
}
