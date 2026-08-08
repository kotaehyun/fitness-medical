/**
 * [공부/면접] 건강 지표 카드 (MetricCard.jsx)
 *
 * Q. 역할은?
 * A. 혈압·혈당·체중·걸음 등 단일 metric을 아이콘+값+단위로 카드화.
 *
 * Q. StatusBadge "정상" 고정?
 * A. 시연 UI — 실제 임상 판정이 아닌 lifestyle 기록 요약 표시.
 */
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
