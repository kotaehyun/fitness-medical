/**
 * [공부/면접] REST API 클라이언트 (apiService.js)
 *
 * Q. credentials: 'include' 는 왜 필수인가?
 * A. Spring Session(또는 JSESSIONID 쿠키) 기반 인증이면 cross-origin fetch에
 *    credentials를 켜야 브라우저가 Set-Cookie·후속 Cookie 헤더를 보낸다.
 *
 * Q. response.status === 204 처리 이유는?
 * A. DELETE 등 "본문 없는 성공" 응답에서 response.json()을 호출하면
 *    SyntaxError가 난다. 204 No Content → null 반환으로 안전 종료.
 *
 * Q. mapMember/mapRecord가 필요한 이유는?
 * A. 백엔드 DTO(lastMeasuredDate, measuredDate)와 UI 모델(lastMeasured, date) 필드명이
 *    다르다. 변환을 서비스 계층에 두면 페이지 컴포넌트는 mock/API를 구분하지 않는다.
 *
 * Q. toApiId('m1') → '1' ?
 * A. UI·mock는 'm' 접두사 id, REST는 Long 숫자 id. POST/PUT/DELETE 경로 조립 시 strip.
 */
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    // [면접] 세션 쿠키 기반 인증 — omit이면 로그인 후 /auth/me가 401
    credentials: 'include',
    ...options,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => null);
    throw new Error(error?.message || '서버 요청에 실패했습니다.');
  }

  // [면접] 204 No Content: JSON 파싱 생략 (deleteMember 등)
  if (response.status === 204) return null;
  return response.json();
}

function toApiId(id) {
  if (!id) {
    throw new Error('연결된 회원 정보를 확인할 수 없습니다.');
  }
  return String(id).replace(/^m/, '');
}

function mapMember(member) {
  return {
    ...member,
    id: `m${member.id}`,
    lastMeasured: member.lastMeasuredDate,
    avatar: member.name[0],
    conditions: [],
  };
}

function mapRecord(record) {
  return {
    ...record,
    id: `r${record.id}`,
    memberId: `m${record.memberId}`,
    date: record.measuredDate,
    sleep: record.sleepHours,
  };
}

function mapFeedback(feedback) {
  return {
    ...feedback,
    id: `f${feedback.id}`,
    memberId: `m${feedback.memberId}`,
    date: feedback.writtenDate,
  };
}

function mapAccount(account) {
  return {
    ...account,
    memberId: account.memberId ? 'm' + account.memberId : null,
  };
}

export const apiService = {
  async signup(payload) {
    // [면접] password·면허번호는 요청 body로만 전송. console.log·에러 메시지에 포함 금지.
    return mapAccount(await request('/auth/signup', {
      method: 'POST',
      body: JSON.stringify(payload),
    }));
  },

  async login(loginId, password) {
    // [면접] password는 요청 body로만 전송. console.log·에러 메시지에 포함 금지.
    return mapAccount(await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ loginId, password }),
    }));
  },

  async getCurrentAccount() {
    // region [핵심로직] /auth/me — 세션 있으면 Account, 없으면 null(로그아웃)
    const response = await fetch(`${API_URL}/auth/me`, {
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
    });
    if (response.status === 401 || response.status === 403) {
      return null;
    }
    if (!response.ok) {
      const error = await response.json().catch(() => null);
      throw new Error(error?.message || '서버 요청에 실패했습니다.');
    }
    return mapAccount(await response.json());
    // endregion
  },

  async logout() {
    await request('/auth/logout', { method: 'POST' });
  },

  async getMembersForAdmin() {
    return request('/admin/members');
  },

  async getProfessionalsForAdmin() {
    return request('/admin/professionals');
  },

  async verifyProfessional(accountId) {
    return request(`/admin/professionals/${accountId}/verify`, { method: 'POST' });
  },

  async revokeProfessional(accountId) {
    return request(`/admin/professionals/${accountId}/revoke`, { method: 'POST' });
  },

  async getMembers() {
    const data = await request('/members');
    return data.map(mapMember);
  },

  async getMember(id) {
    return mapMember(await request(`/members/${toApiId(id)}`));
  },

  async getRecords(memberId) {
    const data = await request(`/members/${toApiId(memberId)}/records`);
    return data.map(mapRecord);
  },

  async getFeedback(memberId) {
    const data = await request(`/members/${toApiId(memberId)}/feedback`);
    return data.map(mapFeedback);
  },

  async addFeedback(memberId, feedback) {
    // [면접] author·role은 서버가 세션 Account에서 채움. 클라이언트가 위조하지 않는다.
    const data = await request(`/members/${toApiId(memberId)}/feedback`, {
      method: 'POST',
      body: JSON.stringify({ content: feedback.content }),
    });
    return mapFeedback(data);
  },

  async updateMember(id, member) {
    return mapMember(await request('/members/' + toApiId(id), {
      method: 'PUT',
      body: JSON.stringify({
        goal: member.goal,
        progress: member.progress,
      }),
    }));
  },

  async deleteMember(id) {
    // DELETE 성공 시 204 → request()가 null 반환
    await request('/members/' + toApiId(id), { method: 'DELETE' });
  },

  async addRecord(record) {
    // [면접] 로그인 MEMBER 계정의 memberId — LoginPage에서 sessionStorage에 저장
    const memberId = sessionStorage.getItem('account-member-id');
    const body = {
      measuredDate: record.date,
      systolic: record.systolic,
      diastolic: record.diastolic,
      bloodSugar: record.bloodSugar,
      weight: record.weight,
      bodyFat: record.bodyFat,
      sleepHours: record.sleep,
      steps: record.steps,
    };

    const data = await request('/members/' + toApiId(memberId) + '/records', {
      method: 'POST',
      body: JSON.stringify(body),
    });
    return mapRecord(data);
  },

  /**
   * [공부/면접] Spring → FastAPI RAG 질문
   * Q. 왜 FastAPI를 프론트에서 직접 안 부르나?
   * A. 세션·CORS·추후 권한을 Backend에서 통일. mock 모드에선 안내만 반환.
   */
  async askLifestyleGuide(query, nResults = 5) {
    return request('/ai/ask', {
      method: 'POST',
      body: JSON.stringify({ query, nResults }),
    });
  },

  async getChatPeers() {
    return request('/messages/peers');
  },

  async getChatMessages(peerAccountId) {
    return request(`/messages?peerAccountId=${peerAccountId}`);
  },

  async sendChatMessage(peerAccountId, body) {
    return request('/messages', {
      method: 'POST',
      body: JSON.stringify({ peerAccountId, body }),
    });
  },
};
