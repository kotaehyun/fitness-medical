import {
  Activity,
  CalendarDays,
  ChevronRight,
  ClipboardList,
  Droplets,
  Footprints,
  HeartPulse,
  Home,
  MessageSquareText,
  Moon,
  Settings,
  Target,
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
  { label: '목표 관리', path: '/member/goals', icon: Target },
  { label: '전문가 피드백', path: '/member/feedback', icon: MessageSquareText },
  { label: '예약', path: '/member/appointments', icon: CalendarDays },
  { label: '설정', path: '/member/settings', icon: Settings },
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
          <div className="donut" style={{ '--value': `${m.progress * 3.6}deg` }}>
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
          value="120/80"
          unit="mmHg"
          detail="최근 측정 · 오늘 오전 8:20"
          tone="mint"
        />
        <MetricCard
          icon={Droplets}
          label="혈당"
          value="102"
          unit="mg/dL"
          detail="최근 측정 · 오늘 오전 8:25"
          tone="blue"
        />
        <MetricCard
          icon={Weight}
          label="체중"
          value="68.0"
          unit="kg"
          detail="지난주 대비 -0.5kg"
          tone="navy"
        />
        <MetricCard
          icon={Footprints}
          label="걸음 수"
          value="7,500"
          unit="보"
          detail="일일 목표의 75%"
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
