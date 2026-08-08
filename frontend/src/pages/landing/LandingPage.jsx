/**
 * [공부/면접] 랜딩 페이지 (LandingPage.jsx)
 *
 * Q. 이 페이지의 역할은?
 * A. 비로그인 마케팅·소개 화면. CTA로 /login?role=member|professional 데모 진입.
 *
 * Q. hero의 대시보드 미리보기는 실제 API 데이터인가?
 * A. 아니다. 정적 UI mockup — 시연용 가상 수치(의료 진단·처방 아님).
 *
 * Q. ImageStoryCarousel을 쓰는 이유는?
 * A. 서비스 가치(전문가 협업, 생활 코칭)를 시각 스토리로 전달.
 */
import {
  Activity,
  ArrowRight,
  BarChart3,
  Check,
  ChevronRight,
  ClipboardCheck,
  HeartPulse,
  Menu,
  MessageSquareText,
  ShieldCheck,
  Sparkles,
  Target,
  TrendingUp,
  Users,
  X,
} from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Disclaimer } from '../../components/common/Disclaimer';
import { Logo } from '../../components/common/Logo';
import { ImageStoryCarousel } from '../../components/common/ImageStoryCarousel';
import wellnessWalk from '../../assets/images/wellness-walk.jpg';
import medicalConsultation from '../../assets/images/medical-consultation-white-coat.jpg';
import specialistTrainer from '../../assets/images/specialist-trainer-collaboration.jpg';
export function LandingPage() {
  const [menu, setMenu] = useState(false);
  const careStories = [
    {
      image: medicalConsultation,
      icon: 'medical',
      label: 'MEDICAL CARE',
      shortLabel: '전문의 상담',
      title: '기록을 함께 이해하는 시간',
      description: '전문의가 건강 데이터의 흐름을 회원의 눈높이에서 함께 살펴봅니다.',
      alt: '흰 의사 가운을 입은 전문의와 건강 기록을 살펴보는 중년 회원',
    },
    {
      image: specialistTrainer,
      icon: 'team',
      label: 'TEAM APPROACH',
      shortLabel: '전문가 협업',
      title: '두 전문성이 하나의 방향으로',
      description: '의료 전문가와 트레이너가 더 나은 생활 관리를 위해 의견을 나눕니다.',
      alt: '건강 관리 방향을 함께 논의하는 전문의와 운동 트레이너',
    },
    {
      image: wellnessWalk,
      icon: 'movement',
      label: 'MOVEMENT CARE',
      shortLabel: '일상 운동',
      title: '일상에서 이어지는 건강한 변화',
      description: '무리하지 않는 움직임부터 꾸준히 이어갈 수 있도록 함께합니다.',
      alt: '도심 공원에서 함께 걷는 중년 회원과 운동 전문가',
    },
  ];
  return (
    <div className="landing">
      <header className="landing-header">
        <Logo />
        <nav className={menu ? 'open' : ''}>
          <a href="#about">서비스 소개</a>
          <a href="#features">건강관리</a>
          <a href="#experts">전문가</a>
          <Link to="/login">로그인</Link>
          <Link className="button primary small" to="/login?role=member">
            회원으로 체험하기
          </Link>
        </nav>
        <button
          className="icon-button mobile-only"
          onClick={() => setMenu(!menu)}
          aria-label="메뉴 열기"
        >
          {menu ? <X /> : <Menu />}
        </button>
      </header>
      <main>
        <section className="hero" id="about">
          <div className="hero-copy">
            <span className="eyebrow">
              <Sparkles size={15} /> 더 나은 건강 습관의 시작
            </span>
            <h1>
              건강 기록과
              <br />
              <em>전문가 관리</em>를 한곳에서
            </h1>
            <p>매일의 건강 변화부터 운동 목표와 전문가 피드백까지 간편하게 관리하세요.</p>
            <div className="hero-actions">
              <Link className="button primary" to="/login?role=member">
                회원으로 체험하기 <ArrowRight size={18} />
              </Link>
              <Link className="button secondary" to="/login?role=professional">
                전문가로 체험하기
              </Link>
            </div>
            <div className="hero-trust">
              <span>
                <Check />
                간편한 기록
              </span>
              <span>
                <Check />
                한눈에 보는 변화
              </span>
              <span>
                <Check />
                전문가와 함께
              </span>
            </div>
          </div>
          <div className="hero-visual" aria-label="건강 대시보드 미리보기">
            <div className="preview-glow" />
            <div className="dashboard-preview">
              <div className="preview-head">
                <div>
                  <span className="mini-label">TODAY</span>
                  <h3>안녕하세요, 김순자님</h3>
                </div>
                <span className="preview-avatar">김</span>
              </div>
              <div className="preview-score">
                <div className="score-ring">
                  <b>82</b>
                  <small>건강 점수</small>
                </div>
                <div>
                  <span className="status status-양호">● 양호</span>
                  <h4>꾸준히 잘 관리하고 있어요</h4>
                  <p>지난주보다 활동량이 8% 늘었어요.</p>
                </div>
              </div>
              <div className="preview-grid">
                <div>
                  <HeartPulse />
                  <span>혈압</span>
                  <b>120/80</b>
                  <small>mmHg</small>
                </div>
                <div>
                  <Activity />
                  <span>걸음 수</span>
                  <b>7,500</b>
                  <small>목표 75%</small>
                </div>
              </div>
              <div className="preview-chart">
                <div className="chart-title">
                  <b>주간 활동</b>
                  <span>이번 주</span>
                </div>
                <div className="bars">
                  {[48, 62, 50, 78, 66, 88, 73].map((h, i) => (
                    <i key={i} style={{ height: `${h}%` }}>
                      <span>{['월', '화', '수', '목', '금', '토', '일'][i]}</span>
                    </i>
                  ))}
                </div>
              </div>
            </div>
            <div className="floating-card">
              <span className="icon-box mint">
                <TrendingUp />
              </span>
              <div>
                <b>목표 달성률</b>
                <strong>70%</strong>
              </div>
            </div>
            <div className="floating-feedback">
              <MessageSquareText />
              <div>
                <b>새로운 피드백</b>
                <span>운동 전문가 김길명</span>
              </div>
            </div>
          </div>
        </section>
        <section className="brand-strip">
          <p>일상의 기록이 더 나은 건강 습관으로</p>
          <div>
            <span>
              <ShieldCheck />
              안전한 시연 환경
            </span>
            <span>
              <Users />
              전문가와 공동 관리
            </span>
            <span>
              <BarChart3 />
              데이터 기반 인사이트
            </span>
          </div>
        </section>
        <section className="section" id="features">
          <div className="section-heading">
            <span className="eyebrow">CORE FEATURES</span>
            <h2>
              나의 건강을 이해하는
              <br />
              가장 쉬운 방법
            </h2>
            <p>
              복잡한 건강 데이터를 이해하기 쉽게 정리하고, 작은 변화까지 놓치지 않도록 도와드립니다.
            </p>
          </div>
          <div className="feature-grid">
            <Feature
              icon={HeartPulse}
              num="01"
              title="건강 데이터 기록"
              text="혈압, 혈당, 체중, 수면, 걸음 수를 한곳에 기록하고 변화의 흐름을 확인하세요."
              color="mint"
            />
            <Feature
              icon={Target}
              num="02"
              title="맞춤 목표 관리"
              text="나에게 맞는 건강 목표를 세우고 매일의 달성 과정을 직관적으로 확인하세요."
              color="navy"
            />
            <Feature
              icon={MessageSquareText}
              num="03"
              title="전문가 피드백"
              text="기록을 바탕으로 의료 전문가와 트레이너의 세심한 관리 의견을 받아보세요."
              color="blue"
            />
          </div>
        </section>
        <section className="human-care">
          <ImageStoryCarousel stories={careStories} />
          <div className="human-care-copy">
            <span className="eyebrow">CARE, MADE HUMAN</span>
            <h2>
              의료 전문가와 트레이너가
              <br />
              일상의 변화를 함께 봅니다.
            </h2>
            <p>
              건강 기록을 의료 전문가와 운동 전문가가 각자의 관점에서 함께 살펴봅니다. 진단이나
              처방이 아닌, 더 나은 생활 습관을 위한 이해하기 쉬운 관리 의견을 제공합니다.
            </p>
            <div className="care-points">
              <div>
                <strong>30일</strong>
                <span>건강 흐름 한눈에 보기</span>
              </div>
              <div>
                <strong>2명</strong>
                <span>분야별 전문가 피드백</span>
              </div>
              <div>
                <strong>1곳</strong>
                <span>기록과 목표의 통합 관리</span>
              </div>
            </div>
            <Link to="/login?role=member">
              나의 건강 여정 시작하기 <ArrowRight />
            </Link>
          </div>
        </section>
        <section className="how" id="experts">
          <div className="section-heading">
            <span className="eyebrow">HOW IT WORKS</span>
            <h2>건강한 변화, 세 단계면 충분해요</h2>
          </div>
          <div className="steps">
            <Step
              icon={ClipboardCheck}
              num="1"
              title="기록하기"
              text="오늘의 건강 수치와 활동을 간편하게 남겨요."
            />
            <ChevronRight />
            <Step
              icon={BarChart3}
              num="2"
              title="변화 확인하기"
              text="차트로 나의 변화와 목표 진행률을 확인해요."
            />
            <ChevronRight />
            <Step
              icon={MessageSquareText}
              num="3"
              title="피드백 받기"
              text="전문가의 따뜻하고 실용적인 의견을 받아요."
            />
          </div>
          <div className="cta-banner">
            <div>
              <span>FITNESS MEDICAL</span>
              <h2>
                오늘부터 건강한 기록을
                <br />
                시작해 보세요.
              </h2>
            </div>
            <Link className="button light" to="/login">
              무료로 체험하기 <ArrowRight />
            </Link>
          </div>
        </section>
      </main>
      <footer>
        <div>
          <Logo />
          <p>건강한 일상을 위한 데이터 기반 헬스케어 파트너</p>
        </div>
        <div className="footer-links">
          <a href="#about">서비스 안내</a>
          <a href="#privacy">개인정보처리방침</a>
          <Link to="/login">체험하기</Link>
        </div>
        <Disclaimer />
        <small>© 2026 Fitness Medical. Demo service.</small>
      </footer>
    </div>
  );
}
function Feature({ icon: Icon, num, title, text, color }) {
  return (
    <article className="feature-card">
      <div className="feature-number">{num}</div>
      <div className={`feature-icon ${color}`}>
        <Icon />
      </div>
      <h3>{title}</h3>
      <p>{text}</p>
      <span>
        자세히 알아보기 <ArrowRight size={16} />
      </span>
    </article>
  );
}
function Step({ icon: Icon, num, title, text }) {
  return (
    <article className="step">
      <div className="step-icon">
        <Icon />
        <span>{num}</span>
      </div>
      <h3>{title}</h3>
      <p>{text}</p>
    </article>
  );
}
