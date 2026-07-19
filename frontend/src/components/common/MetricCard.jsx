import { StatusBadge } from './StatusBadge';
export function MetricCard({ icon: Icon, label, value, unit, detail, tone = 'mint' }) {
  return (
    <article className="metric-card">
      <div className={`icon-box ${tone}`}>
        <Icon size={20} />
      </div>
      <div className="metric-top">
        <span>{label}</span>
        <StatusBadge status="정상" />
      </div>
      <div className="metric-value">
        {value} <small>{unit}</small>
      </div>
      <p>{detail}</p>
    </article>
  );
}
