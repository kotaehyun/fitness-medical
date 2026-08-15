/**
 * [공부/면접] 회원 전문가 피드백 목록 (MemberFeedbackPage.jsx)
 *
 * Q. useFeedback() 데이터 소스는?
 * A. healthService(mock 또는 api) — memberId는 useDashboardData가 resolve.
 *
 * Q. loading / error / empty 분기 패턴?
 * A. Early return + StateView — AppShell·nav는 각 상태에서도 유지.
 *
 * Q. 피드백 content 성격은?
 * A. 생활·운동 코칭 의견(진단·처방 아님).
 */
import { MessageSquareText } from 'lucide-react';
import { AppShell } from '../../components/layout/AppShell';
import { StateView } from '../../components/common/StateView';
import { useFeedback } from '../../hooks/useDashboardData';
import { memberNav } from './MemberDashboard';

export function MemberFeedbackPage() {
  const query = useFeedback();

  if (query.isLoading) {
    return (
      <AppShell nav={memberNav}>
        <StateView type="loading" message="전문가 피드백을 불러오는 중입니다." />
      </AppShell>
    );
  }

  if (query.isError || !query.data) {
    return (
      <AppShell nav={memberNav}>
        <StateView type="error" message="전문가 피드백을 불러오지 못했습니다." />
      </AppShell>
    );
  }

  if (!query.data.length) {
    return (
      <AppShell nav={memberNav}>
        <StateView type="empty" message="아직 등록된 전문가 피드백이 없습니다." />
      </AppShell>
    );
  }

  return (
    <AppShell nav={memberNav}>
      <div className="page-head">
        <div>
          <span className="page-kicker">PROFESSIONAL FEEDBACK</span>
          <h1>전문가 피드백</h1>
          <p>전문가가 남긴 건강 관리 의견을 확인하세요.</p>
        </div>
        <span className="button secondary compact">
          <MessageSquareText />
          {query.data.length}건의 피드백
        </span>
      </div>
      <section className="card feedback-history">
        <div className="card-head">
          <div>
            <h3>피드백 목록</h3>
            <span>최근 등록된 순서로 표시됩니다.</span>
          </div>
        </div>
        {query.data.map((feedback) => (
          <article key={feedback.id}>
            <span className="avatar">{feedback.author[0]}</span>
            <div>
              <b>
                {feedback.author} <small>{feedback.role}</small>
              </b>
              <time>{feedback.date}</time>
              <p>{feedback.content}</p>
            </div>
          </article>
        ))}
      </section>
    </AppShell>
  );
}
