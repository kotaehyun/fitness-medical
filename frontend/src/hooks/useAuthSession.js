/**
 * [공부/면접] 로그인 세션 훅 (useAuthSession.js)
 *
 * Q. 데모와 실로그인을 같이 보는 이유는?
 * A. 데모는 sessionStorage만, 실로그인은 /auth/me(세션 쿠키)다.
 *    화면(랜딩·쉘·보호 라우트)이 같은 규칙으로 로그인 여부를 판단하게 한다.
 *
 * Q. 로그인 전에 /auth/me가 401이면?
 * A. getCurrentAccount가 null을 반환한다. 로그아웃으로 보고, 로그인 성공 시 setQueryData로 덮는다.
 */
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../services/apiService';

export const AUTH_QUERY_KEY = ['auth', 'me'];

// region [하드코딩] 데모 체험 계정 — 시드 UI용. 실로그인 Account와 구분
function demoAccount(role) {
  if (role === 'PROFESSIONAL') {
  return {
    displayName: '홍길동',
    role: 'PROFESSIONAL',
    professionalType: 'PHYSICIAN',
    professionalVerified: true,
    memberId: null,
  };
  }
  return {
    displayName: '김순자',
    role: 'MEMBER',
    professionalType: null,
    memberId: 'm1',
  };
}
// endregion

export function accountRoleLabel(account) {
  const role = String(account?.role || '').toUpperCase();
  if (role === 'MEMBER') return '일반 회원';
  if (role === 'ADMIN') return '관리자';
  const type = String(account?.professionalType || '').toUpperCase();
  if (type === 'TRAINER') return '운동 전문가';
  if (type === 'PHYSICIAN') return '전문의';
  return '전문가';
}

// 핵심 로직: 역할 → 홈 경로. 비로그인·역할 없음은 랜딩 `/`
export function homePath(role) {
  if (role === 'ADMIN') return '/admin';
  if (role === 'PROFESSIONAL') return '/professional';
  if (role === 'MEMBER') return '/member';
  return '/';
}

export function cacheAuthAccount(queryClient, account) {
  queryClient.setQueryData(AUTH_QUERY_KEY, account);
}

export async function clearAuthSession(queryClient) {
  const demoRole = sessionStorage.getItem('fitness-demo-role');
  if (!demoRole) {
    try {
      await apiService.logout();
    } catch {
      // 이미 세션이 없어도 화면은 로그아웃 처리한다.
    }
  }
  sessionStorage.removeItem('fitness-demo-role');
  sessionStorage.removeItem('account-member-id');
  queryClient.removeQueries({ queryKey: ['auth'] });
  queryClient.removeQueries({ queryKey: ['members'] });
  queryClient.removeQueries({ queryKey: ['member'] });
  queryClient.removeQueries({ queryKey: ['records'] });
  queryClient.removeQueries({ queryKey: ['feedback'] });
  queryClient.removeQueries({ queryKey: ['admin'] });
}

export function useAuthSession() {
  // region [핵심로직] 데모 role 또는 /auth/me 로 로그인 여부·역할 결정
  const demoRole = sessionStorage.getItem('fitness-demo-role');
  const query = useQuery({
    queryKey: AUTH_QUERY_KEY,
    queryFn: apiService.getCurrentAccount,
    enabled: !demoRole,
    retry: false,
    refetchOnWindowFocus: false,
  });

  const account = demoRole ? demoAccount(demoRole) : query.data || null;
  const role = demoRole || (account?.role ? String(account.role).toUpperCase() : null);

  return {
    demoRole,
    account,
    role,
    isLoading: !demoRole && query.isLoading,
    isAuthenticated: Boolean(demoRole || query.data),
  };
  // endregion
}

export function useLogout(redirectTo = '/login') {
  const queryClient = useQueryClient();
  const nav = useNavigate();

  return async function logout() {
    await clearAuthSession(queryClient);
    nav(redirectTo, { replace: true });
  };
}
