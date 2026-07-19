import { useQuery } from '@tanstack/react-query';
import { healthService } from '../services/healthService';

// [발표 핵심] 페이지는 fetch나 mock 데이터를 직접 알지 못하고 이 훅만 사용합니다.
// queryKey는 캐시의 주소 역할을 하며, 회원 id가 달라지면 별도의 데이터로 저장됩니다.
export function useMemberData(id = 'm1') {
  return useQuery({ queryKey: ['member', id], queryFn: () => healthService.getMember(id) });
}

export function useRecords(id = 'm1') {
  return useQuery({ queryKey: ['records', id], queryFn: () => healthService.getRecords(id) });
}

export function useFeedback(id = 'm1') {
  return useQuery({ queryKey: ['feedback', id], queryFn: () => healthService.getFeedback(id) });
}

export function useMembers() {
  return useQuery({ queryKey: ['members'], queryFn: healthService.getMembers });
}
