/**
 * [공부/면접] 대시보드 데이터 훅 (useDashboardData.js)
 *
 * Q. 페이지가 healthService를 직접 호출하지 않고 훅을 쓰는 이유는?
 * A. TanStack Query 설정(queryKey, enabled, 캐시)을 한곳에 모아
 *    로딩/에러/재조회 정책을 일관되게 유지한다.
 *
 * Q. queryKey에 memberId를 넣는 이유는?
 * A. 회원별 캐시 분리. ['records', 'm1']과 ['records', 'm2']는 별도 엔트리.
 *
 * Q. enabled: Boolean(memberId) 는?
 * A. memberId가 null/undefined면 쿼리를 실행하지 않는다(enabled: false).
 *    PROFESSIONAL이 id 없이 훅을 호출해도 불필요한 GET·에러를 방지.
 *
 * Q. resolveMemberId 우선순위는?
 * A. 1) 인자 id → 2) sessionStorage 'account-member-id'(로그인 MEMBER)
 *    → 3) demo MEMBER면 'm1' → 그 외 null
 */
import { useQuery } from '@tanstack/react-query';
import { healthService } from '../services/healthService';

function resolveMemberId(memberId) {
  if (memberId) return memberId;

  const accountMemberId = sessionStorage.getItem('account-member-id');
  if (accountMemberId) return accountMemberId;

  // [면접] 데모 "일반 회원 체험" — fitness-demo-role=MEMBER 이면 m1만 사용
  return sessionStorage.getItem('fitness-demo-role') === 'MEMBER' ? 'm1' : null;
}

export function useMemberData(id) {
  const memberId = resolveMemberId(id);
  return useQuery({
    queryKey: ['member', memberId],
    queryFn: () => healthService.getMember(memberId),
    // memberId 없으면 쿼리 비활성 — isLoading false, fetch 안 함
    enabled: Boolean(memberId),
  });
}

export function useRecords(id) {
  const memberId = resolveMemberId(id);
  return useQuery({
    queryKey: ['records', memberId],
    queryFn: () => healthService.getRecords(memberId),
    enabled: Boolean(memberId),
  });
}

export function useFeedback(id) {
  const memberId = resolveMemberId(id);
  return useQuery({
    queryKey: ['feedback', memberId],
    queryFn: () => healthService.getFeedback(memberId),
    enabled: Boolean(memberId),
  });
}

export function useMembers(options = {}) {
  // enabled는 호출부가 넘긴다. 훅 안에서 useAuthSession을 쓰면 훅 순서가 깨질 수 있다.
  const enabled = options.enabled !== false;
  return useQuery({
    queryKey: ['members'],
    queryFn: healthService.getMembers,
    enabled,
    retry: false,
  });
}
