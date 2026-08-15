/**
 * [공부/면접] 회원 대시보드 (MemberDashboard.jsx)
 *
 * Q. memberNav export 이유는?
 * A. router PlaceholderPage, HealthRecordsPage 등에서 동일 사이드바 메뉴 재사용.
 *
 * Q. useMemberData()에 id를 안 넘기면?
 * A. resolveMemberId가 account-member-id 또는 demo MEMBER의 m1을 선택.
 *
 * Q. status/요약 문구는 의료 판정인가?
 * A. 아니다. lifestyle·코칭 맥락의 종합 흐름 표시(시연 데이터).
 */
import {
  Activity,
  ChevronRight,
  ClipboardList,
  Droplets,
  Footprints,
  HeartPulse,
  Home,
  MessageSquareText,
  Moon,
  Sparkles,
  Weight,
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { BloodPressureChart, StepsChart, WeightChart } from '../../components/charts/HealthCharts';
import { MetricCard } from '../../components/common/MetricCard';
import { ProgressBar } from '../../components/common/ProgressBar';
import { StateView } from '../../components/common/StateView';
import { AppShell } from '../../components/layout/AppShell';
import { useFeedback, useMemberData, useRecords } from '../../hooks/useDashboardData';
export const memberNav = [
  { label: '홈', path: '/member', icon: Home },
  { label: '건강 기록', path: '/member/records', icon: ClipboardList },
  { label: '생활 습관 안내', path: '/member/guide', icon: Sparkles },
  { label: '전문가 피드백', path: '/member/feedback', icon: MessageSquareText },
];
export function MemberDashboard() {
  const member = useMemberData();
  const records = useRecords();
  const feedback = useFeedback();
  if (member.isLoading || records.isLoading)
    return (
      <AppShell nav={memberNav}>
        <StateView type="loading" message="건강 데이터를 불러오고 있어요." />
      </AppShell>
    );
  if (member.isError || records.isError || !member.data || !records.data)
    return (
      <AppShell nav={memberNav}>
        <StateView type="error" message="데이터를 불러오지 못했습니다." />
      </AppShell>
    );
  const m = member.data,
    rs = records.data;
  const latestRecord = [...rs].sort((a, b) => b.date.localeCompare(a.date))[0];
  const latestDetail = latestRecord ? `${latestRecord.date} 측정` : '측정 기록 없음';
  return (
    <AppShell nav={memberNav}>
      <div className="page-head">
        <div>
          <span className="page-kicker">MEMBER DASHBOARD</span>
          <h1>
            안녕하세요, {m.name}님 <span>👋</span>
          </h1>
          <p>오늘도 건강한 하루를 만들어 볼까요?</p>
        </div>
        <Link className="button secondary compact" to="/member/records">
          + 건강 기록 추가
        </Link>
      </div>
      <section className="health-summary">
        <div className="summary-copy">
          <span className="summary-label">종합 건강상태</span>
          <div>
            <h2>{m.status}</h2>
            <span className="status status-양호">● 안정적인 흐름</span>
          </div>
          <p>
            최근 7일 동안 혈압과 혈당이 일정하게 유지되고 있어요. 걸음 수도 지난주보다{' '}
            <b>8% 증가</b>했습니다.
          </p>
        </div>
        <div className="summary-progress">
          <div
            className="donut"
            style={/** @type {import('react').CSSProperties} */ ({
              '--value': `${m.progress * 3.6}deg`,
            })}
          >
            <div>
              <b>{m.progress}%</b>
              <span>목표 달성률</span>
            </div>
          </div>
          <div>
            <b>이번 주 목표</b>
            <span>조금만 더 힘내세요!</span>
          </div>
        </div>
      </section>
      <div className="metric-grid">
        <MetricCard
          icon={HeartPulse}
          label="혈압"
          value={latestRecord ? `${latestRecord.systolic}/${latestRecord.diastolic}` : '—'}
          unit="mmHg"
          detail={latestDetail}
          tone="mint"
        />
        <MetricCard
          icon={Droplets}
          label="혈당"
          value={latestRecord ? latestRecord.bloodSugar : '—'}
          unit="mg/dL"
          detail={latestDetail}
          tone="blue"
        />
        <MetricCard
          icon={Weight}
          label="체중"
          value={latestRecord ? latestRecord.weight : '—'}
          unit="kg"
          detail={latestDetail}
          tone="navy"
        />
        <MetricCard
          icon={Footprints}
          label="걸음 수"
          value={latestRecord ? latestRecord.steps.toLocaleString() : '—'}
          unit="보"
          detail={latestRecord ? '최근 측정 기록' : '측정 기록 없음'}
          tone="amber"
        />
      </div>
      <div className="dashboard-grid">
        <section className="card chart-card wide">
          <CardHead title="최근 7일 혈압 변화" subtitle="단위: mmHg" />
          <BloodPressureChart data={rs} />
          <div className="legend">
            <span className="navy-dot">수축기</span>
            <span className="mint-dot">이완기</span>
          </div>
        </section>
        <section className="card chart-card">
          <CardHead title="최근 7일 걸음 수" subtitle="일일 목표 10,000보" />
          <StepsChart data={rs} />
        </section>
        <section className="card chart-card">
          <CardHead title="체중 변화" subtitle="최근 30일" />
          <WeightChart data={rs} />
        </section>
        <section className="card goals-card">
          <CardHead title="나의 건강 목표" link="전체 보기" />
          <Goal icon={Footprints} title="일일 걸음 10,000보" value={75} meta="7,500 / 10,000보" />
          <Goal icon={Activity} title="주 3회 운동" value={67} meta="2 / 3회" />
          <Goal icon={Moon} title="평균 수면 7시간" value={100} meta="평균 7시간" />
        </section>
        <section className="card feedback-card">
          <CardHead title="최근 전문가 피드백" link="전체 보기" />
          {feedback.data?.slice(0, 2).map((f, i) => (
            <article key={f.id}>
              <div className={`avatar ${i ? 'trainer' : ''}`}>{f.author[0]}</div>
              <div>
                <div className="feedback-meta">
                  <b>{f.author}</b>
                  <span>{f.role}</span>
                  <time>{f.date.replaceAll('-', '. ')}</time>
                </div>
                <p>{f.content}</p>
              </div>
            </article>
          ))}
        </section>
      </div>
    </AppShell>
  );
}
/**
 * @param {{ title: string, subtitle?: string, link?: string }} props
 */
function CardHead({ title, subtitle, link }) {
  return (
    <div className="card-head">
      <div>
        <h3>{title}</h3>
        {subtitle && <span>{subtitle}</span>}
      </div>
      {link && (
        <button>
          {link}
          <ChevronRight size={16} />
        </button>
      )}
    </div>
  );
}
function Goal({ icon: Icon, title, value, meta }) {
  return (
    <div className="goal-row">
      <span className="goal-icon">
        <Icon />
      </span>
      <div>
        <div className="goal-title">
          <b>{title}</b>
          <span>{meta}</span>
        </div>
        <ProgressBar value={value} />
      </div>
    </div>
  );
}
