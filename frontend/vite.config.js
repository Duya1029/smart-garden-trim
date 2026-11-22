import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// Vite 5 配置：Vue3 + @ 别名 + 开发代理
export default defineConfig({
  plugins: [vue()],
  resolve: {
    // @ 别名指向 src 目录
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    // 绑定所有网卡，手机 / 其他电脑可通过本机局域网 IP 访问（如 http://192.168.x.x:5173）
    host: true,
    // 允许所有域名访问开发服务器：cpolar 免费版公网域名每次重启会变，需放行所有 host
    allowedHosts: true,
    // 开发态图片与接口都走 Vite 代理到后端 9999（本机 8080 被其他应用占用）
    proxy: {
      '/api': {
        target: 'http://localhost:9999',
        changeOrigin: true
      },
      '/upload': {
        target: 'http://localhost:9999',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    chunkSizeWarningLimit: 1500
  }
})
