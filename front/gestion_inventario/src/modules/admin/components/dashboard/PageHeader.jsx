import "./PageHeader.css";

export default function PageHeader({
  overline,
  title,
  subtitle,
}) {
  return (
    <header className="page-header">
      {overline && (
        <p className="page-header__overline mb-2">{overline}</p>
      )}
      {title && (
        <h1 className="page-header__title mb-2">{title}</h1>
      )}
      {subtitle && (
        <p className="page-header__subtitle">{subtitle}</p>
      )}
    </header>
  );
}
