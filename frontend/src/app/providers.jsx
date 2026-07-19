import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState } from 'react';

export function AppProviders({ children }) {
  // [발표 핵심] QueryClient는 서버 데이터의 캐시, 재요청, 오류 처리를 관리합니다.
  // useState의 함수형 초기값을 사용하면 렌더링할 때마다 QueryClient가 새로 생성되지 않습니다.
  const [client] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            // 30초 동안은 캐시 데이터를 최신으로 간주해 불필요한 요청을 줄입니다.
            staleTime: 30_000,
            // 일시적인 오류일 수 있으므로 조회 요청은 한 번만 재시도합니다.
            retry: 1,
          },
        },
      }),
  );
  return <QueryClientProvider client={client}>{children}</QueryClientProvider>;
}
