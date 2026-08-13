/**
 * [공부/면접] 앱 라우팅 (router.jsx)
 *
 * Q. ProtectedRoute가 하는 일은?
 * A. (1) 데모 role(sessionStorage) 또는 (2) /auth/me API로 역할 확인 후
 *    allowedRole과 다르면 리다이렉트, 미인증이면 /login.
 *
 * Q. demoRole 있을 때 useQuery enabled: !demoRole ?
 * A. 데모 체험은 쿠키 세션 없이 sessionStorage만으로 통과.
 *    API 호출을 끄면 401·불필요한 로딩을 피한다.
 *
 * Q. Route path="/member/*" + PlaceholderPage ?
 * A. 아직 구현되지 않은 회원 메뉴(목표, 예약 등)를 404 대신 "준비 중"으로 처리.
 *
 * Q. Navigate replace vs push ?
 * A. replace는 history 스택에 남기지 않아 뒤로가기 시 보호 라우트로 재진입을 줄인다.
 */
import { Navigate, Route, Routes } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { StateView } from '../components/common/StateView';
import { LoginPage } from '../pages/auth/LoginPage';
import { SignupPage } from '../pages/auth/SignupPage';
import { PlaceholderPage } from '../pages/common/PlaceholderPage';
import { LandingPage } from '../pages/landing/LandingPage';
import { HealthRecordsPage } from '../pages/member/HealthRecordsPage';
import { MemberFeedbackPage } from '../pages/member/MemberFeedbackPage';
import { MemberGuidePage } from '../pages/member/MemberGuidePage';
import { MemberDashboard, memberNav } from '../pages/member/MemberDashboard';
import { MemberDetailPage } from '../pages/professional/MemberDetailPage';
import { MemberManagementPage } from '../pages/professional/MemberManagementPage';
import {
  ProfessionalDashboard,
  professionalNav,
} from '../pages/professional/ProfessionalDashboard';
import { apiService } from '../services/apiService';

function ProtectedRoute({ allowedRole, children }) {
  // [면접] LoginPage enterDemo()가 설정 — 'MEMBER' | 'PROFESSIONAL'
  const demoRole = sessionStorage.getItem('fitness-demo-role');
  const accountQuery = useQuery({
    queryKey: ['auth', 'me'],
    queryFn: apiService.getCurrentAccount,
    // 데모 모드면 /auth/me 호출 안 함 (세션 없음)
    enabled: !demoRole,
    retry: false,
  });

  if (!demoRole && accountQuery.isLoading) {
    return <StateView type="loading" message="로그인 정보를 확인하고 있습니다." />;
  }

  if (!demoRole && (accountQuery.isError || !accountQuery.data)) {
    return <Navigate to="/login" replace />;
  }

  const role = demoRole || String(accountQuery.data.role || '').toUpperCase();
  if (role !== allowedRole) {
    return <Navigate to={role === 'PROFESSIONAL' ? '/professional' : '/member'} replace />;
  }

  return children;
}

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route
        path="/member"
        element={
          <ProtectedRoute allowedRole="MEMBER">
            <MemberDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/member/records"
        element={
          <ProtectedRoute allowedRole="MEMBER">
            <HealthRecordsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/member/feedback"
        element={
          <ProtectedRoute allowedRole="MEMBER">
            <MemberFeedbackPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/member/guide"
        element={
          <ProtectedRoute allowedRole="MEMBER">
            <MemberGuidePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/member/*"
        element={
          <ProtectedRoute allowedRole="MEMBER">
            <PlaceholderPage nav={memberNav} />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professional"
        element={
          <ProtectedRoute allowedRole="PROFESSIONAL">
            <ProfessionalDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professional/members/:id"
        element={
          <ProtectedRoute allowedRole="PROFESSIONAL">
            <MemberDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professional/members"
        element={
          <ProtectedRoute allowedRole="PROFESSIONAL">
            <MemberManagementPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professional/*"
        element={
          <ProtectedRoute allowedRole="PROFESSIONAL">
            <PlaceholderPage nav={professionalNav} professional />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
