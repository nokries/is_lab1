<script setup>
// ref делает переменную «следимой»: когда она меняется, страница обновляется сама
import { ref } from 'vue'

import MainTable from './MainTable.vue'
import SpecialOperations from './SpecialOperations.vue'
import ToastContainer from './components/ToastContainer.vue'

// Какая вкладка открыта сейчас: 'table' или 'operations'. Сначала открыта таблица
const currentTab = ref('table')
</script>

<template>
  <!-- Главный блок страницы: на всю высоту экрана, светло-серый фон -->
  <div class="min-h-screen bg-gray-50">
    <!-- Здесь показываются всплывающие уведомления -->
    <ToastContainer />
    <!-- Верхняя панель с двумя вкладками -->
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <div class="flex space-x-8">
              <!-- Кнопка вкладки «Таблица объектов».
                   При нажатии меняем currentTab на 'table'.
                   Если эта вкладка открыта, кнопка синяя, иначе серая -->
              <button
                  @click="currentTab = 'table'"
                  :class="[
                  'inline-flex items-center px-4 py-2 border-b-2 text-sm font-medium transition-colors',
                  currentTab === 'table'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                Таблица объектов
              </button>
              <!-- Кнопка вкладки «Специальные операции», по аналогии -->
              <button
                  @click="currentTab = 'operations'"
                  :class="[
                  'inline-flex items-center px-4 py-2 border-b-2 text-sm font-medium transition-colors',
                  currentTab === 'operations'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                Специальные операции
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- Показываем только ту часть, которая выбрана в currentTab.
         Вторая часть при этом полностью удаляется со страницы -->
    <MainTable v-if="currentTab === 'table'" />
    <SpecialOperations v-if="currentTab === 'operations'" />
  </div>
</template>

<!-- Своих стилей здесь нет, всё оформление через классы Tailwind -->
<style scoped></style>