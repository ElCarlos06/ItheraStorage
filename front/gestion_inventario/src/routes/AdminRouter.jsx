import { Route, Routes } from "react-router-dom";

export default function AdminRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" />} />
      <Route path="/dashboard" element={<h1>AdminRouter</h1>} />
    </Routes>
  );
}
