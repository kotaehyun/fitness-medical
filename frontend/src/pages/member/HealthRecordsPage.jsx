/**
 * [공부/면접] 회원 건강 기록 목록·등록 (HealthRecordsPage.jsx)
 *
 * Q. useQuery vs useMutation 역할 분담은?
 * A. useRecords: 조회·캐시. useMutation(addRecord): POST 후 invalidateQueries로 목록 갱신.
 *
 * Q. StatusBadge "정상"은 진단 결과인가?
 * A. 시연 UI용 라벨 — 의료 진단·처방이 아닌 기록 참고 표시.
 *
 * Q. FormData + Number() 변환 이유는?
 * A. HTML input value는 문자열. API/mock는 숫형 필드를 기대한다.
 */
import { CalendarDays, ChevronDown, Plus, X } from 'lucide-react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { AppShell } from '../../components/layout/AppShell';
import { memberNav } from './MemberDashboard';
import { useRecords } from '../../hooks/useDashboardData';
import { StateView } from '../../components/common/StateView';
import { StatusBadge } from '../../components/common/StatusBadge';
import { healthService } from '../../services/healthService';
export function HealthRecordsPage() {
  const records = useRecords();
  const [open, setOpen] = useState(false);
  const [type, setType] = useState('전체 지표');
  const qc = useQueryClient();

  // [면접] mutation 성공 → ['records'] prefix invalidate → useRecords refetch
  const mutation = useMutation({
    mutationFn: healthService.addRecord,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['records'] });
      setOpen(false);
    },
  });
  if (records.isLoading)
    return (
      <AppShell nav={memberNav}>
        <StateView type="loading" message="건강 기록을 불러오는 중입니다." />
      </AppShell>
    );
  return (
    <AppShell nav={memberNav}>
      <div className="page-head">
        <div>
          <span className="page-kicker">HEALTH RECORDS</span>
          <h1>건강 기록</h1>
          <p>나의 건강 데이터를 한눈에 확인하고 관리하세요.</p>
        </div>
        <button className="button primary compact" onClick={() => setOpen(true)}>
          <Plus />
          기록 추가
        </button>
      </div>
      <section className="card records-card">
        <div className="filters">
          <label>
            기간
            <div className="select-wrap">
              <CalendarDays />
              <select>
                <option>최근 30일</option>
                <option>최근 7일</option>
              </select>
              <ChevronDown />
            </div>
          </label>
          <label>
            지표 유형
            <div className="select-wrap">
              <select value={type} onChange={(e) => setType(e.target.value)}>
                <option>전체 지표</option>
                <option>혈압</option>
                <option>혈당</option>
                <option>체중</option>
              </select>
              <ChevronDown />
            </div>
          </label>
        </div>
        {!records.data?.length ? (
          <StateView type="empty" message="아직 건강 기록이 없습니다." />
        ) : (
          <>
            <div className="records-table">
              <table>
                <thead>
                  <tr>
                    <th>측정일</th>
                    <th>혈압</th>
                    <th>혈당</th>
                    <th>체중</th>
                    <th>체지방</th>
                    <th>수면</th>
                    <th>걸음 수</th>
                    <th>상태</th>
                  </tr>
                </thead>
                <tbody>
                  {records.data.map((r) => (
                    <tr key={r.id}>
                      <td>{r.date}</td>
                      <td>
                        {r.systolic}/{r.diastolic} <small>mmHg</small>
                      </td>
                      <td>
                        {r.bloodSugar} <small>mg/dL</small>
                      </td>
                      <td>
                        {r.weight} <small>kg</small>
                      </td>
                      <td>{r.bodyFat}%</td>
                      <td>{r.sleep}시간</td>
                      <td>{r.steps.toLocaleString()}보</td>
                      <td>
                        <StatusBadge status="정상" />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="record-cards">
              {records.data.map((r) => (
                <article key={r.id}>
                  <div>
                    <time>{r.date}</time>
                    <StatusBadge status="정상" />
                  </div>
                  <dl>
                    <div>
                      <dt>혈압</dt>
                      <dd>
                        {r.systolic}/{r.diastolic}
                      </dd>
                    </div>
                    <div>
                      <dt>혈당</dt>
                      <dd>{r.bloodSugar} mg/dL</dd>
                    </div>
                    <div>
                      <dt>체중</dt>
                      <dd>{r.weight} kg</dd>
                    </div>
                    <div>
                      <dt>걸음</dt>
                      <dd>{r.steps.toLocaleString()}보</dd>
                    </div>
                  </dl>
                </article>
              ))}
            </div>
          </>
        )}
      </section>
      {open && (
        <RecordModal
          close={() => setOpen(false)}
          submit={(e) => {
            e.preventDefault();
            // FormData는 input의 name을 key로 사용합니다.
            // HTML input 값은 문자열이므로 건강 수치는 Number로 명시적으로 변환합니다.
            const fd = new FormData(e.currentTarget);
            mutation.mutate({
              date: String(fd.get('date')),
              systolic: Number(fd.get('systolic')),
              diastolic: Number(fd.get('diastolic')),
              bloodSugar: Number(fd.get('bloodSugar')),
              weight: Number(fd.get('weight')),
              bodyFat: Number(fd.get('bodyFat')),
              sleep: Number(fd.get('sleep')),
              steps: Number(fd.get('steps')),
            });
          }}
          pending={mutation.isPending}
        />
      )}
    </AppShell>
  );
}
function RecordModal({ close, submit, pending }) {
  return (
    <div className="modal-backdrop" role="presentation">
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <div className="modal-head">
          <div>
            <h2 id="modal-title">건강 기록 추가</h2>
            <p>오늘 측정한 건강 정보를 입력해 주세요.</p>
          </div>
          <button className="icon-button" onClick={close} aria-label="닫기">
            <X />
          </button>
        </div>
        {/* required, min, max를 사용해 제출 전에 브라우저 기본 유효성 검사를 수행합니다. */}
        <form onSubmit={submit}>
          <label className="full-field">
            측정일
            <input name="date" type="date" defaultValue="2026-07-19" required />
          </label>
          <div className="form-grid">
            <label>
              수축기 혈압 <span>mmHg</span>
              <input name="systolic" type="number" min="80" max="180" defaultValue="120" required />
            </label>
            <label>
              이완기 혈압 <span>mmHg</span>
              <input name="diastolic" type="number" min="50" max="120" defaultValue="80" required />
            </label>
            <label>
              혈당 <span>mg/dL</span>
              <input
                name="bloodSugar"
                type="number"
                min="60"
                max="200"
                defaultValue="102"
                required
              />
            </label>
            <label>
              체중 <span>kg</span>
              <input
                name="weight"
                type="number"
                step="0.1"
                min="30"
                max="200"
                defaultValue="68"
                required
              />
            </label>
            <label>
              체지방 <span>%</span>
              <input
                name="bodyFat"
                type="number"
                step="0.1"
                min="5"
                max="60"
                defaultValue="25.5"
                required
              />
            </label>
            <label>
              수면시간 <span>시간</span>
              <input
                name="sleep"
                type="number"
                step="0.1"
                min="0"
                max="16"
                defaultValue="7"
                required
              />
            </label>
            <label>
              걸음 수 <span>보</span>
              <input name="steps" type="number" min="0" max="50000" defaultValue="7500" required />
            </label>
          </div>
          <div className="modal-actions">
            <button type="button" className="button secondary" onClick={close}>
              취소
            </button>
            <button className="button primary" disabled={pending}>
              {pending ? '저장 중...' : '기록 저장'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
