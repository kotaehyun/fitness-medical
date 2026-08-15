/**
 * [공부/면접] 브랜드 로고 링크 (Logo.jsx)
 *
 * Q. Link 목적지는?
 * A. 비로그인은 랜딩(`/`). 로그인이면 역할 홈(`/member` `/professional` `/admin`).
 *
 * Q. compact prop?
 * A. 사이드바 등 좁은 영역에서 텍스트 숨기고 마크만 표시(현재 기본 false).
 */
import { Activity } from 'lucide-react';
import { Link } from 'react-router-dom';
import { homePath, useAuthSession } from '../../hooks/useAuthSession';
export function Logo({ compact = false }) {
  const { isAuthenticated, role } = useAuthSession();
  // 핵심 로직: 로고 = 비로그인 랜딩 / 로그인 역할 홈
  const to = isAuthenticated ? homePath(role) : '/';
  return (
    <Link to={to} className="logo" aria-label="Fitness Medical 홈">
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
