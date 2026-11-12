// Инструменты для работы с путями к файлам
import { fileURLToPath, URL } from 'node:url'

// Основная функция настроек Vite
import { defineConfig } from 'vite'
// Плагин, чтобы Vite понимал файлы .vue
import vue from '@vitejs/plugin-vue'
// Плагин для отладки Vue
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  // Подключаем плагины
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      // Сокращение: знак @ в путях означает папку src
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  define: {
    // Нужно для библиотеки WebSocket: в браузере нет переменной global, подставляем globalThis
    'global': 'globalThis',
  },
  server: {
    // Пересылка запросов с фронтенда на сервер Spring
    proxy: {
      // Запросы, которые начинаются с /api, идут на Spring (порт 8080)
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // Запросы /ws (WebSocket) тоже идут на Spring
      '/ws': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
      }
    }
  }
})