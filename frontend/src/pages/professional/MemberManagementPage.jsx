/**
 * [공부/면접] 전문가 회원 관리 CRUD (MemberManagementPage.jsx)
 *
 * Q. updateMutation vs deleteMutation invalidateQueries?
 * A. 성공 시 ['members'] 캐시 무효화 → useMembers refetch로 테이블 갱신.
 *
 * Q. delete 시 window.confirm?
 * A. 클라이언트 UX 확인. 실제 삭제는 healthService.deleteMember → API 204.
 *
 * Q. 수정 가능 필드가 goal/progress만인 이유는?
 * A. 백엔드 PUT DTO와 동일 — PII·의료정보는 이 화면에서 다루지 않음.
 */
import { ArrowRight, Edit3, Trash2, Users } from 'lucide-react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { ProgressBar } from '../../components/common/ProgressBar';
import { StateView } from '../../components/common/StateView';
import { StatusBadge } from '../../components/common/StatusBadge';
import { AppShell } from '../../components/layout/AppShell';
import { useMembers } from '../../hooks/useDashboardData';
import { healthService } from '../../services/healthService';
import { professionalNav } from './ProfessionalDashboard';

export function MemberManagementPage() {
  const query = useMembers();
  const queryClient = useQueryClient();
  const [editingMember, setEditingMember] = useState(null);
  const updateMutation = useMutation({
    mutationFn: ({ id, goal, progress }) => healthService.updateMember(id, { goal, progress }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['members'] });
      setEditingMember(null);
    },
  });
  const deleteMutation = useMutation({
    mutationFn: healthService.deleteMember,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['members'] }),
  });

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
            <span>관리</span>
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
              <span className="member-actions">
                <button
                  type="button"
                  className="action-btn"
                  onClick={() => setEditingMember(member)}
                  disabled={deleteMutation.isPending}
                >
                  <Edit3 size={14} />
                  수정
                </button>
                <button
                  type="button"
                  className="action-btn"
                  onClick={() => {
                    if (window.confirm(member.name + ' 회원을 삭제할까요?')) {
                      deleteMutation.mutate(member.id);
                    }
                  }}
                  disabled={deleteMutation.isPending}
                >
                  <Trash2 size={14} />
                  삭제
                </button>
                <Link to={'/professional/members/' + member.id} className="action-btn detail">
                  상세보기
                  <ArrowRight size={14} />
                </Link>
              </span>
            </div>
          ))}
        </div>
      </section>
      {editingMember && (
        <MemberEditModal
          member={editingMember}
          pending={updateMutation.isPending}
          error={updateMutation.error?.message}
          onClose={() => setEditingMember(null)}
          onSubmit={(member) => updateMutation.mutate(member)}
        />
      )}
    </AppShell>
  );
}

function MemberEditModal({ member, pending, error, onClose, onSubmit }) {
  const [goal, setGoal] = useState(member.goal);
  const [progress, setProgress] = useState(member.progress);

  return (
    <div className="modal-backdrop" role="presentation">
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="member-edit-title">
        <div className="modal-head">
          <div>
            <h2 id="member-edit-title">회원 목표 수정</h2>
            <p>{member.name} 회원의 목표와 진행률을 수정합니다.</p>
          </div>
          <button className="icon-button" onClick={onClose} aria-label="닫기" disabled={pending}>
            ×
          </button>
        </div>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            onSubmit({ id: member.id, goal, progress: Number(progress) });
          }}
        >
          <label className="full-field">
            목표
            <input value={goal} onChange={(event) => setGoal(event.target.value)} required />
          </label>
          <label className="full-field">
            진행률 (%)
            <input
              type="number"
              min="0"
              max="100"
              value={progress}
              onChange={(event) => setProgress(event.target.value)}
              required
            />
          </label>
          {error && <p className="form-error" role="alert">{error}</p>}
          <div className="modal-actions">
            <button type="button" className="button secondary" onClick={onClose} disabled={pending}>
              취소
            </button>
            <button className="button primary" disabled={pending}>
              {pending ? '저장 중...' : '저장'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
