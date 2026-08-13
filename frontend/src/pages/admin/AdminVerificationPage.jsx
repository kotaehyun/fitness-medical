/**
 * [공부/면접] 관리자 종합 현황 — 일반 회원 가입 + 전문직 인증
 *
 * Q. 왜 슈퍼계정이 회원 목록도 보나?
 * A. 승인만 보면 가입 현황을 모른다. 일반 회원·전문가·인증 대기를 한 화면에서 본다.
 *
 * Q. licenseNumber·키·체중을 보여주나?
 * A. 보여주지 않는다. 가입 여부·상태·목표·인증만 본다.
 */
import {
  ShieldCheck,
  UserRound,
  UserRoundCheck,
  UserRoundX,
  Users,
} from 'lucide-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateView } from '../../components/common/StateView';
import { StatusBadge } from '../../components/common/StatusBadge';
import { AppShell } from '../../components/layout/AppShell';
import { apiService } from '../../services/apiService';

export const adminNav = [
  { label: '종합 현황', path: '/admin', icon: ShieldCheck },
];

function professionalTypeLabel(type) {
  if (type === 'TRAINER') return '운동 전문가';
  if (type === 'PHYSICIAN') return '전문의';
  return type || '-';
}

function SummaryCard({ icon: Icon, label, value, detail }) {
  return (
    <article className="summary-card">
      <div className="icon-box mint">
        <Icon size={20} />
      </div>
      <div>
        <span>{label}</span>
        <b>{value}</b>
        <small>{detail}</small>
      </div>
    </article>
  );
}

export function AdminVerificationPage() {
  const queryClient = useQueryClient();
  const membersQuery = useQuery({
    queryKey: ['admin', 'members'],
    queryFn: apiService.getMembersForAdmin,
    retry: false,
  });
  const professionalsQuery = useQuery({
    queryKey: ['admin', 'professionals'],
    queryFn: apiService.getProfessionalsForAdmin,
    retry: false,
  });
  const verifyMutation = useMutation({
    mutationFn: apiService.verifyProfessional,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin', 'professionals'] }),
  });
  const revokeMutation = useMutation({
    mutationFn: apiService.revokeProfessional,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin', 'professionals'] }),
  });

  const loading = membersQuery.isLoading || professionalsQuery.isLoading;
  const failed = membersQuery.isError || professionalsQuery.isError
    || !membersQuery.data || !professionalsQuery.data;

  if (loading) {
    return (
      <AppShell nav={adminNav} workspaceLabel="관리자 워크스페이스">
        <StateView type="loading" message="가입·인증 현황을 불러오고 있습니다." />
      </AppShell>
    );
  }

  if (failed) {
    return (
      <AppShell nav={adminNav} workspaceLabel="관리자 워크스페이스">
        <StateView type="error" message="가입·인증 현황을 불러오지 못했습니다." />
      </AppShell>
    );
  }

  const members = membersQuery.data;
  const professionals = professionalsQuery.data;
  const pendingCount = professionals.filter((account) => !account.professionalVerified).length;
  const busy = verifyMutation.isPending || revokeMutation.isPending;

  return (
    <AppShell nav={adminNav} workspaceLabel="관리자 워크스페이스">
      <div className="page-head">
        <div>
          <span className="page-kicker">ADMIN</span>
          <h1>가입·인증 현황</h1>
          <p>일반 회원 가입과 전문직 인증을 한곳에서 확인합니다.</p>
        </div>
      </div>
      <div className="pro-summary admin-summary">
        <SummaryCard
          icon={UserRound}
          label="일반 회원"
          value={String(members.length)}
          detail="로그인 계정이 있는 회원"
        />
        <SummaryCard
          icon={Users}
          label="전문직"
          value={String(professionals.length)}
          detail="트레이너·전문의 계정"
        />
        <SummaryCard
          icon={ShieldCheck}
          label="인증 대기"
          value={String(pendingCount)}
          detail="승인 전 전문직"
        />
      </div>

      <section className="panel member-list">
        <div className="card-head">
          <div>
            <h3>일반 회원 가입 현황</h3>
            <span>공개 가입한 MEMBER 계정입니다. 키·체중은 표시하지 않습니다.</span>
          </div>
        </div>
        {members.length === 0 ? (
          <StateView type="empty" message="가입한 일반 회원이 없습니다." />
        ) : (
          <div className="member-table">
            <div className="member-row header admin-member-row">
              <span>계정</span>
              <span>성별</span>
              <span>나이</span>
              <span>목표</span>
              <span>상태</span>
            </div>
            {members.map((account) => (
              <div key={account.accountId} className="member-row admin-member-row">
                <div className="member-name">
                  <i>{(account.displayName || '?')[0]}</i>
                  <span>
                    <b>{account.displayName}</b>
                    <small>{account.loginId}</small>
                  </span>
                </div>
                <span>{account.gender || '-'}</span>
                <span>{account.age ?? '-'}</span>
                <span>{account.goal || '-'}</span>
                <StatusBadge status={account.status || '확인 필요'} />
              </div>
            ))}
          </div>
        )}
      </section>

      <section className="panel member-list admin-pro-panel">
        <div className="card-head">
          <div>
            <h3>전문직 인증</h3>
            <span>공개 가입 전문가는 승인 전까지 회원 API를 쓸 수 없습니다.</span>
          </div>
        </div>
        {professionals.length === 0 ? (
          <StateView type="empty" message="등록된 전문직 계정이 없습니다." />
        ) : (
          <div className="member-table">
            <div className="member-row header admin-verify-row">
              <span>계정</span>
              <span>유형</span>
              <span>인증</span>
              <span>처리</span>
            </div>
            {professionals.map((account) => (
              <div key={account.id} className="member-row admin-verify-row">
                <div className="member-name">
                  <i>{(account.displayName || '?')[0]}</i>
                  <span>
                    <b>{account.displayName}</b>
                    <small>{account.loginId}</small>
                  </span>
                </div>
                <span>{professionalTypeLabel(account.professionalType)}</span>
                <StatusBadge status={account.professionalVerified ? '양호' : '확인 필요'} />
                <div className="member-actions">
                  {account.professionalVerified ? (
                    <button
                      type="button"
                      className="action-btn"
                      disabled={busy}
                      onClick={() => revokeMutation.mutate(account.id)}
                    >
                      <UserRoundX size={14} />
                      해제
                    </button>
                  ) : (
                    <button
                      type="button"
                      className="action-btn detail"
                      disabled={busy}
                      onClick={() => verifyMutation.mutate(account.id)}
                    >
                      <UserRoundCheck size={14} />
                      승인
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </AppShell>
  );
}
