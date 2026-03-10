import Icon from "../../../../components/Icon/Icon";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import "./StatCard.css";

export default function StatCard({ icon: IconComponent, value, label, badge }) {
  const status = badge?.status ?? "disponible";
  return (
    <div className="stat-card">
      <div className="stat-card__top">
        <div className="stat-card__icon">
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
