/**
 * [공부/면접] 회원가입 (SignupPage.jsx)
 *
 * Q. 회원 vs 전문가 가입 차이는?
 * A. MEMBER는 프로필로 새 Member+Account를 같이 만든다. 기존 memberId는 받지 않는다.
 *    PROFESSIONAL은 memberId 없이 가입하고, 형식만 맞으면 저장한다.
 *    공개 가입 전문가는 verified=false. 로컬 데모 trainer01/doctor01만 즉시 사용 가능.
 *
 * Q. 면허번호를 프론트만 검사하면 되나?
 * A. 안 된다. 서버 AccountService에서 다시 검증한다. 프론트 검사는 UX용.
 *
 * Q. 가입 후 바로 로그인하는 이유?
 * A. 별도 로그인 입력을 줄인다. password는 요청 body로만 보내고 로그에 남기지 않는다.
 */
import { ArrowLeft, BriefcaseMedical, HeartPulse, LockKeyhole, Mail, UserRound } from 'lucide-react';
import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Logo } from '../../components/common/Logo';
import { Disclaimer } from '../../components/common/Disclaimer';
import { apiService } from '../../services/apiService';

export function SignupPage() {
  const nav = useNavigate();
  const [params] = useSearchParams();
  const initialRole = params.get('role') === 'professional' ? 'PROFESSIONAL' : 'MEMBER';
  const [role, setRole] = useState(initialRole);
  const [professionalType, setProfessionalType] = useState('TRAINER');
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [licenseNumber, setLicenseNumber] = useState('');
  const [gender, setGender] = useState('여성');
  const [age, setAge] = useState('30');
  const [height, setHeight] = useState('165');
  const [weight, setWeight] = useState('60');
  const [goal, setGoal] = useState('규칙적인 생활 습관');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const payload = {
        loginId: loginId.trim(),
        password,
        displayName: displayName.trim(),
        role,
      };

      if (role === 'PROFESSIONAL') {
        payload.professionalType = professionalType;
        const trimmedLicense = licenseNumber.trim();
        if (trimmedLicense) {
          payload.licenseNumber = trimmedLicense;
        }
      } else {
        payload.gender = gender;
        payload.age = Number(age);
        payload.height = Number(height);
        payload.weight = Number(weight);
        payload.goal = goal.trim();
        payload.progress = 0;
      }

      await apiService.signup(payload);
      const account = await apiService.login(loginId.trim(), password);
      sessionStorage.removeItem('fitness-demo-role');
      if (account.memberId) {
        sessionStorage.setItem('account-member-id', account.memberId);
      } else {
        sessionStorage.removeItem('account-member-id');
      }

      nav(account.role === 'PROFESSIONAL' ? '/professional' : '/member');
    } catch (requestError) {
      // checkJs: catch는 unknown. instanceof로 메시지에만 접근한다. password는 넣지 않는다.
      const message =
        requestError instanceof Error
          ? requestError.message
          : '회원가입에 실패했습니다. 입력값을 확인해 주세요.';
      setError(message || '회원가입에 실패했습니다. 입력값을 확인해 주세요.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-side">
        <Logo />
        <div>
          <span className="eyebrow light">
            <HeartPulse size={16} /> 계정 만들기
          </span>
          <h1>
            역할에 맞는
            <br />
            가입 규칙을 적용합니다.
          </h1>
          <p>
            공개 전문가 가입은 관리자 승인 전까지
            <br />
            회원 데이터에 접근할 수 없습니다.
          </p>
        </div>
        <div className="login-quote">자격·면허 인증은 형식 확인이며 의료 진단·처방 권한이 아닙니다.</div>
      </div>
      <main className="login-main">
        <Link to="/login" className="back-link">
          <ArrowLeft />
          로그인으로 돌아가기
        </Link>
        <div className="login-card">
          <div className="mobile-login-logo">
            <Logo />
          </div>
          <span className="eyebrow">CREATE ACCOUNT</span>
          <h2>회원가입</h2>
          <p>회원 또는 전문가 유형을 고른 뒤 정보를 입력하세요.</p>
          <form onSubmit={handleSubmit}>
            <div className="role-toggle" role="tablist" aria-label="가입 역할">
              <button
                type="button"
                className={role === 'MEMBER' ? 'selected' : ''}
                onClick={() => setRole('MEMBER')}
              >
                <HeartPulse size={16} />
                일반 회원
              </button>
              <button
                type="button"
                className={role === 'PROFESSIONAL' ? 'selected' : ''}
                onClick={() => setRole('PROFESSIONAL')}
              >
                <BriefcaseMedical size={16} />
                전문가
              </button>
            </div>

            {role === 'PROFESSIONAL' ? (
              <div className="role-toggle compact" role="tablist" aria-label="전문가 유형">
                <button
                  type="button"
                  className={professionalType === 'TRAINER' ? 'selected' : ''}
                  onClick={() => setProfessionalType('TRAINER')}
                >
                  트레이너
                </button>
                <button
                  type="button"
                  className={professionalType === 'PHYSICIAN' ? 'selected' : ''}
                  onClick={() => setProfessionalType('PHYSICIAN')}
                >
                  전문의
                </button>
              </div>
            ) : null}

            <label>
              표시 이름
              <div className="input-wrap">
                <UserRound />
                <input
                  type="text"
                  value={displayName}
                  onChange={(event) => setDisplayName(event.target.value)}
                  placeholder="화면에 표시할 이름"
                  required
                />
              </div>
            </label>
            <label>
              로그인 아이디
              <div className="input-wrap">
                <Mail />
                <input
                  type="text"
                  value={loginId}
                  onChange={(event) => setLoginId(event.target.value)}
                  placeholder="영문, 숫자, 밑줄 4자 이상"
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
                  placeholder="8자 이상"
                  autoComplete="new-password"
                  required
                />
              </div>
            </label>

            {role === 'MEMBER' ? (
              <>
                <div className="form-grid signup-grid">
                  <label>
                    성별
                    <select value={gender} onChange={(event) => setGender(event.target.value)}>
                      <option value="여성">여성</option>
                      <option value="남성">남성</option>
                    </select>
                  </label>
                  <label>
                    나이
                    <input
                      type="number"
                      min="1"
                      max="120"
                      value={age}
                      onChange={(event) => setAge(event.target.value)}
                      required
                    />
                  </label>
                  <label>
                    키(cm)
                    <input
                      type="number"
                      min="140"
                      max="200"
                      step="0.1"
                      value={height}
                      onChange={(event) => setHeight(event.target.value)}
                      required
                    />
                  </label>
                  <label>
                    체중(kg)
                    <input
                      type="number"
                      min="30"
                      max="200"
                      step="0.1"
                      value={weight}
                      onChange={(event) => setWeight(event.target.value)}
                      required
                    />
                  </label>
                </div>
                <label>
                  건강 목표
                  <div className="input-wrap">
                    <HeartPulse />
                    <input
                      type="text"
                      value={goal}
                      onChange={(event) => setGoal(event.target.value)}
                      placeholder="예: 규칙적인 걷기"
                      required
                    />
                  </div>
                </label>
              </>
            ) : null}

            {role === 'PROFESSIONAL' ? (
              <label>
                {professionalType === 'PHYSICIAN' ? '전문의 면허번호' : '생활스포츠지도사 자격번호 (선택)'}
                <div className="input-wrap">
                  <BriefcaseMedical />
                  <input
                    type="text"
                    value={licenseNumber}
                    onChange={(event) => setLicenseNumber(event.target.value)}
                    placeholder={
                      professionalType === 'PHYSICIAN' ? '숫자 5~10자리' : '예: SP21001234 (우대)'
                    }
                    required={professionalType === 'PHYSICIAN'}
                  />
                </div>
                <small className="signup-hint">
                  {professionalType === 'PHYSICIAN'
                    ? '학습용 형식 확인입니다. 가입 후 관리자 승인 전까지 전문가 API는 사용할 수 없습니다.'
                    : '선택(우대). 영문 2자 + 숫자 6~12자. 가입 직후는 미인증입니다.'}
                </small>
              </label>
            ) : null}

            {error ? (
              <p className="form-error" role="alert">
                {error}
              </p>
            ) : null}
            <button className="button primary full" type="submit" disabled={loading}>
              {loading ? '가입 중...' : '회원가입'}
            </button>
          </form>
          <p className="auth-switch">
            이미 계정이 있나요? <Link to="/login">로그인</Link>
          </p>
          <Disclaimer />
        </div>
      </main>
    </div>
  );
}
