import "./StatusBadge.css";

const KNOWN_STATUSES = ["disponible", "resguardado", "mantenimiento", "en-proceso", "baja", "reportado", "neutral"];

export function normalizeStatus(s) {
  if (!s || typeof s !== "string") return "disponible";
  const lower = s.trim().toLowerCase();
  const map = {
    "en mantenimiento": "mantenimiento",
    "en proceso": "en-proceso",
    "enproceso": "en-proceso",
    "proc": "en-proceso",
    "resguardo": "resguardado",
    "resg": "resguardado",
    "custodia resguardado": "resguardado",
    "custodia-resguardado": "resguardado",
    "disp": "disponible",
    "rep": "reportado",
    "ok": "disponible",
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
