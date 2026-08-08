/**
 * [공부/면접] 전문가 회원 상세·피드백 작성 (MemberDetailPage.jsx)
 *
 * Q. useParams id 기본값 'm1'?
 * A. URL 파라미터 없을 때 mock 데모 fallback(드물게 사용).
 *
 * Q. feedbackMutation invalidateQueries queryKey?
 * A. ['feedback', id] — 해당 회원 피드백 목록만 refetch.
 *
 * Q. textarea placeholder·안내 문구?
 * A. 코칭 톤 유도, 진단·처방 오해 방지(Disclaimer와 동일 정책).
 */
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
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { WeightChart } from '../../components/charts/HealthCharts';
import { MetricCard } from '../../components/common/MetricCard';
import { ProgressBar } from '../../components/common/ProgressBar';
import { StateView } from '../../components/common/StateView';
import { AppShell } from '../../components/layout/AppShell';
import { useFeedback, useMemberData, useRecords } from '../../hooks/useDashboardData';
import { healthService } from '../../services/healthService';
import { professionalNav } from './ProfessionalDashboard';
export function MemberDetailPage() {
  const { id = 'm1' } = useParams();
  const member = useMemberData(id),
    records = useRecords(id),
    feedback = useFeedback(id);
  const queryClient = useQueryClient();
  const [feedbackSuccess, setFeedbackSuccess] = useState(false);
  const feedbackMutation = useMutation({
    mutationFn: ({ memberId, content }) =>
      healthService.addFeedback(memberId, {
        author: '홍길동',
        role: '재활의학 전문가',
        content,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['feedback', id] });
      setFeedbackSuccess(true);
    },
  });
  if (member.isLoading || records.isLoading || feedback.isLoading)
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="loading" message="회원 상세 정보를 불러오고 있습니다." />
      </AppShell>
    );
  if (member.isError || records.isError || feedback.isError || !member.data || !records.data)
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="error" message="회원 정보를 확인할 수 없습니다." />
      </AppShell>
    );
  const m = member.data;
  const latestRecord = [...records.data].sort((a, b) => b.date.localeCompare(a.date))[0];
  const latestDetail = latestRecord ? `${latestRecord.date} 측정` : '측정 기록 없음';
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
          value={latestRecord ? `${latestRecord.systolic}/${latestRecord.diastolic}` : '—'}
          unit="mmHg"
          detail={latestDetail}
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
          value={latestRecord ? latestRecord.weight : m.weight || '—'}
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
          <form
            onSubmit={(event) => {
              event.preventDefault();
              setFeedbackSuccess(false);
              const form = event.currentTarget;
              const content = String(new FormData(form).get('content') || '').trim();
              feedbackMutation.mutate({ memberId: id, content }, {
                onSuccess: () => form.reset(),
              });
            }}
          >
            <label htmlFor="feedback">관리 의견</label>
            <textarea
              id="feedback"
              name="content"
              rows={6}
              placeholder="회원이 이해하기 쉬운 말로 건강 흐름과 생활 관리 의견을 작성해 주세요."
              minLength={10}
              required
            />
            <p>의료 진단이나 처방으로 오해할 수 있는 단정적 표현은 피해주세요.</p>
            {feedbackMutation.isError && (
              <p className="form-error" role="alert">
                {feedbackMutation.error.message || '피드백 등록에 실패했습니다.'}
              </p>
            )}
            {feedbackSuccess && <p className="form-success" role="status">피드백이 등록되었습니다.</p>}
            <button className="button primary" type="submit" disabled={feedbackMutation.isPending}>
              <Send />
              {feedbackMutation.isPending ? '등록 중...' : '피드백 등록'}
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
