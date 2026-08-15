/**
 * [공부/면접] 앱 라우팅 (router.jsx)
 *
 * Q. ProtectedRoute가 하는 일은?
 * A. useAuthSession으로 데모 role 또는 /auth/me를 확인한 뒤
 *    allowedRole과 다르면 리다이렉트, 미인증이면 /login.
 *
 * Q. demoRole 있을 때 /auth/me를 안 부르는 이유?
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
import { StateView } from '../components/common/StateView';
import { LoginPage } from '../pages/auth/LoginPage';
import { SignupPage } from '../pages/auth/SignupPage';
import { PlaceholderPage } from '../pages/common/PlaceholderPage';
import { MessagesPage } from '../pages/common/MessagesPage';
import { PrivacyPage } from '../pages/common/PrivacyPage';
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
import { AdminVerificationPage, adminNav } from '../pages/admin/AdminVerificationPage';
import { homePath, useAuthSession } from '../hooks/useAuthSession';

function ProtectedRoute({ allowedRole, children }) {
  const { isLoading, isAuthenticated, role } = useAuthSession();

  if (isLoading) {
    return <StateView type="loading" message="로그인 정보를 확인하고 있습니다." />;
  }

  if (!isAuthenticated || !role) {
    return <Navigate to="/login" replace />;
  }

  if (role !== allowedRole) {
    const next = homePath(role);
    return <Navigate to={next === '/' ? '/login' : next} replace />;
  }

  return children;
}

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/privacy/member" element={<PrivacyPage audience="MEMBER" />} />
      <Route path="/privacy/professional" element={<PrivacyPage audience="PROFESSIONAL" />} />
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
        path="/member/messages"
        element={
          <ProtectedRoute allowedRole="MEMBER">
            <MessagesPage />
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
        path="/professional/messages"
        element={
          <ProtectedRoute allowedRole="PROFESSIONAL">
            <MessagesPage professional />
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
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRole="ADMIN">
            <AdminVerificationPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/*"
        element={
          <ProtectedRoute allowedRole="ADMIN">
            <PlaceholderPage nav={adminNav} workspaceLabel="관리자 워크스페이스" />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
