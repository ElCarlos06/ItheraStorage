import Icon from "../../../../components/Icon/Icon";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import "./ChartCard.css";

export default function ChartCard({
  icon: IconComponent,
  title,
  subtitle,
  iconBg = "piccolo",
  statusBadge,
  children,
}) {
  return (
    <div className="chart-card">
      <div className="chart-card__header">
        <div className={`chart-card__icon chart-card__icon--${iconBg}`}>
          {IconComponent && <Icon icon={IconComponent} size={30} />}
        </div>
        <div className="chart-card__titles">
          <div className="chart-card__title-row">
            <h3 className="chart-card__title">{title}</h3>
            {statusBadge != null && (
              <StatusBadge status={statusBadge.status} size={statusBadge.size ?? "small"}>
                {statusBadge.text}
              </StatusBadge>
            )}
          </div>
          <p className="chart-card__subtitle">{subtitle}</p>
        </div>
      </div>
      <div className="chart-card__body">{children}</div>
    </div>
  );
}
