<template>
  <!-- Контейнер для уведомлений: закреплён в правом верхнем углу экрана поверх всего (z-[9999]),
       уведомления внутри располагаются друг под другом с небольшим отступом -->
  <div class="fixed top-4 right-4 z-[9999] space-y-2">
    <!-- TransitionGroup добавляет плавное появление и исчезновение уведомлений
         (стили анимации toast-... описаны внизу файла) -->
    <TransitionGroup name="toast">
      <!-- Один блок на каждое уведомление из списка toasts (v-for).
           :key — уникальный номер, чтобы Vue отличал уведомления друг от друга.
           :class — общие классы оформления + цвета по виду уведомления -->
      <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="[
          'min-w-[300px] max-w-md px-4 py-3 rounded-lg shadow-lg flex items-start gap-3',
          'transform transition-all duration-300',
          toastClasses[toast.type]
        ]"
      >
        <!-- Иконка выбирается по виду уведомления (галочка, восклицательный знак и т.д.) -->
        <component :is="toastIcons[toast.type]" :size="20" class="flex-shrink-0 mt-0.5" />
        <div class="flex-1">
          <!-- Текст уведомления. v-html вставляет его как HTML (для жирного заголовка),
               поэтому текст с сервера предварительно экранируется в errorHandler.js -->
          <p class="text-sm font-medium" v-html="toast.message"></p>
        </div>
        <!-- Кнопка-крестик при нажатии удаляет это уведомление из списка -->
        <button
            @click="removeToast(toast.id)"
            class="flex-shrink-0 opacity-70 hover:opacity-100 transition-opacity"
        >
          <X :size="18" />
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
// Иконки из библиотеки lucide
import { CheckCircle, AlertCircle, AlertTriangle, Info, X } from 'lucide-vue-next'
// Общий список уведомлений и функция удаления
import { useToast } from '../composables/useToast.js'

// Берём из useToast список уведомлений и функцию удаления
const { toasts, removeToast } = useToast()

// Цвета оформления для каждого вида уведомления
const toastClasses = {
  success: 'bg-green-50 border border-green-200 text-green-800',
  error: 'bg-red-50 border border-red-200 text-red-800',
  warning: 'bg-yellow-50 border border-yellow-200 text-yellow-800',
  info: 'bg-blue-50 border border-blue-200 text-blue-800'
}

// Иконка для каждого вида уведомления
const toastIcons = {
  success: CheckCircle,
  error: AlertCircle,
  warning: AlertTriangle,
  info: Info
}
</script>

<style scoped>
/* Стили действуют только в этом компоненте (scoped).
   Анимация уведомлений: появление и исчезновение занимают 0,3 секунды */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

/* Начало появления: уведомление прозрачное и сдвинуто вправо за край экрана */
.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

/* Исчезновение: уведомление уезжает вправо, становится прозрачным и немного уменьшается */
.toast-leave-to {
  opacity: 0;
  transform: translateX(100%) scale(0.9);
}
</style>