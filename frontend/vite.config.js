import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // 0.0.0.0 — localhost / 127.0.0.1 모두
    port: 5173,
    strictPort: true,
    open: true, // 기동 시 기본 브라우저 자동 오픈
  },
});
