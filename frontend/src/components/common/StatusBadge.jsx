/**
 * [공부/면접] 상태 뱃지 (StatusBadge.jsx)
 *
 * Q. 역할은?
 * A. '양호'|'주의'|'확인 필요' 등 문자열을 CSS class(status-*)로 매핑해 표시.
 *
 * Q. status.replace(' ', '-') ?
 * A. '확인 필요' → status-확인-필요. 공백 포함 class명 CSS 이슈 방지.
 *
 * Q. 의료 진단 표시인가?
 * A. 아니다. 코칭·관리 흐름용 UI 라벨(시연).
 */
export function StatusBadge({ status }) {
  return <span className={`status status-${status.replace(' ', '-')}`}>{status}</span>;
}
