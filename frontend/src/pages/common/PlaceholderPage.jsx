import { Construction } from 'lucide-react';
import { AppShell } from '../../components/layout/AppShell';
export function PlaceholderPage({ nav, professional = false }) {
  return (
    <AppShell nav={nav} professional={professional}>
      <div className="placeholder card">
        <Construction />
        <h1>준비 중인 화면입니다</h1>
        <p>Fitness Medical의 다음 기능으로 곧 만나보실 수 있습니다.</p>
      </div>
    </AppShell>
  );
}
