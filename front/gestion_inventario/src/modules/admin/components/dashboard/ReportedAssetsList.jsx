import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import "./ReportedAssetsList.css";

const RANK_COLORS = ["chichi", "krillin", "teal"];

export default function ReportedAssetsList({ items = [] }) {
  return (
    <div className="reported-assets-list">
      {items.map((item, index) => (
        <div key={item.id ?? index} className="reported-assets-list__item">
          <div
            className={`reported-assets-list__rank reported-assets-list__rank--${RANK_COLORS[index % RANK_COLORS.length]}`}
          >
            {index + 1}
          </div>
          <div className="reported-assets-list__content">
            <p className="reported-assets-list__name">{item.name}</p>
            {item.type && (
              <p className="reported-assets-list__type">Tipo: {item.type}</p>
            )}
            {item.status != null && (
              <StatusBadge status={item.status} size="small" className="reported-assets-list__status">
                {item.statusLabel ?? item.status}
              </StatusBadge>
            )}
          </div>
          <div className="reported-assets-list__meta">
            <span className="reported-assets-list__count">{item.reports}</span>
            <span className="reported-assets-list__label">reportes</span>
          </div>
        </div>
      ))}
    </div>
  );
}
