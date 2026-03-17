import { ChevronLeft, ChevronRight } from "lucide-react";
import "./Pagination.css";

export default function Pagination({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  className = "",
}) {
  if (totalElements === 0) {
    return null;
  }

  const startItem = (currentPage - 1) * pageSize + 1;
  const endItem = Math.min(currentPage * pageSize, totalElements);

  const prevPage = () => {
    if (currentPage > 1) {
      onPageChange(currentPage - 1);
    }
  };

  const nextPage = () => {
    if (currentPage < totalPages) {
      onPageChange(currentPage + 1);
    }
  };

  return (
    <div className={`pagination-container ${className}`}>
      <div className="pagination-info">
        Mostrando <span className="pagination-bold">{startItem}</span> a{" "}
        <span className="pagination-bold">{endItem}</span> de{" "}
        <span className="pagination-bold">{totalElements}</span> registros
      </div>

      <div className="pagination-controls">
        <button
          className={`pagination-btn ${currentPage <= 1 ? "disabled" : ""}`}
          onClick={prevPage}
          disabled={currentPage <= 1}
          aria-label="Página anterior"
        >
          <ChevronLeft size={18} />
        </button>

        <div className="pagination-page-info">
          <span className="pagination-bold">{currentPage}</span>&nbsp;/&nbsp;
          {totalPages}
        </div>

        <button
          className={`pagination-btn pagination-btn-next ${
            currentPage >= totalPages ? "disabled" : ""
          }`}
          onClick={nextPage}
          disabled={currentPage >= totalPages}
          aria-label="Página siguiente"
        >
          <ChevronRight size={18} />
        </button>
      </div>
    </div>
  );
}
