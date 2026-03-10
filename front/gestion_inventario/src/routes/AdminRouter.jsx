import { lazy, Suspense } from "react";
import { Route, Routes, Navigate } from "react-router-dom";
import MainLayout from "../modules/admin/components/layout/MainLayout";

const Dashboard = lazy(() => import("../modules/admin/pages/Dashboard/Dashboard"));
const Activos = lazy(() => import("../modules/admin/pages/Activos/Activos"));
const Users = lazy(() => import("../modules/admin/pages/Users/Users"));
const Requests = lazy(() => import("../modules/admin/pages/Requests/Requests"));
const Catalogs = lazy(() => import("../modules/admin/pages/Catalogs/Catalogs"));

function PageLoader() {
  return (
    <div style={{ padding: "2rem", textAlign: "center", color: "var(--moon-trunks)" }}>
      Cargando…
    </div>
  );
}

export default function AdminRouter() {
  return (
    <Suspense fallback={<PageLoader />}>
      <Routes>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/activos" element={<Activos />} />
          <Route path="/usuarios" element={<Users />} />
          <Route path="/solicitudes" element={<Requests />} />
          <Route path="/catalogos" element={<Catalogs />} />
        </Route>
      </Routes>
    </Suspense>
  );
}
