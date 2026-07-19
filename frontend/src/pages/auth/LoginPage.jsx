import {
  ArrowLeft,
  ArrowRight,
  BriefcaseMedical,
  Eye,
  HeartPulse,
  LockKeyhole,
  Mail,
} from 'lucide-react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Logo } from '../../components/common/Logo';
import { Disclaimer } from '../../components/common/Disclaimer';
export function LoginPage() {
  const nav = useNavigate();
  const [params] = useSearchParams();
  const preferred = params.get('role');
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
              onClick={() => nav('/member')}
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
              onClick={() => nav('/professional')}
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
          <form
            onSubmit={(e) => {
              e.preventDefault();
              nav('/member');
            }}
          >
            <label>
              이메일
              <div className="input-wrap">
                <Mail />
                <input type="email" placeholder="name@example.com" required />
              </div>
            </label>
            <label>
              비밀번호
              <div className="input-wrap">
                <LockKeyhole />
                <input type="password" placeholder="비밀번호를 입력하세요" required />
                <Eye />
              </div>
            </label>
            <div className="form-options">
              <label className="check">
                <input type="checkbox" /> 로그인 상태 유지
              </label>
              <button type="button">비밀번호 찾기</button>
            </div>
            <button className="button primary full" type="submit">
              로그인
            </button>
          </form>
          <Disclaimer />
        </div>
      </main>
    </div>
  );
}
