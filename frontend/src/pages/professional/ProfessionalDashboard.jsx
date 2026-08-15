/**
 * [공부/면접] 전문가 대시보드 (ProfessionalDashboard.jsx)
 *
 * Q. professionalNav export 이유는?
 * A. router·PlaceholderPage·하위 professional 페이지에서 공통 사이드바.
 *
 * Q. useMembers() vs useMemberData(id)?
 * A. 전문가는 전체 회원 목록 조회. 회원 상세는 URL :id로 개별 훅 호출.
 *
 * Q. pro-summary 숫자(5, 2, 3…)는 API인가?
 * A. 일부는 query.data, 일부는 시연용 정적 카드 — 데모 UI 혼합.
 */
import {
  AlertCircle,
  BellRing,
  CalendarCheck,
  ChevronRight,
  FileCheck2,
  Gauge,
  MessageSquareText,
  MessagesSquare,
  UserRound,
  Users,
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { ProgressBar } from '../../components/common/ProgressBar';
import { StateView } from '../../components/common/StateView';
import { StatusBadge } from '../../components/common/StatusBadge';
import { AppShell } from '../../components/layout/AppShell';
import { useAuthSession } from '../../hooks/useAuthSession';
import { useMembers } from '../../hooks/useDashboardData';
export const professionalNav = [
  { label: '대시보드', path: '/professional', icon: Gauge },
  { label: '회원 관리', path: '/professional/members', icon: Users },
  { label: '메시지', path: '/professional/messages', icon: MessagesSquare },
];
export function ProfessionalDashboard() {
  const { account, demoRole } = useAuthSession();
  const pendingVerification = !demoRole && account?.professionalVerified === false;
  const canListMembers =
    demoRole === 'PROFESSIONAL' || account?.professionalVerified === true;
  const query = useMembers({ enabled: canListMembers });
  if (pendingVerification) {
    return (
      <AppShell nav={professionalNav} professional>
        <StateView
          type="empty"
          message="전문직 인증 대기 중입니다. 관리자(admin01) 승인 후 회원 목록을 볼 수 있습니다."
        />
      </AppShell>
    );
  }
  if (query.isLoading)
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="loading" message="회원 현황을 불러오고 있습니다." />
      </AppShell>
    );
  if (query.isError || !query.data)
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="error" message="회원 정보를 불러오지 못했습니다." />
      </AppShell>
    );
  if (!query.data.length)
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="empty" message="등록된 회원이 없습니다." />
      </AppShell>
    );
  return (
    <AppShell nav={professionalNav} professional>
      <div className="page-head">
        <div>
          <span className="page-kicker">PROFESSIONAL DASHBOARD</span>
          <h1>좋은 아침입니다, 홍길동 전문가님</h1>
          <p>회원들의 오늘 건강 흐름을 확인해 보세요.</p>
        </div>
        <button className="button primary compact">
          <MessageSquareText />
          피드백 작성
        </button>
      </div>
      <div className="pro-summary">
        <Summary icon={Users} label="관리 회원 수" value="5" detail="이번 달 +1명" />
        <Summary
          icon={AlertCircle}
          label="주의가 필요한 회원"
          value="2"
          detail="확인이 필요해요"
          tone="amber"
        />
        <Summary
          icon={CalendarCheck}
          label="오늘 예약"
          value="3"
          detail="다음 예약 14:00"
          tone="blue"
        />
        <Summary
          icon={BellRing}
          label="미확인 기록"
          value="6"
          detail="새 기록이 있어요"
          tone="navy"
        />
      </div>
      <div className="pro-grid">
        <section className="card member-list">
          <div className="card-head">
            <div>
              <h3>관리 회원</h3>
              <span>최근 건강 기록과 목표 현황입니다.</span>
            </div>
            <button>
              전체 보기
              <ChevronRight />
            </button>
          </div>
          <div className="member-table">
            <div className="member-row header">
              <span>회원</span>
              <span>최근 측정일</span>
              <span>종합 상태</span>
              <span>목표 달성률</span>
              <span />
            </div>
            {query.data.map((m) => (
              <div className="member-row" key={m.id}>
                <span className="member-name">
                  <i>{m.avatar}</i>
                  <span>
                    <b>{m.name}</b>
                    <small>
                      {m.gender} · {m.age}세
                    </small>
                  </span>
                </span>
                <span>{m.lastMeasured}</span>
                <span>
                  <StatusBadge status={m.status} />
                </span>
                <span className="row-progress">
                  <ProgressBar value={m.progress} />
                  <small>{m.progress}%</small>
                </span>
                <span>
                  <Link to={`/professional/members/${m.id}`} className="text-button">
                    상세보기
                  </Link>
                </span>
              </div>
            ))}
          </div>
        </section>
        <aside className="side-stack">
          <section className="card alerts">
            <div className="card-head">
              <div>
                <h3>건강 흐름 알림</h3>
                <span>부드러운 확인이 필요한 항목</span>
              </div>
            </div>
            <Alert name="한미정" status="확인 필요" text="최근 3일간 활동 기록이 없습니다." />
            <Alert name="박정호" status="주의" text="걸음 목표 달성률이 평소보다 낮아요." />
            <Alert name="김순자" status="양호" text="이번 주 기록이 안정적으로 이어지고 있어요." />
          </section>
          <section className="card activity-list">
            <div className="card-head">
              <div>
                <h3>최근 활동</h3>
              </div>
            </div>
            {[
              {
                icon: FileCheck2,
                title: '새 건강기록',
                text: '김순자님이 혈압을 기록했습니다.',
                time: '10분 전',
              },
              {
                icon: UserRound,
                title: '목표 변경',
                text: '박정호님의 걷기 목표가 변경됐습니다.',
                time: '1시간 전',
              },
              {
                icon: MessageSquareText,
                title: '피드백 작성',
                text: '김길명 전문가가 피드백을 남겼습니다.',
                time: '3시간 전',
              },
            ].map(({ icon: Icon, ...a }) => (
              <article key={a.title}>
                <span>
                  <Icon />
                </span>
                <div>
                  <b>{a.title}</b>
                  <p>{a.text}</p>
                  <time>{a.time}</time>
                </div>
              </article>
            ))}
          </section>
        </aside>
      </div>
    </AppShell>
  );
}
function Summary({ icon: Icon, label, value, detail, tone = 'mint' }) {
  return (
    <article className="summary-card">
      <span className={`icon-box ${tone}`}>
        <Icon />
      </span>
      <div>
        <span>{label}</span>
        <b>{value}</b>
        <small>{detail}</small>
      </div>
    </article>
  );
}
function Alert({ name, status, text }) {
  return (
    <article className="alert-row">
      <span className="avatar">{name[0]}</span>
      <div>
        <div>
          <b>{name}</b>
          <StatusBadge status={status} />
        </div>
        <p>{text}</p>
      </div>
    </article>
  );
}
