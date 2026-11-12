<template>
  <!-- Серый фон на весь экран, внутри белая карточка по центру -->
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-5xl mx-auto">
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h1 class="text-3xl font-bold text-gray-900 mb-6">Специальные операции</h1>

        <!-- Пять блоков, по одному на каждую операцию -->
        <div class="space-y-6">
          <!-- Операция 1: Объект с минимальными координатами -->
          <!-- Одна кнопка без полей ввода. Результат появляется ниже, когда он есть -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Объект с минимальными координатами
            </h3>
            <p class="text-sm text-gray-600 mb-3">
              Координаты сравниваются сначала по X, затем по Y
            </p>
            <button
                @click="findMinCoordinates"
                class="w-full px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Найти
            </button>
            <!-- Зелёный блок с именем, номером и координатами найденного существа -->
            <div v-if="minCoordinatesResult" class="mt-3 p-3 bg-green-50 rounded-lg">
              <p class="font-medium text-green-800">{{ minCoordinatesResult.name }}</p>
              <p class="text-sm text-gray-600">
                ID: {{ minCoordinatesResult.id }},
                Координаты: ({{ minCoordinatesResult.coordinates.x }}, {{ minCoordinatesResult.coordinates.y }})
              </p>
            </div>
          </div>

          <!-- Операция 2: Объекты, имя которых содержит подстроку -->
          <!-- Поле для текста и кнопка поиска -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Поиск объектов по подстроке в имени
            </h3>
            <div class="flex gap-3">
              <input
                  v-model="nameSubstring"
                  type="text"
                  placeholder="Введите подстроку"
                  class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <button
                  @click="findByNameSubstring"
                  class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Найти
              </button>
            </div>
            <!-- Результат показываем если поиск уже был. Пустой список тоже результат,
                 поэтому сравниваем с null. Сверху счётчик, ниже карточки с прокруткой -->
            <div v-if="nameResults !== null" class="mt-3 space-y-2">
              <p class="text-sm text-gray-600">Найдено: {{ nameResults.length }}</p>
              <div class="max-h-48 overflow-y-auto space-y-2">
                <div
                    v-for="creature in nameResults"
                    :key="creature.id"
                    class="p-3 bg-blue-50 rounded-lg"
                >
                  <p class="font-medium">{{ creature.name }}</p>
                  <p class="text-sm text-gray-600">ID: {{ creature.id }}, Тип: {{ creature.creatureType ?? 'Не указан' }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Операция 3: Объекты с кольцом слабее заданного -->
          <!-- Число вводится в поле, границы указаны под целое число базы данных -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Поиск объектов с кольцом меньше заданного
            </h3>
            <p class="text-sm text-gray-600 mb-3">
              Кольца сравниваются по силе (power)
            </p>
            <div class="flex gap-3">
              <input
                  v-model.number="ringThreshold"
                  type="number"
                  step="1"
                  min="1"
                  max="2147483647"
                  placeholder="Введите силу кольца"
                  class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <button
                  @click="findByRingBelow"
                  class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Найти
              </button>
            </div>
            <!-- Результат в виде списка существ с названием и силой их кольца -->
            <div v-if="ringResults !== null" class="mt-3 space-y-2">
              <p class="text-sm text-gray-600">Найдено: {{ ringResults.length }}</p>
              <div class="max-h-48 overflow-y-auto space-y-2">
                <div
                    v-for="creature in ringResults"
                    :key="creature.id"
                    class="p-3 bg-purple-50 rounded-lg"
                >
                  <p class="font-medium">{{ creature.name }}</p>
                  <p class="text-sm text-gray-600">
                    ID: {{ creature.id }}, Кольцо: {{ creature.ring.name }} (сила {{ creature.ring.power }})
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Операция 4: Забрать все кольца у хоббитов -->
          <!-- Оранжевая кнопка, потому что операция меняет данные. Ниже сообщение от сервера,
               зелёное при успехе и красное при ошибке -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Забрать все кольца у хоббитов
            </h3>
            <p class="text-sm text-gray-600 mb-3">
              Кольцо обязательно для каждого существа, поэтому хоббит получает кольцо-заглушку "Без кольца", а его настоящее кольцо остаётся в системе свободным
            </p>
            <button
                @click="takeRings"
                class="w-full px-6 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition-colors"
            >
              Забрать кольца
            </button>
            <div v-if="takeMessage" class="mt-3 p-3 rounded-lg" :class="takeSuccess ? 'bg-green-50' : 'bg-red-50'">
              <p :class="takeSuccess ? 'text-green-800' : 'text-red-800'">
                {{ takeMessage }}
              </p>
            </div>
          </div>

          <!-- Операция 5: Переместить хоббитов с кольцами в Мордор -->
          <!-- Красная кнопка, операция тоже меняет данные. Сообщение устроено так же как выше -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Переместить всех хоббитов с кольцами в Мордор
            </h3>
            <p class="text-sm text-gray-600 mb-3">
              Эта операция переместит всех хоббитов, у которых есть кольцо, в локацию "Мордор"
            </p>
            <button
                @click="moveHobbitsToMordor"
                class="w-full px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
            >
              Переместить в Мордор
            </button>
            <div v-if="mordorMessage" class="mt-3 p-3 rounded-lg" :class="mordorSuccess ? 'bg-green-50' : 'bg-red-50'">
              <p :class="mordorSuccess ? 'text-green-800' : 'text-red-800'">
                {{ mordorMessage }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// ref делает переменную следимой
import { ref } from 'vue'
// Запросы к серверу
import { api } from './api.js'
// Жёлтые предупреждения
import { useToast } from './composables/useToast.js'
// Красное уведомление об ошибке
import { showApiError } from './utils/errorHandler.js'

// Берём функцию показа предупреждений
const { warning } = useToast()

// Показывает красное уведомление об ошибке. Если у ошибки нет заголовка или текста, берёт обычные
function reportError(error: any) {
  showApiError({
    title: error.title || 'Ошибка',
    message: error.message || 'Ошибка при выполнении операции'
  })
}

// Операция 1: Объект с минимальными координатами
// Здесь лежит найденное существо, пока поиска не было там null
const minCoordinatesResult = ref<any>(null)

// Просит у сервера существо с минимальными координатами
async function findMinCoordinates() {
  try {
    minCoordinatesResult.value = await api.getMinCoordinates()
  } catch (error) {
    // При ошибке показываем уведомление и прячем старый результат
    reportError(error)
    minCoordinatesResult.value = null
  }
}

// Операция 2: Поиск по подстроке в имени
// Введённый текст и список найденных существ
const nameSubstring = ref('')
const nameResults = ref<any[] | null>(null)

async function findByNameSubstring() {
  // Если поле пустое, просим ввести текст и не отправляем запрос
  if (!nameSubstring.value) {
    warning('Введите подстроку')
    return
  }

  try {
    nameResults.value = await api.findByNameSubstring(nameSubstring.value)
  } catch (error) {
    reportError(error)
    nameResults.value = null
  }
}

// Операция 3: Поиск объектов с кольцом слабее заданного
// Введённая сила кольца и список найденных существ
const ringThreshold = ref<number | null>(null)
const ringResults = ref<any[] | null>(null)

async function findByRingBelow() {
  // Проверяем что число введено
  if (ringThreshold.value === null || (ringThreshold.value as any) === '') {
    warning('Введите силу кольца')
    return
  }
  // Проверяем что число целое и помещается в целое число базы данных
  if (!Number.isInteger(ringThreshold.value) || ringThreshold.value < 1 || ringThreshold.value > 2147483647) {
    warning('Сила кольца должна быть целым числом от 1 до 2147483647')
    return
  }

  try {
    ringResults.value = await api.findByRingBelow(ringThreshold.value)
  } catch (error) {
    reportError(error)
    ringResults.value = null
  }
}

// Операция 4: Забрать все кольца у хоббитов
// Текст сообщения и признак успеха, от него зависит цвет блока
const takeMessage = ref('')
const takeSuccess = ref(false)

async function takeRings() {
  // Спрашиваем подтверждение, потому что операция меняет данные
  if (!confirm('Вы уверены, что хотите забрать кольца у всех хоббитов?')) {
    return
  }

  try {
    const result = await api.takeRingsFromHobbits()
    takeSuccess.value = true
    // Сообщение приходит от сервера
    takeMessage.value = result.message
  } catch (error: any) {
    takeSuccess.value = false
    takeMessage.value = 'Ошибка: ' + error.message
  }
}

// Операция 5: Переместить хоббитов в Мордор
// То же самое, текст сообщения и признак успеха
const mordorMessage = ref('')
const mordorSuccess = ref(false)

async function moveHobbitsToMordor() {
  if (!confirm('Вы уверены, что хотите переместить всех хоббитов с кольцами в Мордор?')) {
    return
  }

  try {
    const result = await api.moveHobbitsToMordor()
    mordorSuccess.value = true
    mordorMessage.value = result.message
  } catch (error: any) {
    mordorSuccess.value = false
    mordorMessage.value = 'Ошибка: ' + error.message
  }
}
</script>