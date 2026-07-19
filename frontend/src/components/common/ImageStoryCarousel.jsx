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
