/**
 * [공부/면접] 역할별 개인정보 처리방침 (PrivacyPage.jsx)
 *
 * Q. 왜 회원/전문가를 나누나?
 * A. 회원이 맡기는 데이터(건강 기록)와 전문가가 맡기는 데이터(자격·면허, 회원 열람)가 다르다.
 *    같은 문구로 묶으면 수집 목적·이용 범위가 흐려진다.
 *
 * Q. 이 문서는 실제 법률 자문인가?
 * A. 아니다. 학습·시연용 고지다. 진단·처방 권한이 없음을 분명히 한다.
 */
import { ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Logo } from '../../components/common/Logo';
import { Disclaimer } from '../../components/common/Disclaimer';

// region [하드코딩] 시연용 약관 문구 — 실제 법률 자문이 아님
const MEMBER_POLICY = {
  title: '회원 개인정보 처리방침',
  kicker: 'MEMBER PRIVACY',
  summary: '일반 회원의 계정·건강 기록을 어떻게 수집하고, 전문가와 어떻게 나누는지 안내합니다.',
  sections: [
    {
      heading: '1. 수집하는 정보',
      body: '로그인 아이디, 표시 이름, 비밀번호(해시), 성별·나이·키·체중·건강 목표, 혈압·혈당·체중·체지방·수면·걸음 수 등 건강 기록을 수집합니다.',
    },
    {
      heading: '2. 이용 목적',
      body: '본인 건강 흐름 확인, 목표 관리, 인증된 전문가의 생활 관리 피드백 제공에만 사용합니다. 의료 진단이나 처방에는 사용하지 않습니다.',
    },
    {
      heading: '3. 제공 범위',
      body: '인증이 완료된 전문가(트레이너·전문의)가 코칭 목적으로 회원 기록을 열람할 수 있습니다. 미인증 전문가와 외부 제3자에게는 제공하지 않습니다.',
    },
    {
      heading: '4. 보관',
      body: '학습·시연 환경에서만 보관합니다. 비밀번호와 자격·면허번호는 화면에 다시 보여주지 않습니다.',
    },
  ],
};

const PROFESSIONAL_POLICY = {
  title: '전문가 개인정보 처리방침',
  kicker: 'PROFESSIONAL PRIVACY',
  summary: '전문가 계정의 자격·면허 정보와, 회원 건강 기록을 다룰 때의 책임을 안내합니다.',
  sections: [
    {
      heading: '1. 수집하는 정보',
      body: '로그인 아이디, 표시 이름, 비밀번호(해시), 전문가 유형(트레이너/전문의), 자격·면허번호, 전문직 인증 여부를 수집합니다.',
    },
    {
      heading: '2. 이용 목적',
      body: '전문가 계정 운영, 자격·면허 형식 확인, 인증 완료 후 회원 기록 열람·피드백 작성에만 사용합니다. 국가 자격 조회나 의료 면허 검증이 아닙니다.',
    },
    {
      heading: '3. 회원 정보 이용 제한',
      body: '회원 건강 기록은 생활 관리 코칭에만 사용할 수 있습니다. 진단·처방·외부 유출은 할 수 없으며, 미인증 상태에서는 회원 데이터에 접근할 수 없습니다.',
    },
    {
      heading: '4. 보관',
      body: '학습·시연 환경에서만 보관합니다. 자격·면허번호는 로그·응답에 포함하지 않습니다.',
    },
  ],
};
// endregion

export function PrivacyPage({ audience = 'MEMBER' }) {
  const isProfessional = audience === 'PROFESSIONAL';
  const policy = isProfessional ? PROFESSIONAL_POLICY : MEMBER_POLICY;

  return (
    <div className="privacy-page">
      <header className="privacy-head">
        <Logo />
        <Link to="/" className="back-link">
          <ArrowLeft />
          홈으로 돌아가기
        </Link>
      </header>
      <main className="privacy-main">
        <div className="privacy-tabs" role="tablist" aria-label="개인정보 처리방침 대상">
          <Link
            className={!isProfessional ? 'selected' : ''}
            to="/privacy/member"
          >
            일반 회원
          </Link>
          <Link
            className={isProfessional ? 'selected' : ''}
            to="/privacy/professional"
          >
            전문가
          </Link>
        </div>
        <article className="privacy-article card">
          <span className="eyebrow">{policy.kicker}</span>
          <h1>{policy.title}</h1>
          <p>{policy.summary}</p>
          {policy.sections.map((section) => (
            <section key={section.heading}>
              <h2>{section.heading}</h2>
              <p>{section.body}</p>
            </section>
          ))}
        </article>
        <Disclaimer />
      </main>
    </div>
  );
}
