import { Navigate, Route, Routes } from 'react-router-dom';
import { LoginPage } from '../pages/auth/LoginPage';
import { PlaceholderPage } from '../pages/common/PlaceholderPage';
import { LandingPage } from '../pages/landing/LandingPage';
import { HealthRecordsPage } from '../pages/member/HealthRecordsPage';
import { MemberDashboard, memberNav } from '../pages/member/MemberDashboard';
import { MemberDetailPage } from '../pages/professional/MemberDetailPage';
import {
  ProfessionalDashboard,
  professionalNav,
} from '../pages/professional/ProfessionalDashboard';

// [발표 핵심] React Router는 현재 URL에 맞는 페이지 컴포넌트를 선택합니다.
// 공통 메뉴에 아직 구현되지 않은 주소는 PlaceholderPage가 받아서 빈 화면을 방지합니다.
export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/member" element={<MemberDashboard />} />
      <Route path="/member/records" element={<HealthRecordsPage />} />
      <Route path="/member/*" element={<PlaceholderPage nav={memberNav} />} />
      <Route path="/professional" element={<ProfessionalDashboard />} />
      <Route path="/professional/members/:id" element={<MemberDetailPage />} />
      <Route
        path="/professional/*"
        element={<PlaceholderPage nav={professionalNav} professional />}
      />
      {/* 등록되지 않은 주소는 랜딩 페이지로 이동시킵니다. replace는 잘못된 URL을 방문 기록에서 교체합니다. */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
