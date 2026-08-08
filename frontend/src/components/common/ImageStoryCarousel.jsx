/**
 * [공부/면접] 랜딩 이미지 캐러셀 (ImageStoryCarousel.jsx)
 *
 * Q. prefers-reduced-motion 체크 이유는?
 * A. 접근성 — 사용자 OS 설정에서 애니메이션 감소 시 자동 슬라이드 비활성.
 *
 * Q. onFocusCapture로 pause ?
 * A. 키보드 포커스 시에도 자동 전환 멈춤 — 콘텐츠 읽기 방해 방지.
 *
 * Q. aria-roledescription="carousel" ?
 * A. 스크린리더에 슬라이드 UI임을 알림.
 */
import { Activity, HeartPulse, Pause, Play, Users } from 'lucide-react';
import { useEffect, useState } from 'react';

const icons = { medical: HeartPulse, team: Users, movement: Activity };

export function ImageStoryCarousel({ stories }) {
  const [active, setActive] = useState(0);
  const [paused, setPaused] = useState(false);

  useEffect(() => {
    if (paused || window.matchMedia('(prefers-reduced-motion: reduce)').matches) return undefined;
    const timer = window.setInterval(
      () => setActive((current) => (current + 1) % stories.length),
      4500,
    );
    return () => window.clearInterval(timer);
  }, [paused, stories.length]);

  return (
    <div
      className="story-carousel"
      onMouseEnter={() => setPaused(true)}
      onMouseLeave={() => setPaused(false)}
      onFocusCapture={() => setPaused(true)}
      onBlurCapture={() => setPaused(false)}
      aria-roledescription="carousel"
      aria-label="Fitness Medical 서비스 이야기"
    >
      <div className="story-stage" aria-live="polite">
        {stories.map((story, index) => {
          const Icon = icons[story.icon];
          return (
            <article
              className={`story-slide ${index === active ? 'active' : ''}`}
              key={story.label}
              aria-hidden={index !== active}
            >
              <img src={story.image} alt={story.alt} loading={index === 0 ? 'eager' : 'lazy'} />
              <div className="story-shade" />
              <div className="story-caption">
                <span>
                  <Icon />
                  {story.label}
                </span>
                <h3>{story.title}</h3>
                <p>{story.description}</p>
              </div>
            </article>
          );
        })}
        <button
          className="story-pause"
          onClick={() => setPaused((value) => !value)}
          aria-label={paused ? '자동 전환 재생' : '자동 전환 일시정지'}
        >
          {paused ? <Play /> : <Pause />}
        </button>
      </div>
      <div className="story-thumbnails" role="tablist" aria-label="이미지 선택">
        {stories.map((story, index) => (
          <button
            key={story.label}
            className={index === active ? 'active' : ''}
            onClick={() => setActive(index)}
            role="tab"
            aria-selected={index === active}
          >
            <img src={story.image} alt="" />
            <span>
              <b>0{index + 1}</b>
              {story.shortLabel}
            </span>
            <i />
          </button>
        ))}
      </div>
    </div>
  );
}
