import { useState, useMemo } from "react";
import PageHeader from "../../components/dashboard/PageHeader";
import Pagination from "../../components/layout/Pagination";

export default function Requests() {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  
  // Placeholder data para mostrar que la paginación de esta vista funciona
  const mockRequests = useMemo(() => Array.from({ length: 25 }, (_, i) => ({ id: i + 1, title: `Solicitud pendiende #${i + 1}` })), []);
  
  const totalItems = mockRequests.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  const paginatedItems = useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    return mockRequests.slice(startIndex, startIndex + itemsPerPage);
  }, [currentPage, itemsPerPage]);

  return (
    <div style={{ paddingBottom: "2rem" }}>
      <PageHeader
        overline="Panel de Control - Administrador"
        title="Solicitudes"
        subtitle="Revisa y gestiona solicitudes de activos y mantenimientos"
      />

      <div style={{ marginTop: "2rem", display: "flex", flexDirection: "column", gap: "1rem" }}>
        {paginatedItems.map(req => (
          <div key={req.id} style={{ padding: "1.5rem", background: "var(--moon-gohan, #fff)", borderRadius: "12px", border: "1px solid var(--moon-beerus, #eee)" }}>
            <p style={{ margin: 0, fontWeight: "500", color: "var(--moon-bulma, #000)" }}>{req.title}</p>
          </div>
        ))}
      </div>

      <div style={{ marginTop: "2rem" }}>
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          totalElements={totalItems}
          pageSize={itemsPerPage}
          onPageChange={setCurrentPage}
        />
      </div>
    </div>
  );
}
