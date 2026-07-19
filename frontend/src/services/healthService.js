import { apiService } from './apiService';
import { mockService } from './mockService';

// [발표 핵심: Strategy 패턴의 간단한 형태]
// UI는 healthService만 가져다 쓰고, 환경변수에 따라 데이터 공급자만 교체합니다.
// 기본값은 mock이며 VITE_USE_MOCK=false일 때 Spring Boot REST API를 사용합니다.
const useMock = import.meta.env.VITE_USE_MOCK !== 'false';

export const healthService = useMock ? mockService : apiService;
