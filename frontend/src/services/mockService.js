/**
 * [공부/면접] Mock 데이터 서비스 (mockService.js)
 *
 * Q. mockService가 apiService와 같은 메서드 시그니처를 갖는 이유는?
 * A. healthService에서 교체 가능한 Strategy. Liskov 치환 — UI/훅은 동일하게 호출.
 *
 * Q. delay()를 넣는 이유는?
 * A. 네트워크 latency 없이도 loading 스피너·pending UI를 시연/테스트할 수 있다.
 *
 * Q. addRecord에서 memberId: 'm1' 고정인 이유?
 * A. 데모 MEMBER 체험은 sessionStorage 'fitness-demo-role'만 설정하고
 *    account-member-id는 없다. mock은 김순자(m1) 시나리오에 맞춘 단일 회원 데모.
 *
 * Q. getRecords(memberId = 'm1') 기본값?
 * A. memberId 미전달 시에도 MEMBER 데모 대시보드가 m1 기록을 보여준다.
 */
import { feedbacks, healthRecords, members } from '../data/mock/members';

const delay = (ms = 280) => new Promise((resolve) => setTimeout(resolve, ms));

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
  async updateMember(id, member) {
    await delay();
    const target = members.find((item) => item.id === id);
    if (!target) throw new Error('회원을 찾을 수 없습니다.');
    target.goal = member.goal;
    target.progress = member.progress;
    return target;
  },
  async deleteMember(id) {
    await delay();
    const index = members.findIndex((item) => item.id === id);
    if (index === -1) throw new Error('회원을 찾을 수 없습니다.');
    members.splice(index, 1);
  },
  async addRecord(record) {
    await delay(450);
    // [면접] 데모 MEMBER는 항상 m1(김순자) 기록에 추가 — 실 API는 sessionStorage memberId 사용
    const created = { ...record, id: `r${Date.now()}`, memberId: 'm1' };
    healthRecords.unshift(created);
    return created;
  },

  async askLifestyleGuide(query) {
    await delay(500);
    return {
      answer:
        `모의 응답입니다. "${query}"에 대해 규칙적인 생활 습관을 유지해 보세요. ` +
        '이 안내는 의료 진단이나 처방이 아닙니다.',
      model: 'mock',
      sources: [],
    };
  },
};
