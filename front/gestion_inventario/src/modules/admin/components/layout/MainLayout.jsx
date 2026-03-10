import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import "./MainLayout.css";

export default function MainLayout() {
  return (
    <div className="admin-layout">
      <Sidebar />
      <main className="admin-main">
        <Outlet />
      </main>
    </div>
  );
}
