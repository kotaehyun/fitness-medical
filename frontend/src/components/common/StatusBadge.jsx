export function StatusBadge({ status }) {
  return <span className={`status status-${status.replace(' ', '-')}`}>{status}</span>;
}
