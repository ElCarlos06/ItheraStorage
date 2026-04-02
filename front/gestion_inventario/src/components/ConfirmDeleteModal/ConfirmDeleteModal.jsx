import { useState } from "react";
import * as AlertDialog from "@radix-ui/react-alert-dialog";
import Button from "../Button/Button";
import titeDodoriaSvg from "../../assets/tite-dodoria.svg";
import "./ConfirmDeleteModal.css";

export default function ConfirmDeleteModal({
  open,
  onClose,
  onConfirm,
  title = "¿Confirmar eliminación?",
  message = "Esta acción no se puede deshacer.",
  confirmLabel = "Eliminar",
  cancelLabel = "Cancelar",
}) {
  const [loading, setLoading] = useState(false);

  const handleConfirm = async (e) => {
    e?.preventDefault?.();
    setLoading(true);
    try {
      await onConfirm?.();
      onClose?.();
    } finally {
      setLoading(false);
    }
  };

  return (
    <AlertDialog.Root open={open} onOpenChange={(o) => !o && !loading && onClose?.()}>
      <AlertDialog.Portal>
        <AlertDialog.Overlay className="confirm-delete-modal__overlay" />
        <AlertDialog.Content className="confirm-delete-modal__content">
          <div className="confirm-delete-modal__inner d-flex flex-column align-items-center text-center gap-4">
            <div className="confirm-delete-modal__icon d-flex align-items-center justify-content-center flex-shrink-0">
              <img src={titeDodoriaSvg} alt="" width={280} height={280} aria-hidden />
            </div>
            <AlertDialog.Title className="confirm-delete-modal__title">{title}</AlertDialog.Title>
            <AlertDialog.Description asChild>
              <p className="confirm-delete-modal__message">{message}</p>
            </AlertDialog.Description>
            <div className="confirm-delete-modal__actions d-flex gap-3 justify-content-center align-items-center mt-2 w-100">
              <AlertDialog.Cancel asChild>
                <Button variant="outline" size="medium" disabled={loading}>{cancelLabel}</Button>
              </AlertDialog.Cancel>
              <Button
                variant="primary"
                size="medium"
                className="confirm-delete-modal__btn-delete"
                onClick={handleConfirm}
                disabled={loading}
              >
                {loading ? "Procesando…" : confirmLabel}
              </Button>
            </div>
          </div>
        </AlertDialog.Content>
      </AlertDialog.Portal>
    </AlertDialog.Root>
  );
}
