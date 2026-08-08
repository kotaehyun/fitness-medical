/**
 * [공부/면접] 전역 Provider (TanStack Query)
 *
 * Q. QueryClientProvider가 필요한 이유는?
 * A. useQuery/useMutation이 같은 QueryClient(캐시·재요청·상태)를 공유하려면
 *    React Context로 client를 하위 트리에 주입해야 한다.
 *
 * Q. useState(() => new QueryClient()) 패턴은?
 * A. 함수형 초기값은 최초 마운트 1회만 실행된다.
 *    렌더마다 new QueryClient()를 호출하면 캐시가 매번 초기화된다.
 *
 * Q. staleTime vs cacheTime(garbage collection)?
 * A. staleTime: 이 시간 동안은 "신선"으로 간주해 자동 refetch를 줄인다.
 *    gcTime(구 cacheTime): 미사용 쿼리 데이터를 메모리에서 제거하기까지의 시간.
 */
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState } from 'react';

export function AppProviders({ children }) {
  const [client] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            // 30초 동안 캐시를 fresh로 간주 → 대시보드 연속 탐색 시 중복 GET 감소
            staleTime: 30_000,
            // 네트워크 일시 오류 1회 재시도 (무한 retry 방지)
            retry: 1,
          },
        },
      }),
  );
  return <QueryClientProvider client={client}>{children}</QueryClientProvider>;
}
