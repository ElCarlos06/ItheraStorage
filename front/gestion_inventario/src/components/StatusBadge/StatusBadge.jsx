export default function StatusBadge({ status, size = "small", children }) {
  return (
    <span className={`status-badge status-badge--${status} status-badge--${size}`}>
      {children}
    </span>
  );
}
