import "./StatusBadge.css";

const KNOWN_STATUSES = ["disponible", "resguardado", "mantenimiento", "en-proceso", "baja", "reportado", "neutral"];

/** Normaliza el status a minúsculas para las clases CSS */
function normalizeStatus(s) {
  if (!s || typeof s !== "string") return "disponible";
  const lower = s.trim().toLowerCase();
  const map = {
    "en mantenimiento": "mantenimiento",
    "en proceso": "en-proceso",
  };
  const normalized = map[lower] ?? lower.replace(/\s+/g, "-");
  return KNOWN_STATUSES.includes(normalized) ? normalized : "neutral";
}

export default function StatusBadge({ status, size = "small", children }) {
  const normalized = normalizeStatus(status);
  return (
    <span className={`status-badge status-badge--${normalized} status-badge--${size}`}>
      {children}
    </span>
  );
}
