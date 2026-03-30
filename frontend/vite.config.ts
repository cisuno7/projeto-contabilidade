import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Produção embutida no Spring (context-path /api): assets em /api/assets/*
const embedded =
  process.env.VITE_DEPLOY_EMBEDDED === 'true' || process.env.VITE_DEPLOY_EMBEDDED === '1'

export default defineConfig(({ mode }) => ({
  base: mode === 'production' && embedded ? '/api/' : '/',
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
}))
