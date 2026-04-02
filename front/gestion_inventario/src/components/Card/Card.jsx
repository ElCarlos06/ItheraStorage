import "./Card.css";

export default function Card({ children, className = "", padding, ...props }) {
  const paddingClass = padding === "medium" ? "p-4" : "";
  return (
    <div className={`moon-card ${paddingClass} ${className}`.trim()} {...props}>
      {children}
    </div>
  );
}
