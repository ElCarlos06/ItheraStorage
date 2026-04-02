import Icon from "../../../../components/Icon/Icon";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import "./StatCard.css";

export default function StatCard({ icon: IconComponent, value, label, badge }) {
  const status = badge?.status ?? "disponible";
  const tip =
    label != null && value != null ? `${label}: ${value}` : (label ?? "");
  return (
    <div className="stat-card d-flex flex-column" title={tip || undefined}>
      <div className="stat-card__top d-flex align-items-start justify-content-between mb-3">
        <div className="stat-card__icon d-flex align-items-center justify-content-center">
          {IconComponent && <Icon icon={IconComponent} size={30} />}
        </div>
        {badge != null && (
          <StatusBadge status={status} size="small">
            {badge.text}
          </StatusBadge>
        )}
      </div>
      <p className="stat-card__value">{value}</p>
      <p className="stat-card__label">{label}</p>
    </div>
  );
}
