import {
  Activity,
  ArrowLeft,
  Droplets,
  Footprints,
  HeartPulse,
  Moon,
  Send,
  Weight,
} from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import { WeightChart } from '../../components/charts/HealthCharts';
import { MetricCard } from '../../components/common/MetricCard';
import { ProgressBar } from '../../components/common/ProgressBar';
import { StateView } from '../../components/common/StateView';
import { AppShell } from '../../components/layout/AppShell';
import { useFeedback, useMemberData, useRecords } from '../../hooks/useDashboardData';
import { professionalNav } from './ProfessionalDashboard';
export function MemberDetailPage() {
  const { id = 'm1' } = useParams();
  const member = useMemberData(id),
    records = useRecords('m1'),
    feedback = useFeedback('m1');
  if (member.isLoading || records.isLoading)
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="loading" message="회원 상세 정보를 불러오고 있습니다." />
      </AppShell>
    );
  if (!member.data || !records.data)
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="error" message="회원 정보를 확인할 수 없습니다." />
      </AppShell>
    );
  const m = member.data;
  return (
    <AppShell nav={professionalNav} professional>
      <Link to="/professional" className="back-link">
        <ArrowLeft />
        회원 목록으로
      </Link>
      <div className="detail-profile card">
        <div className="profile-identity">
          <span>{m.avatar}</span>
          <div>
            <span className="page-kicker">MEMBER PROFILE</span>
            <h1>{m.name}님</h1>
            <p>
              {m.gender} · {m.age}세 · {m.height}cm
            </p>
          </div>
        </div>
        <dl>
          <div>
            <dt>현재 목표</dt>
            <dd>{m.goal}</dd>
          </div>
          <div>
            <dt>관리 참고사항</dt>
            <dd>{m.conditions.join(', ')}</dd>
          </div>
          <div>
            <dt>최근 측정</dt>
            <dd>{m.lastMeasured}</dd>
          </div>
        </dl>
      </div>
      <div className="metric-grid">
        <MetricCard
          icon={HeartPulse}
          label="혈압"
          value="120/80"
          unit="mmHg"
          detail="최근 측정 기록"
        />
        <MetricCard
          icon={Droplets}
          label="혈당"
          value="102"
          unit="mg/dL"
          detail="최근 측정 기록"
          tone="blue"
        />
        <MetricCard
          icon={Weight}
          label="체중"
          value={`${m.weight}`}
          unit="kg"
          detail="최근 측정 기록"
          tone="navy"
        />
        <MetricCard
          icon={Footprints}
          label="걸음 수"
          value="7,500"
          unit="보"
          detail="일일 평균"
          tone="amber"
        />
      </div>
      <div className="detail-grid">
        <section className="card chart-card">
          <div className="card-head">
            <div>
              <h3>최근 30일 체중 변화</h3>
              <span>회원의 기록 흐름</span>
            </div>
          </div>
          <WeightChart data={records.data} />
        </section>
        <section className="card detail-goals">
          <div className="card-head">
            <div>
              <h3>건강 목표</h3>
              <span>현재 진행 상황</span>
            </div>
          </div>
          <div>
            <Target icon={Footprints} title="일일 걸음 10,000보" value={75} />
            <Target icon={Activity} title="주 3회 운동" value={67} />
            <Target icon={Moon} title="평균 수면 7시간" value={100} />
          </div>
        </section>
        <section className="card feedback-history">
          <div className="card-head">
            <div>
              <h3>이전 피드백</h3>
              <span>전문가의 관리 의견</span>
            </div>
          </div>
          {feedback.data?.map((f) => (
            <article key={f.id}>
              <span className="avatar">{f.author[0]}</span>
              <div>
                <b>
                  {f.author} <small>{f.role}</small>
                </b>
                <time>{f.date}</time>
                <p>{f.content}</p>
              </div>
            </article>
          ))}
        </section>
        <section className="card feedback-form">
          <div className="card-head">
            <div>
              <h3>새 피드백 작성</h3>
              <span>홍길동 · 재활의학 전문가</span>
            </div>
          </div>
          <form onSubmit={(e) => e.preventDefault()}>
            <label htmlFor="feedback">관리 의견</label>
            <textarea
              id="feedback"
              rows={6}
              placeholder="회원이 이해하기 쉬운 말로 건강 흐름과 생활 관리 의견을 작성해 주세요."
              minLength={10}
            />
            <p>의료 진단이나 처방으로 오해할 수 있는 단정적 표현은 피해주세요.</p>
            <button className="button primary">
              <Send />
              피드백 등록
            </button>
          </form>
        </section>
      </div>
    </AppShell>
  );
}
function Target({ icon: Icon, title, value }) {
  return (
    <div className="goal-row">
      <span className="goal-icon">
        <Icon />
      </span>
      <div>
        <div className="goal-title">
          <b>{title}</b>
          <span>{value}%</span>
        </div>
        <ProgressBar value={value} />
      </div>
    </div>
  );
}
