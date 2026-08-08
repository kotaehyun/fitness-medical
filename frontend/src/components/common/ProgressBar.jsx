/**
 * [공부/면접] 목표 달성률 바 (ProgressBar.jsx)
 *
 * Q. Math.min(value, 100) ?
 * A. 100% 초과 입력 시 bar overflow 방지(회원 progress 필드 검증 보완).
 *
 * Q. aria-label ?
 * A. 시각 bar만 있는 UI에 접근성 이름 제공.
 */
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
