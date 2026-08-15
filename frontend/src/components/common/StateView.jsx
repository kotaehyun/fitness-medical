/**
 * [공부/면접] 로딩·에러·빈 상태 UI (StateView.jsx)
 *
 * Q. role="alert" vs role="status" ?
 * A. error는 alert(스크린리더 즉시 알림), loading/empty는 status(보조 정보).
 *
 * Q. 사용처는?
 * A. ProtectedRoute, MemberDashboard, HealthRecordsPage 등 데이터 fetch 공통 UX.
 */
import { AlertCircle, LoaderCircle } from 'lucide-react';
export function StateView({ type, message }) {
  return (
    <div className="state-view" role={type === 'error' ? 'alert' : 'status'}>
      {type === 'loading' ? <LoaderCircle className="spin" /> : <AlertCircle />}
      <p>{message}</p>
    </div>
  );
}
