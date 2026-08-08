/**
 * [공부/면접] React 앱 진입점 (main.jsx)
 *
 * Q. main.jsx의 역할은?
 * A. DOM의 #root에 React 트리를 마운트하는 SPA 부트스트랩 파일이다.
 *    Vite는 이 파일을 HTML의 <script type="module"> 진입점으로 번들한다.
 *
 * Q. BrowserRouter를 최상단에 두는 이유는?
 * A. 하위 모든 컴포넌트(router, pages)가 useNavigate, Link, useParams 등
 *    React Router 훅/컴포넌트를 사용할 수 있게 Context를 제공한다.
 *
 * Q. StrictMode는 무엇을 하나?
 * A. 개발 모드에서 이중 렌더·effect 실행 등으로 부작용을 조기에 드러낸다.
 *    프로덕션 빌드에는 영향을 주지 않는다.
 */
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { AppProviders } from './app/providers';
import { AppRouter } from './app/router';
import './styles/global.css';

// createRoot: React 18의 Concurrent 렌더링 API. legacy ReactDOM.render 대신 사용.
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* 라우팅 Context → 전역 상태(QueryClient) → 실제 Route 트리 순으로 감싼다. */}
    <BrowserRouter>
      <AppProviders>
        <AppRouter />
      </AppProviders>
    </BrowserRouter>
  </React.StrictMode>,
);
