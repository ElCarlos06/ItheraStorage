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
    <div className="chart-card d-flex flex-column">
      <div className="chart-card__header d-flex align-items-center gap-3 mb-4">
        <div
          className={`chart-card__icon chart-card__icon--${iconBg} d-flex align-items-center justify-content-center flex-shrink-0`}
        >
          {IconComponent && <Icon icon={IconComponent} size={30} />}
        </div>
        <div className="chart-card__titles">
          <div className="chart-card__title-row d-flex align-items-center gap-2 flex-wrap">
            <h3 className="chart-card__title">{title}</h3>
            {statusBadge != null && (
              <StatusBadge
                status={statusBadge.status}
                size={statusBadge.size ?? "small"}
              >
                {statusBadge.text}
              </StatusBadge>
            )}
          </div>
          <p className="chart-card__subtitle">{subtitle}</p>
        </div>
      </div>
      <div className="chart-card__body flex-grow-1">{children}</div>
    </div>
  );
}
