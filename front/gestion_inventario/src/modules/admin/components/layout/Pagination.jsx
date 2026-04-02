import { ChevronLeft, ChevronRight } from "lucide-react";
import "./Pagination.css";

/**
 * Paginador reutilizable.
 * currentPage es base-0 (0 = primera página).
 */
export default function Pagination({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  className = "",
}) {
  if (totalElements === 0) return null;

  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

  const isFirst = currentPage <= 0;
  const isLast = currentPage >= totalPages - 1;

  return (
    <div
      className={`pagination-container d-flex justify-content-between align-items-center py-3 w-100 small ${className}`}
    >
      <div className="pagination-info d-flex align-items-center gap-1">
        Mostrando <span className="pagination-bold">{startItem}</span> a{" "}
        <span className="pagination-bold">{endItem}</span> de{" "}
        <span className="pagination-bold">{totalElements}</span> registros
      </div>

      <div className="pagination-controls d-flex align-items-center gap-3">
        <button
          className={`pagination-btn d-flex justify-content-center align-items-center ${isFirst ? "disabled" : ""}`}
          onClick={() => !isFirst && onPageChange(currentPage - 1)}
          disabled={isFirst}
          aria-label="Página anterior"
          title="Página anterior"
        >
          <ChevronLeft size={18} />
        </button>

        <div className="pagination-page-info d-flex justify-content-center align-items-center">
          <span className="pagination-bold">{currentPage + 1}</span>
          &nbsp;/&nbsp;{totalPages}
        </div>

        <button
          className={`pagination-btn pagination-btn-next d-flex justify-content-center align-items-center ${isLast ? "disabled" : ""}`}
          onClick={() => !isLast && onPageChange(currentPage + 1)}
          disabled={isLast}
          aria-label="Página siguiente"
          title="Página siguiente"
        >
          <ChevronRight size={18} />
        </button>
      </div>
    </div>
  );
}
