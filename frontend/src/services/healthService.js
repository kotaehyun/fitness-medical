/**
 * [공부/면접] 데이터 공급자 선택 (Strategy 패턴)
 *
 * Q. UI가 apiService/mockService를 직접 import하지 않는 이유는?
 * A. 환경변수(VITE_USE_MOCK)로 구현체를 한 곳에서 교체하면
 *    페이지·훅 코드를 수정하지 않고 mock ↔ REST API 전환이 가능하다.
 *
 * Q. VITE_USE_MOCK !== 'false' 의미는?
 * A. 기본값은 mock. 명시적으로 'false' 문자열일 때만 Spring Boot API를 사용한다.
 *    (Vite env는 빌드 시점에 문자열로 치환됨)
 *
 * Q. 면접에서 "Strategy vs Factory" 차이?
 * A. Strategy: 런타임에 알고리즘(데이터 소스) 교체. Factory: 객체 생성 위임.
 *    여기서는 단순 re-export이지만 개념적으로 Strategy에 가깝다.
 */
import { apiService } from './apiService';
import { mockService } from './mockService';

const useMock = import.meta.env.VITE_USE_MOCK !== 'false';

export const healthService = useMock ? mockService : apiService;
