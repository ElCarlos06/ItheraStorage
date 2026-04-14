import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // /api/eventos PRIMERO (más específico) — configuración especial para SSE
      '/api/eventos': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq) => {
            // Quitar Accept-Encoding para que el backend NO comprima la respuesta.
            // La compresión rompe el streaming de SSE porque bufferiza los chunks.
            proxyReq.removeHeader('Accept-Encoding');
          });
        },
      },
      // /api general después (menos específico)
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-react': ['react', 'react-dom', 'react-router-dom'],
          'vendor-charts': ['recharts'],
          'vendor-ui': ['motion', '@radix-ui/react-alert-dialog', '@radix-ui/react-tooltip', 'sonner', 'lucide-react'],
          'vendor-icons': ['@heathmont/moon-icons'],
        },
      },
    },
  },
})
