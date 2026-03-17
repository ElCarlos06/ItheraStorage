import { lazy, Suspense, useEffect } from "react";
import { Route, Routes, Navigate } from "react-router-dom";
import MainLayout from "../modules/admin/components/layout/MainLayout";

const pageImports = {
  Dashboard: () => import("../modules/admin/pages/Dashboard/Dashboard"),
  ActivosPage: () => import("../modules/admin/pages/Activos/ActivosPage"),
  Users: () => import("../modules/admin/pages/Users/Users"),
  Requests: () => import("../modules/admin/pages/Requests/Requests"),
  Catalogs: () => import("../modules/admin/pages/Catalogs/Catalogs"),
  Settings: () => import("../modules/admin/pages/Settings/Settings"),
};

const Dashboard = lazy(pageImports.Dashboard);
const ActivosPage = lazy(pageImports.ActivosPage);
const Users = lazy(pageImports.Users);
const Requests = lazy(pageImports.Requests);
const Catalogs = lazy(pageImports.Catalogs);
const Settings = lazy(pageImports.Settings);

function usePrefetchRoutes() {
  useEffect(() => {
    const id = requestIdleCallback ?? setTimeout;
    const handle = id(() => {
      Object.values(pageImports).forEach((fn) => fn());
    }, { timeout: 2000 });
    return () => (cancelIdleCallback ?? clearTimeout)(handle);
  }, []);
}

export default function AdminRouter() {
  usePrefetchRoutes();

  return (
    <Suspense fallback={null}>
      <Routes>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/activos" element={<ActivosPage />} />
          <Route path="/usuarios" element={<Users />} />
          <Route path="/solicitudes" element={<Requests />} />
          <Route path="/catalogos" element={<Catalogs />} />
          <Route path="/ajustes" element={<Settings />} />
        </Route>
      </Routes>
    </Suspense>
  );
}
