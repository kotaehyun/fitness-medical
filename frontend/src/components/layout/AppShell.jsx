/**
 * [공부/면접] 앱 공통 레이아웃 (AppShell.jsx)
 *
 * Q. AppShell의 책임은?
 * A. 사이드바 NavLink, topbar, main children 슬롯 — 회원/전문가 워크스페이스 공통 chrome.
 *
 * Q. NavLink end={path.split('/').length === 3} ?
 * A. /member, /professional 루트만 exact active. /member/records는 '건강 기록'만 active.
 *
 * Q. professional prop?
 * A. 라벨·프로필 더미(홍길동 vs 김순자) 분기 — 시연용 UI.
 */
import { Bell, CalendarDays, ChevronDown, Menu, X } from 'lucide-react';
import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { Disclaimer } from '../common/Disclaimer';
import { Logo } from '../common/Logo';
export function AppShell({ children, nav, professional = false }) {
  const [open, setOpen] = useState(false);
  const date = new Intl.DateTimeFormat('ko-KR', {
    month: 'long',
    day: 'numeric',
    weekday: 'short',
  }).format(new Date());
  return (
    <div className="app-shell">
      <aside className={`sidebar ${open ? 'open' : ''}`}>
        <div className="sidebar-head">
          <Logo />
          <button
            className="icon-button mobile-only"
            onClick={() => setOpen(false)}
            aria-label="메뉴 닫기"
          >
            <X />
          </button>
        </div>
        <div className="workspace-label">
          {professional ? '전문가 워크스페이스' : '회원 워크스페이스'}
        </div>
        <nav>
          {nav.map(({ label, path, icon: Icon }) => (
            <NavLink
              key={path}
              to={path}
              end={path.split('/').length === 3}
              onClick={() => setOpen(false)}
            >
              <Icon size={19} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-help">
          <span>도움이 필요하신가요?</span>
          <p>서비스 이용 안내를 확인해 보세요.</p>
          <button>이용 안내</button>
        </div>
      </aside>
      {open && <button className="overlay" onClick={() => setOpen(false)} aria-label="메뉴 닫기" />}
      <div className="app-content">
        <header className="topbar">
          <button
            className="icon-button mobile-only"
            onClick={() => setOpen(true)}
            aria-label="메뉴 열기"
          >
            <Menu />
          </button>
          <div className="top-date">
            <CalendarDays size={18} />
            {date}
          </div>
          <div className="top-actions">
            <button className="icon-button" aria-label="알림">
              <Bell size={19} />
              <span className="notification-dot" />
            </button>
            <button className="profile-button">
              <span className="avatar">{professional ? '홍' : '김'}</span>
              <span className="profile-copy">
                <b>{professional ? '홍길동' : '김순자'}</b>
                <small>{professional ? '재활의학 전문가' : '일반 회원'}</small>
              </span>
              <ChevronDown size={16} />
            </button>
          </div>
        </header>
        <main>{children}</main>
        <Disclaimer />
      </div>
    </div>
  );
}
