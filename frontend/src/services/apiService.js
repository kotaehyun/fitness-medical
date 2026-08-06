const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api';

// [발표 핵심] fetch의 중복 코드와 공통 오류 처리를 request 함수 한 곳에 모았습니다.
async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    ...options,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => null);
    throw new Error(error?.message || '서버 요청에 실패했습니다.');
  }

  if (response.status === 204) return null;
  return response.json();
}

function toApiId(id) {
  return String(id).replace('m', '');
}

// 백엔드 DTO와 프론트 화면 모델의 이름이 달라도 UI를 수정하지 않도록 변환합니다.
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

export const apiService = {
  async login(loginId, password) {
    return request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ loginId, password }),
    });
  },

  async getCurrentAccount() {
    return request('/auth/me');
  },

  async getMembers() {
    const data = await request('/members');
    return data.map(mapMember);
  },

  async getMember(id) {
    return mapMember(await request(`/members/${toApiId(id)}`));
  },

  async getRecords(memberId = 'm1') {
    const data = await request(`/members/${toApiId(memberId)}/records`);
    return data.map(mapRecord);
  },

  async getFeedback(memberId = 'm1') {
    const data = await request(`/members/${toApiId(memberId)}/feedback`);
    return data.map(mapFeedback);
  },

  async addFeedback(memberId, feedback) {
    const data = await request(`/members/${toApiId(memberId)}/feedback`, {
      method: 'POST',
      body: JSON.stringify(feedback),
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
    await request('/members/' + toApiId(id), { method: 'DELETE' });
  },

  async addRecord(record) {
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

    const data = await request('/members/1/records', {
      method: 'POST',
      body: JSON.stringify(body),
    });
    return mapRecord(data);
  },
};
