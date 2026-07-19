export function ProgressBar({ value, label }) {
  return (
    <div className="progress-wrap" aria-label={label ?? `달성률 ${value}%`}>
      <div className="progress-track">
        <span style={{ width: `${Math.min(value, 100)}%` }} />
      </div>
      {label && <span>{value}%</span>}
    </div>
  );
}
