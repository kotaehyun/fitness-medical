import { ArrowRight, Users } from 'lucide-react';
import { Link } from 'react-router-dom';
import { ProgressBar } from '../../components/common/ProgressBar';
import { StateView } from '../../components/common/StateView';
import { StatusBadge } from '../../components/common/StatusBadge';
import { AppShell } from '../../components/layout/AppShell';
import { useMembers } from '../../hooks/useDashboardData';
import { professionalNav } from './ProfessionalDashboard';

export function MemberManagementPage() {
  const query = useMembers();

  if (query.isLoading) {
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="loading" message="회원 정보를 불러오고 있습니다." />
      </AppShell>
    );
  }

  if (query.isError || !query.data) {
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="error" message="회원 정보를 불러오지 못했습니다." />
      </AppShell>
    );
  }

  if (!query.data.length) {
    return (
      <AppShell nav={professionalNav} professional>
        <StateView type="empty" message="등록된 회원이 없습니다." />
      </AppShell>
    );
  }

  return (
    <AppShell nav={professionalNav} professional>
      <div className="page-head">
        <div>
          <span className="page-kicker">MEMBER MANAGEMENT</span>
          <h1>회원 관리</h1>
          <p>관리 중인 회원의 건강 상태와 목표 현황을 확인하세요.</p>
        </div>
        <span className="button secondary compact">
          <Users />
          {query.data.length}명 관리 중
        </span>
      </div>
      <section className="card member-list">
        <div className="card-head">
          <div>
            <h3>관리 회원 목록</h3>
            <span>회원별 최근 측정일과 목표 달성률입니다.</span>
          </div>
        </div>
        <div className="member-table">
          <div className="member-row header">
            <span>회원</span>
            <span>최근 측정일</span>
            <span>종합 상태</span>
            <span>목표 달성률</span>
            <span />
          </div>
          {query.data.map((member) => (
            <div className="member-row" key={member.id}>
              <span className="member-name">
                <i>{member.avatar}</i>
                <span>
                  <b>{member.name}</b>
                  <small>
                    {member.gender} · {member.age}세
                  </small>
                </span>
              </span>
              <span>{member.lastMeasured || '측정 기록 없음'}</span>
              <span>
                <StatusBadge status={member.status} />
              </span>
              <span className="row-progress">
                <ProgressBar value={member.progress} />
                <small>{member.progress}%</small>
              </span>
              <span>
                <Link to={'/professional/members/' + member.id} className="text-button">
                  상세보기
                  <ArrowRight size={14} />
                </Link>
              </span>
            </div>
          ))}
        </div>
      </section>
    </AppShell>
  );
}
