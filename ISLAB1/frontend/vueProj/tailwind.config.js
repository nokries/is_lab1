/** @type {import('tailwindcss').Config} */
export default {
  // Где Tailwind ищет используемые CSS-классы: в index.html и во всех файлах папки src
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  // Своих цветов и размеров не добавляем, используем стандартные
  theme: {
    extend: {},
  },
  // Дополнительные плагины не используем
  plugins: [],
}