import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import "./ReportedAssetsList.css";

const RANK_COLORS = ["chichi", "krillin", "teal"];

export default function ReportedAssetsList({ items = [] }) {
  return (
    <div className="reported-assets-list d-flex flex-column gap-3 flex-grow-1 min-h-0">
      {items.map((item, index) => (
        <div
          key={item.id ?? index}
          className="reported-assets-list__item d-flex align-items-center gap-3 py-3 px-3"
        >
          <div
            className={`reported-assets-list__rank reported-assets-list__rank--${RANK_COLORS[index % RANK_COLORS.length]} d-flex align-items-center justify-content-center flex-shrink-0`}
          >
            {index + 1}
          </div>
          <div className="reported-assets-list__content flex-grow-1 min-w-0">
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
          <div className="reported-assets-list__meta d-flex flex-column align-items-end flex-shrink-0">
            <span className="reported-assets-list__count">{item.reports}</span>
            <span className="reported-assets-list__label">reportes</span>
          </div>
        </div>
      ))}
    </div>
  );
}
