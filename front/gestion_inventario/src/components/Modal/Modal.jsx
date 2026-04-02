import "./Modal.css";

export default function Modal({ open, onClose, className = "", children }) {
  if (!open) return null;
  return (
    <div
      className="modal-overlay position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center p-4"
      onClick={onClose}
    >
      <div
        className={`modal-content ${className}`}
        onClick={(e) => e.stopPropagation()}
      >
        {children}
      </div>
    </div>
  );
}
