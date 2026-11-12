<template>
  <!-- Тёмный фон на весь экран, а по центру белое окно, которое можно прокручивать -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Шапка окна с заголовком и крестиком закрытия -->
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">Информация об объекте</h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>

      <!-- Основное содержимое показывается, когда существо уже загружено с сервера -->
      <div v-if="creature" class="p-6 space-y-6">
        <!-- Основная информация -->
        <!-- Если возраста или типа нет, пишем «Не указан» -->
        <div class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Основная информация</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">ID:</span>
              <p class="font-medium">{{ creature.id }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Имя:</span>
              <p class="font-medium">{{ creature.name }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Возраст:</span>
              <p class="font-medium">{{ creature.age ?? 'Не указан' }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Тип существа:</span>
              <p class="font-medium">
                <span class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-sm">
                  {{ creature.creatureType ?? 'Не указан' }}
                </span>
              </p>
            </div>
            <!-- Дату оформляет функция formatDate -->
            <div>
              <span class="text-sm text-gray-600">Дата создания:</span>
              <p class="font-medium">{{ formatDate(creature.creationDate) }}</p>
            </div>
          </div>
        </div>

        <!-- Координаты -->
        <div class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Координаты</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">X:</span>
              <p class="font-medium">{{ creature.coordinates.x }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Y:</span>
              <p class="font-medium">{{ creature.coordinates.y }}</p>
            </div>
          </div>
        </div>

        <!-- Характеристики -->
        <div class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Характеристики</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">Уровень атаки:</span>
              <p class="font-medium">{{ creature.attackLevel }}</p>
            </div>
          </div>
        </div>

        <!-- Локация -->
        <!-- Если локация есть, показываем её данные, иначе ниже надпись что локации нет.
             Пустые необязательные поля заменяем текстом -->
        <div v-if="creature.creatureLocation" class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Локация</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">Название:</span>
              <p class="font-medium">{{ creature.creatureLocation.name }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Площадь:</span>
              <p class="font-medium">{{ creature.creatureLocation.area }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Население:</span>
              <p class="font-medium">{{ creature.creatureLocation.population }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Плотность населения:</span>
              <p class="font-medium">{{ creature.creatureLocation.populationDensity }}</p>
            </div>
            <!-- Дату основания форматируем только если она есть -->
            <div>
              <span class="text-sm text-gray-600">Дата основания:</span>
              <p class="font-medium">{{ creature.creatureLocation.establishmentDate ? formatDate(creature.creatureLocation.establishmentDate) : 'Не указана' }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Правитель:</span>
              <p class="font-medium">{{ creature.creatureLocation.governor ?? 'Не указан' }}</p>
            </div>
            <!-- Столица это true или false, показываем словами -->
            <div>
              <span class="text-sm text-gray-600">Столица:</span>
              <p class="font-medium">{{ creature.creatureLocation.capital ? 'Да' : 'Нет' }}</p>
            </div>
          </div>
        </div>
        <div v-else class="bg-gray-50 rounded-lg p-4">
          <p class="text-gray-600">Локация не указана</p>
        </div>

        <!-- Кольцо -->
        <!-- Так же как с локацией, данные кольца или надпись что кольца нет -->
        <div v-if="creature.ring" class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Кольцо</h3>
          <div class="grid grid-cols-3 gap-4">
            <div>
              <span class="text-sm text-gray-600">Название:</span>
              <p class="font-medium">{{ creature.ring.name }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Сила:</span>
              <p class="font-medium">{{ creature.ring.power }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Вес:</span>
              <p class="font-medium">{{ creature.ring.weight }}</p>
            </div>
          </div>
        </div>
        <div v-else class="bg-gray-50 rounded-lg p-4">
          <p class="text-gray-600">Кольцо отсутствует</p>
        </div>

        <!-- Кнопки действий -->
        <!-- Закрыть прячет окно. Редактировать отправляет родителю событие с номером существа,
             и родитель открывает окно редактирования -->
        <div class="flex justify-end gap-3 pt-4 border-t border-gray-200">
          <button
              @click="$emit('close')"
              class="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Закрыть
          </button>
          <button
              @click="$emit('edit', creatureId)"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <Edit :size="18" />
            Редактировать
          </button>
        </div>
      </div>

      <!-- Пока существо не загрузилось, показываем надпись про загрузку -->
      <div v-else class="p-6">
        <p class="text-center text-gray-600">Загрузка...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// ref делает переменную следимой, onMounted выполняется когда окно появилось
import { ref, onMounted } from 'vue'
// Иконки крестика и карандаша
import { X, Edit } from 'lucide-vue-next'
// Запросы к серверу
import { api } from './api.js'
// Красное уведомление об ошибке
import { showApiError } from './utils/errorHandler.js'

// Данные которые родитель передаёт в окно, номер существа для просмотра
const props = defineProps<{
  creatureId: number
}>()

// События которые окно отправляет родителю, закрыться и редактировать с номером существа
defineEmits<{
  close: []
  edit: [id: number]
}>()

// Здесь лежит загруженное существо, пока оно не пришло там null
const creature = ref<any>(null)

// Превращает дату с сервера в понятный вид по-русски
function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleString('ru-RU')
}

// Загружает существо с сервера по номеру
async function loadCreature() {
  try {
    creature.value = await api.getCreatureById(props.creatureId)
  } catch (error) {
    console.error('Ошибка загрузки объекта:', error)
    showApiError({ title: 'Ошибка', message: 'Не удалось загрузить данные объекта' })
  }
}

// Когда окно появилось, сразу загружаем существо
onMounted(() => {
  loadCreature()
})
</script>