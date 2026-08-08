/**
 * [공부/면접] 로그인·데모 진입 (LoginPage.jsx)
 *
 * Q. 실제 로그인 vs 데모 체험 흐름 차이는?
 * A. 로그인: apiService.login → 세션 쿠키 + account-member-id 저장 → role별 redirect.
 *    데모: sessionStorage 'fitness-demo-role'만 설정, API 인증 없이 ProtectedRoute 통과.
 *
 * Q. account-member-id sessionStorage 용도는?
 * A. MEMBER 계정 로그인 시 연결된 memberId(m1 등)를 저장.
 *    useDashboardData·apiService.addRecord가 "누구의 기록인지" 판별할 때 사용.
 *
 * Q. 로그인 성공 시 fitness-demo-role remove ?
 * A. 이전 데모 세션과 실계정 세션이 충돌하지 않도록 demo 플래그를 지운다.
 *
 * Q. 보안 — password 처리 주의점?
 * A. state에만 보관, 전송 후 로그/에러 메시지에 포함하지 않는다.
 */
import {
  ArrowLeft,
  ArrowRight,
  BriefcaseMedical,
  Eye,
  HeartPulse,
  LockKeyhole,
  Mail,
} from 'lucide-react';
import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Logo } from '../../components/common/Logo';
import { Disclaimer } from '../../components/common/Disclaimer';
import { apiService } from '../../services/apiService';

export function LoginPage() {
  const nav = useNavigate();
  const [params] = useSearchParams();
  const preferred = params.get('role');
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const account = await apiService.login(loginId, password);
      // 실계정 로그인 — 데모 role 플래그 제거
      sessionStorage.removeItem('fitness-demo-role');
      const currentAccount = account?.role ? account : await apiService.getCurrentAccount();
      if (currentAccount.memberId) {
        // [면접] MEMBER 전용 — 이후 GET/POST records 경로에 사용
        sessionStorage.setItem('account-member-id', currentAccount.memberId);
      } else {
        sessionStorage.removeItem('account-member-id');
      }
      const role = String(currentAccount?.role || '').toUpperCase();

      if (role === 'MEMBER') {
        nav('/member');
      } else if (role === 'PROFESSIONAL') {
        nav('/professional');
      } else {
        setError('로그인한 계정의 역할을 확인할 수 없습니다.');
      }
    } catch (requestError) {
      // [면접] 에러 메시지에 password 포함 금지 — loginId만 사용자에게 안내
      setError(requestError.message || '로그인에 실패했습니다. 아이디와 비밀번호를 확인해 주세요.');
    } finally {
      setLoading(false);
    }
  }

  function enterDemo(role) {
    // [면접] 데모 MEMBER → useDashboardData가 m1 사용 / PROFESSIONAL → useMembers 등
    sessionStorage.setItem('fitness-demo-role', role);
    nav(role === 'PROFESSIONAL' ? '/professional' : '/member');
  }

  return (
    <div className="login-page">
      <div className="login-side">
        <Logo />
        <div>
          <span className="eyebrow light">
            <HeartPulse size={16} /> 건강 관리의 새로운 기준
          </span>
          <h1>
            매일의 기록이
            <br />
            건강한 내일을 만듭니다.
          </h1>
          <p>
            건강 데이터와 전문가 관리를 연결하는
            <br />
            Fitness Medical을 체험해 보세요.
          </p>
        </div>
        <div className="login-quote">“작은 기록이 모여 더 나은 일상을 만듭니다.”</div>
      </div>
      <main className="login-main">
        <Link to="/" className="back-link">
          <ArrowLeft />
          홈으로 돌아가기
        </Link>
        <div className="login-card">
          <div className="mobile-login-logo">
            <Logo />
          </div>
          <span className="eyebrow">DEMO ACCOUNT</span>
          <h2>Fitness Medical 시작하기</h2>
          <p>체험할 역할을 선택하거나 계정으로 로그인하세요.</p>
          <div className="demo-buttons">
            <button
              className={preferred === 'member' ? 'selected' : ''}
              onClick={() => enterDemo('MEMBER')}
            >
              <span className="demo-icon">
                <HeartPulse />
              </span>
              <span>
                <b>일반 회원으로 체험</b>
                <small>건강 기록과 목표를 관리해요</small>
              </span>
              <ArrowRight />
            </button>
            <button
              className={preferred === 'professional' ? 'selected' : ''}
              onClick={() => enterDemo('PROFESSIONAL')}
            >
              <span className="demo-icon navy">
                <BriefcaseMedical />
              </span>
              <span>
                <b>전문가로 체험</b>
                <small>회원의 건강 흐름을 살펴봐요</small>
              </span>
              <ArrowRight />
            </button>
          </div>
          <div className="or">
            <span>또는 계정으로 로그인</span>
          </div>
          <form onSubmit={handleSubmit}>
            <label>
              로그인 아이디
              <div className="input-wrap">
                <Mail />
                <input
                  type="text"
                  value={loginId}
                  onChange={(event) => setLoginId(event.target.value)}
                  placeholder="로그인 아이디를 입력하세요"
                  autoComplete="username"
                  required
                />
              </div>
            </label>
            <label>
              비밀번호
              <div className="input-wrap">
                <LockKeyhole />
                <input
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="비밀번호를 입력하세요"
                  autoComplete="current-password"
                  required
                />
                <Eye />
              </div>
            </label>
            <div className="form-options">
              <label className="check">
                <input type="checkbox" /> 로그인 상태 유지
              </label>
              <button type="button">비밀번호 찾기</button>
            </div>
            {error && <p className="form-error" role="alert">{error}</p>}
            <button className="button primary full" type="submit" disabled={loading}>
              {loading ? '로그인 중...' : '로그인'}
            </button>
          </form>
          <Disclaimer />
        </div>
      </main>
    </div>
  );
}
