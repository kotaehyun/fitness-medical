/**
 * [공부/면접] 미구현 라우트 placeholder (PlaceholderPage.jsx)
 *
 * Q. 왜 404 대신 이 컴포넌트를 쓰나?
 * A. memberNav/professionalNav에 등록된 "준비 중" 메뉴(목표, 예약 등)로
 *    AppShell 레이아웃을 유지한 채 빈 화면·에러 UX를 방지한다.
 *
 * Q. router.jsx와의 관계는?
 * A. /member/*, /professional/* 와일드카드 Route의 element.
 */
import { Construction } from 'lucide-react';
import { AppShell } from '../../components/layout/AppShell';

/**
 * @param {{
 *   nav: { label: string, path: string, icon?: import('react').ComponentType }[],
 *   professional?: boolean,
 *   workspaceLabel?: string,
 * }} props
 */
export function PlaceholderPage({ nav, professional = false, workspaceLabel = undefined }) {
  return (
    <AppShell nav={nav} professional={professional} workspaceLabel={workspaceLabel}>
      <div className="placeholder card">
        <Construction />
        <h1>준비 중인 화면입니다</h1>
        <p>Fitness Medical의 다음 기능으로 곧 만나보실 수 있습니다.</p>
      </div>
    </AppShell>
  );
}
