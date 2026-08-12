/**
 * [공부/면접] 생활 습관 안내 (RAG)
 *
 * Q. 이 페이지가 호출하는 API는?
 * A. healthService.askLifestyleGuide → (실모드) Spring POST /api/ai/ask → FastAPI /ask
 *
 * Q. 의료 기능인가?
 * A. 아니다. 등록된 생활·코칭 문서 검색 기반 안내. 진단·처방 UI를 두지 않는다.
 */
import { useState } from 'react';
import { Sparkles } from 'lucide-react';
import { AppShell } from '../../components/layout/AppShell';
import { Disclaimer } from '../../components/common/Disclaimer';
import { healthService } from '../../services/healthService';
import { memberNav } from './MemberDashboard';

export function MemberGuidePage() {
  const [query, setQuery] = useState('잠은 어떻게 자면 좋나요?');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  async function handleAsk(event) {
    event.preventDefault();
    const trimmed = query.trim();
    if (!trimmed) {
      setError('질문을 입력해 주세요.');
      return;
    }

    setLoading(true);
    setError('');
    setResult(null);
    try {
      const data = await healthService.askLifestyleGuide(trimmed, 5);
      setResult(data);
    } catch (err) {
      setError(err.message || '안내를 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <AppShell nav={memberNav}>
      <div className="page-head">
        <div>
          <span className="page-kicker">LIFESTYLE GUIDE</span>
          <h1>생활 습관 안내</h1>
          <p>등록된 안내 문서를 바탕으로 생활·운동 습관 팁을 받아보세요.</p>
        </div>
        <span className="button secondary compact">
          <Sparkles size={16} />
          RAG 시연
        </span>
      </div>

      <section className="card">
        <div className="card-head">
          <div>
            <h3>질문하기</h3>
            <span>의료 진단·처방 질문은 답하지 않습니다.</span>
          </div>
        </div>
        <form className="feedback-form" onSubmit={handleAsk}>
          <label>
            질문
            <textarea
              rows={3}
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="예: 수면의 질을 높이려면?"
              maxLength={500}
            />
          </label>
          <button className="button" type="submit" disabled={loading}>
            {loading ? '답변 생성 중…' : '안내 받기'}
          </button>
        </form>
        {error ? <p className="disclaimer">{error}</p> : null}
      </section>

      {result ? (
        <section className="card feedback-history" style={{ marginTop: '1.25rem' }}>
          <div className="card-head">
            <div>
              <h3>안내 답변</h3>
              <span>model: {result.model}</span>
            </div>
          </div>
          <article>
            <div>
              <p>{result.answer}</p>
            </div>
          </article>
          {result.sources?.length ? (
            <>
              <div className="card-head" style={{ marginTop: '1rem' }}>
                <div>
                  <h3>참고한 문서 조각</h3>
                  <span>{result.sources.length}건</span>
                </div>
              </div>
              {result.sources.map((source) => (
                <article key={source.chunkId || `${source.documentId}-${source.chunkIndex}`}>
                  <div>
                    <b>{source.title || '문서'}</b>
                    <p>{source.content}</p>
                  </div>
                </article>
              ))}
            </>
          ) : null}
        </section>
      ) : null}

      <Disclaimer />
    </AppShell>
  );
}
