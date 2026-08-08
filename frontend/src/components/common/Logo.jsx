/**
 * [공부/면접] 브랜드 로고 링크 (Logo.jsx)
 *
 * Q. Link to="/" ?
 * A. 어디서든 클릭 시 랜딩으로 — SPA 내비, full reload 없음.
 *
 * Q. compact prop?
 * A. 사이드바 등 좁은 영역에서 텍스트 숨기고 마크만 표시(현재 기본 false).
 */
import { Activity } from 'lucide-react';
import { Link } from 'react-router-dom';
export function Logo({ compact = false }) {
  return (
    <Link to="/" className="logo" aria-label="Fitness Medical 홈">
      <span className="logo-mark">
        <Activity size={20} />
      </span>
      {!compact && (
        <span>
          Fitness <b>Medical</b>
        </span>
      )}
    </Link>
  );
}
