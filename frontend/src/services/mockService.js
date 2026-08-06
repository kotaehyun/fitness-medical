import { feedbacks, healthRecords, members } from '../data/mock/members';

// 실제 네트워크 요청과 비슷하게 loading UI를 확인하기 위한 의도적인 지연입니다.
const delay = (ms = 280) => new Promise((resolve) => setTimeout(resolve, ms));

// [발표 핵심] 더미 수치는 data/mock에 하드코딩했지만 접근 방식은 Promise 기반으로 만들었습니다.
// 따라서 컴포넌트를 수정하지 않고 healthService만 실제 API 구현으로 교체할 수 있습니다.
export const mockService = {
  async getMembers() {
    await delay();
    return members;
  },
  async getMember(id) {
    await delay();
    const member = members.find((m) => m.id === id);
    if (!member) throw new Error('회원을 찾을 수 없습니다.');
    return member;
  },
  async getRecords(memberId = 'm1') {
    await delay();
    return healthRecords.filter((r) => r.memberId === memberId);
  },
  async getFeedback(memberId = 'm1') {
    await delay();
    return feedbacks.filter((f) => f.memberId === memberId);
  },
  async addFeedback(memberId, feedback) {
    await delay(450);
    const created = {
      ...feedback,
      id: `f${Date.now()}`,
      memberId,
      date: new Date().toISOString().slice(0, 10),
    };
    feedbacks.unshift(created);
    return created;
  },
  async addRecord(record) {
    await delay(450);
    // Date.now()는 데모 환경에서 새 레코드의 id가 겹치지 않게 만드는 간단한 방법입니다.
    const created = { ...record, id: `r${Date.now()}`, memberId: 'm1' };
    // unshift로 최신 기록을 배열 맨 앞에 추가합니다. 새로고침하면 원본 더미데이터로 돌아갑니다.
    healthRecords.unshift(created);
    return created;
  },
};
