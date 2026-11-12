<template>
  <!-- Тёмный фон на весь экран, а по центру белое окно меню -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
      <!-- Шапка окна с заголовком и крестиком, крестик сообщает родителю что окно надо закрыть -->
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-900">Создать объект</h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>

      <!-- Три кнопки выбора. Каждая при нажатии отправляет родителю своё событие,
           а родитель уже открывает нужное окно создания -->
      <div class="space-y-3">
        <!-- Кнопка создания персонажа, синяя, с иконкой человека -->
        <button
            @click="$emit('create-creature')"
            class="w-full px-6 py-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center justify-between group"
        >
          <span class="text-lg font-medium">Создать персонажа</span>
          <User :size="24" class="group-hover:scale-110 transition-transform" />
        </button>

        <!-- Кнопка создания кольца, жёлтая, с иконкой кольца -->
        <button
            @click="$emit('create-ring')"
            class="w-full px-6 py-4 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 transition-colors flex items-center justify-between group"
        >
          <span class="text-lg font-medium">Создать кольцо</span>
          <Circle :size="24" class="group-hover:scale-110 transition-transform" />
        </button>

        <!-- Кнопка создания города, зелёная, с иконкой дома -->
        <button
            @click="$emit('create-city')"
            class="w-full px-6 py-4 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center justify-between group"
        >
          <span class="text-lg font-medium">Создать город</span>
          <Home :size="24" class="group-hover:scale-110 transition-transform" />
        </button>
      </div>

      <!-- Кнопка отмены просто закрывает меню -->
      <button
          @click="$emit('close')"
          class="w-full mt-4 px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
      >
        Отмена
      </button>
    </div>
  </div>
</template>

<script setup>
// Подключаем иконки для крестика и для трёх кнопок
import { X, User, Circle, Home } from 'lucide-vue-next'

// Перечисляем события которые это окно может отправить родителю
// закрытие и выбор что именно создавать
defineEmits(['close', 'create-creature', 'create-ring', 'create-city'])
</script>