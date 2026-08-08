/**
 * [공부/면접] Mock 시드 데이터 (data/mock/members.js)
 *
 * Q. 왜 in-memory 배열을 export하나?
 * A. mockService가 import 후 filter/unshift/splice — 새로고침 시 Vite HMR/리로드로 초기화.
 *
 * Q. healthRecords memberId 'm1' only?
 * A. MEMBER 데모(김순자) 30일 시계열. 다른 회원 상세는 members 메타만 있고 기록은 m1 중심.
 *
 * Q. wave 배열 역할?
 * A. 30일간 혈압·걸음 등에 작은 변동을 줘 차트가 자연스럽게 보이도록.
 *
 * Q. conditions 필드?
 * A. 코칭 참고용 lifestyle 메모 — 진단명이 아님.
 */
export const members = [
  {
    id: 'm1',
    name: '김순자',
    gender: '여성',
    age: 56,
    height: 164,
    weight: 68,
    goal: '건강한 일상 복귀',
    progress: 70,
    status: '양호',
    conditions: ['고혈압', '관절 불편'],
    avatar: '김',
    lastMeasured: '2026-07-19',
  },
  {
    id: 'm2',
    name: '이영희',
    gender: '여성',
    age: 48,
    height: 160,
    weight: 61.4,
    goal: '생활 체력 높이기',
    progress: 82,
    status: '양호',
    conditions: ['없음'],
    avatar: '이',
    lastMeasured: '2026-07-19',
  },
  {
    id: 'm3',
    name: '박정호',
    gender: '남성',
    age: 62,
    height: 173,
    weight: 76.2,
    goal: '규칙적인 걷기',
    progress: 54,
    status: '주의',
    conditions: ['혈압 관리 중'],
    avatar: '박',
    lastMeasured: '2026-07-18',
  },
  {
    id: 'm4',
    name: '최민수',
    gender: '남성',
    age: 39,
    height: 178,
    weight: 81.8,
    goal: '체중 균형 관리',
    progress: 66,
    status: '양호',
    conditions: ['없음'],
    avatar: '최',
    lastMeasured: '2026-07-18',
  },
  {
    id: 'm5',
    name: '한미정',
    gender: '여성',
    age: 67,
    height: 158,
    weight: 59.3,
    goal: '관절 부담 줄이기',
    progress: 43,
    status: '확인 필요',
    conditions: ['관절 불편'],
    avatar: '한',
    lastMeasured: '2026-07-16',
  },
];
const wave = [
  0, 1, -1, 2, 0, -2, 1, -1, 0, 2, -1, 1, 0, -2, 2, 1, -1, 0, 1, -2, 0, 2, -1, 1, 0, -1, 2, 0, -2,
  0,
];
export const healthRecords = Array.from({ length: 30 }, (_, i) => {
  const d = new Date(2026, 6, 19 - i);
  const v = wave[i];
  return {
    id: `r${i + 1}`,
    memberId: 'm1',
    date: d.toISOString().slice(0, 10),
    systolic: 120 + v,
    diastolic: 80 + Math.round(v / 2),
    bloodSugar: 102 + v * 2,
    weight: Number((68 + i * 0.018 + v * 0.03).toFixed(1)),
    bodyFat: Number((25.5 + v * 0.08).toFixed(1)),
    sleep: Number((7 + v * 0.12).toFixed(1)),
    steps: 7500 + v * 260 + (i % 3) * 120,
  };
});
export const feedbacks = [
  {
    id: 'f1',
    memberId: 'm1',
    author: '홍길동',
    role: '재활의학 전문가',
    date: '2026-07-18',
    content:
      '최근 기록이 안정적으로 이어지고 있습니다. 무릎에 부담이 없는 범위에서 걷기 시간을 천천히 늘려보세요.',
  },
  {
    id: 'f2',
    memberId: 'm1',
    author: '김길명',
    role: '운동 전문가',
    date: '2026-07-16',
    content:
      '이번 주 운동 목표를 잘 지키고 있어요. 다음 운동에서는 스트레칭 시간을 5분 더 확보해 보세요.',
  },
  {
    id: 'f3',
    memberId: 'm1',
    author: '홍길동',
    role: '재활의학 전문가',
    date: '2026-07-08',
    content:
      '수면과 활동 기록의 흐름이 좋습니다. 불편함이 느껴지면 강도를 낮추고 휴식을 우선해 주세요.',
  },
];
