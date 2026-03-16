import "./Card.css";

export default function Card({ children, className = "", padding, ...props }) {
  const paddingClass = padding ? `moon-card--padding-${padding}` : "";
  return (
    <div className={`moon-card ${paddingClass} ${className}`.trim()} {...props}>
      {children}
    </div>
  );
}
