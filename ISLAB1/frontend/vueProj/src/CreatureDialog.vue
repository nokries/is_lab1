<template>
  <!-- Тёмный фон на весь экран, а по центру белое окно с формой, которое можно прокручивать -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Шапка окна с заголовком, который зависит от режима, и крестиком закрытия -->
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">
          {{ mode === 'create' ? 'Создать новый объект' : 'Редактировать объект' }}
        </h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>

      <!-- Форма. При отправке вызывается handleSubmit, а .prevent отменяет перезагрузку страницы -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <!-- Основная информация -->
        <!-- Имя, возраст и тип существа -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900">Основная информация</h3>

          <!-- Имя обязательно, v-model связывает поле с переменной formData.name -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Имя <span class="text-red-500">*</span>
            </label>
            <input
                v-model="formData.name"
                type="text"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <!-- Возраст необязателен, .number превращает введённое в число -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Возраст
            </label>
            <input
                v-model.number="formData.age"
                type="number"
                min="1"
                step="1"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <!-- Тип существа выбирается из списка, пустое значение значит что тип не указан -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Тип существа
            </label>
            <select
                v-model="formData.creatureType"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Не указан</option>
              <option value="HOBBIT">Хоббит</option>
              <option value="ELF">Эльф</option>
              <option value="HUMAN">Человек</option>
              <option value="GOLLUM">Голлум</option>
            </select>
          </div>
        </div>

        <!-- Координаты -->
        <!-- Два числа в две колонки, X не может быть больше 735 -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900">Координаты</h3>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                X <span class="text-red-500">*</span>
              </label>
              <input
                  v-model.number="formData.coordinates.x"
                  type="number"
                  step="1"
                  max="735"
                  required
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Y <span class="text-red-500">*</span>
              </label>
              <input
                  v-model.number="formData.coordinates.y"
                  type="number"
                  step="1"
                  required
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </div>

        <!-- Характеристики -->
        <!-- Уровень атаки, обязательное число не меньше 1 -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900">Характеристики</h3>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Уровень атаки <span class="text-red-500">*</span>
              </label>
              <input
                  v-model.number="formData.attackLevel"
                  type="number"
                  step="1"
                  min="1"
                  required
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </div>

        <!-- Локация -->
        <!-- Локация необязательна, галочка включает и выключает весь блок ниже -->
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Локация</h3>
            <label class="flex items-center gap-2">
              <input
                  v-model="hasLocation"
                  type="checkbox"
                  class="rounded border-gray-300"
              />
              <span class="text-sm text-gray-700">Добавить локацию</span>
            </label>
          </div>

          <!-- Блок показывается только если стоит галочка -->
          <div v-if="hasLocation" class="space-y-4 pl-4 border-l-2 border-blue-200">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Выберите город
              </label>
              <!-- Переключатель между двумя способами, взять готовый город или создать новый.
                   Значение true или false записывается в selectExistingCity.
                   При выборе готового города сразу загружается список городов -->
              <div class="mb-2">
                <label class="flex items-center gap-2 mb-2">
                  <input
                      v-model="selectExistingCity"
                      type="radio"
                      :value="true"
                      @change="loadCities(1)"
                      class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Выбрать существующий город</span>
                </label>
                <label class="flex items-center gap-2">
                  <input
                      v-model="selectExistingCity"
                      type="radio"
                      :value="false"
                      class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Создать новый город</span>
                </label>
              </div>

              <!-- Выпадающий список готовых городов, виден только при выборе существующего города.
                   Для каждого города из списка cities создаётся пункт, а выбранный id попадает в selectedCityId -->
              <div v-if="selectExistingCity" class="space-y-2">
                <select
                    v-model="selectedCityId"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                    @change="onCitySelected"
                >
                  <option :value="null">Выберите город</option>
                  <option
                      v-for="city in cities"
                      :key="city.id"
                      :value="city.id"
                  >
                    {{ city.name }} (население: {{ city.population }})
                  </option>
                </select>

                <!-- Пагинация для городов -->
                <!-- Показывается только если страниц больше одной.
                     Кнопки со стрелками листают страницы списка городов,
                     type="button" нужен чтобы они не отправляли всю форму.
                     На первой странице кнопка назад отключена, на последней кнопка вперёд -->
                <div v-if="citiesTotalPages > 1" class="flex items-center justify-between mt-2">
                  <div class="text-xs text-gray-600">
                    Страница {{ citiesCurrentPage }} из {{ citiesTotalPages }}
                  </div>
                  <div class="flex gap-1">
                    <button
                        type="button"
                        @click="changeCitiesPage(citiesCurrentPage - 1)"
                        :disabled="citiesCurrentPage === 1"
                        class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      ←
                    </button>
                    <span class="px-2 py-1 text-xs border border-gray-300 rounded bg-blue-50">
                      {{ citiesCurrentPage }}
                    </span>
                    <button
                        type="button"
                        @click="changeCitiesPage(citiesCurrentPage + 1)"
                        :disabled="citiesCurrentPage >= citiesTotalPages"
                        class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      →
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Поля нового города, показываются если выбрано создание нового города.
                 Атрибут required включается только при включённой локации -->
            <div v-if="!selectExistingCity" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Название <span class="text-red-500">*</span>
                </label>
                <input
                    v-model="formData.creatureLocation.name"
                    type="text"
                    :required="hasLocation"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    Площадь <span class="text-red-500">*</span>
                  </label>
                  <input
                      v-model.number="formData.creatureLocation.area"
                      type="number"
                      step="any"
                      min="0.01"
                      :required="hasLocation"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    Население <span class="text-red-500">*</span>
                  </label>
                  <input
                      v-model.number="formData.creatureLocation.population"
                      type="number"
                      step="1"
                      min="1"
                      :required="hasLocation"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Дата основания
                </label>
                <input
                    v-model="formData.creatureLocation.establishmentDate"
                    type="datetime-local"
                    min="1000-01-01T00:00"
                    max="9999-12-31T23:59"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Правитель
                </label>
                <select
                    v-model="formData.creatureLocation.governor"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Не указан</option>
                  <option value="HOBBIT">Хоббит</option>
                  <option value="ELF">Эльф</option>
                  <option value="HUMAN">Человек</option>
                  <option value="GOLLUM">Голлум</option>
                </select>
              </div>

              <label class="flex items-center gap-2">
                <input
                    v-model="formData.creatureLocation.capital"
                    type="checkbox"
                    class="rounded border-gray-300"
                />
                <span class="text-sm text-gray-700">Столица</span>
              </label>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Плотность населения <span class="text-red-500">*</span>
                </label>
                <input
                    v-model.number="formData.creatureLocation.populationDensity"
                    type="number"
                    step="1"
                    min="1"
                    :required="hasLocation"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Кольцо -->
        <!-- Кольцо обязательно, звёздочка в заголовке. Блок устроен так же как блок города,
             можно выбрать готовое кольцо из списка или создать новое -->
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Кольцо <span class="text-red-500">*</span></h3>
          </div>

          <div v-if="hasRing" class="space-y-4 pl-4 border-l-2 border-yellow-200">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Выберите кольцо
              </label>
              <!-- Переключатель между готовым и новым кольцом, при выборе готового загружается список колец -->
              <div class="mb-2">
                <label class="flex items-center gap-2 mb-2">
                  <input
                      v-model="selectExistingRing"
                      type="radio"
                      :value="true"
                      @change="loadRings(1)"
                      class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Выбрать существующее кольцо</span>
                </label>
                <label class="flex items-center gap-2">
                  <input
                      v-model="selectExistingRing"
                      type="radio"
                      :value="false"
                      class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Создать новое кольцо</span>
                </label>
              </div>

              <!-- Выпадающий список готовых колец, выбранный id попадает в selectedRingId -->
              <div v-if="selectExistingRing" class="space-y-2">
                <select
                    v-model="selectedRingId"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                    @change="onRingSelected"
                >
                  <option :value="null">Выберите кольцо</option>
                  <option
                      v-for="ring in rings"
                      :key="ring.id"
                      :value="ring.id"
                  >
                    {{ ring.name }} (Сила: {{ ring.power }}, Вес: {{ ring.weight }})
                  </option>
                </select>

                <!-- Пагинация для колец -->
                <!-- Работает так же как у городов, кнопки листают страницы списка колец -->
                <div v-if="ringsTotalPages > 1" class="flex items-center justify-between mt-2">
                  <div class="text-xs text-gray-600">
                    Страница {{ ringsCurrentPage }} из {{ ringsTotalPages }}
                  </div>
                  <div class="flex gap-1">
                    <button
                        type="button"
                        @click="changeRingsPage(ringsCurrentPage - 1)"
                        :disabled="ringsCurrentPage === 1"
                        class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      ←
                    </button>
                    <span class="px-2 py-1 text-xs border border-gray-300 rounded bg-blue-50">
                      {{ ringsCurrentPage }}
                    </span>
                    <button
                        type="button"
                        @click="changeRingsPage(ringsCurrentPage + 1)"
                        :disabled="ringsCurrentPage >= ringsTotalPages"
                        class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      →
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Поля нового кольца, название, сила и вес. Показываются если выбрано создание нового кольца -->
            <div v-if="!selectExistingRing" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Название <span class="text-red-500">*</span>
                </label>
                <input
                    v-model="formData.ring.name"
                    type="text"
                    :required="hasRing"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    Сила <span class="text-red-500">*</span>
                  </label>
                  <input
                      v-model.number="formData.ring.power"
                      type="number"
                      step="1"
                      min="1"
                      :required="hasRing"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    Вес <span class="text-red-500">*</span>
                  </label>
                  <input
                      v-model.number="formData.ring.weight"
                      type="number"
                      step="1"
                      min="1"
                      :required="hasRing"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Кнопки -->
        <!-- Отмена закрывает окно, а кнопка с типом submit отправляет форму.
             Подпись второй кнопки зависит от режима -->
        <div class="flex justify-end gap-3 pt-4 border-t border-gray-200">
          <button
              type="button"
              @click="$emit('close')"
              class="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Отмена
          </button>
          <button
              type="submit"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <Save :size="18" />
            {{ mode === 'create' ? 'Создать' : 'Сохранить' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
// ref делает переменную следимой, onMounted выполняется когда окно появилось, watch следит за изменениями
import { ref, onMounted, watch } from 'vue'
// Иконки крестика и дискеты
import { X, Save } from 'lucide-vue-next'
// Запросы к серверу
import { api } from './api.js'
// Жёлтые предупреждения
import { useToast } from './composables/useToast.js'
// Уведомления об ошибке и об успехе
import { showApiError, showApiSuccess } from './utils/errorHandler.js'

// Берём функцию показа предупреждений
const { warning } = useToast()

// Данные которые родитель передаёт в окно, номер существа для редактирования и режим окна.
// Запись с типами говорит что именно можно передавать
const props = defineProps<{
  creatureId: number | null
  mode: 'create' | 'edit'
}>()

// События которые окно отправляет родителю, закрыться и существо сохранено
const emit = defineEmits<{
  close: []
  saved: []
}>()

// Включена ли локация, по умолчанию нет
const hasLocation = ref(false)
const hasRing = ref(true) // кольцо обязательно (ring не может быть null)

// Выбран ли режим готового города или кольца (true) или создания нового (false)
const selectExistingCity = ref(false)
const selectExistingRing = ref(false)

// Состояние для городов
// Список городов для выпадающего меню, текущая и общая страницы и выбранный город
const cities = ref<any[]>([])
const citiesCurrentPage = ref(1)
const citiesTotalPages = ref(0)
const selectedCityId = ref<number | null>(null)

// Состояние для колец
// То же самое для списка колец
const rings = ref<any[]>([])
const ringsCurrentPage = ref(1)
const ringsTotalPages = ref(0)
const selectedRingId = ref<number | null>(null)

// Все данные формы. Поля формы связаны с этими значениями через v-model.
// Запись as number | null значит что значение может быть числом или пустым
const formData = ref({
  name: '',
  age: null as number | null,
  creatureType: '',
  coordinates: {
    x: null as number | null,
    y: null as number | null
  },
  attackLevel: null as number | null,
  creatureLocation: {
    name: '',
    area: null as number | null,
    population: null as number | null,
    establishmentDate: '',
    governor: '',
    capital: false,
    populationDensity: null as number | null
  },
  ring: {
    name: '',
    power: null as number | null,
    weight: null as number | null
  }
})

// Если галочку локации сняли, очищаем введённые данные города
watch(hasLocation, (newValue) => {
  if (!newValue) {
    formData.value.creatureLocation = {
      name: '',
      area: null,
      population: null,
      establishmentDate: '',
      governor: '',
      capital: false,
      populationDensity: null
    }
  }
})

// Если кольцо выключено, очищаем его данные и выбор
watch(hasRing, (newValue) => {
  if (!newValue) {
    formData.value.ring = {
      name: '',
      power: null,
      weight: null
    }
    selectedRingId.value = null
    selectExistingRing.value = false
  }
})

// Если галочку локации сняли, сбрасываем выбор готового города
watch(hasLocation, (newValue) => {
  if (!newValue) {
    selectedCityId.value = null
    selectExistingCity.value = false
  }
})

// Загрузка городов
// Запрашивает у сервера страницу городов и запоминает список и номера страниц
async function loadCities(page = 1) {
  try {
    const data = await api.getCities(page)
    cities.value = data.content
    citiesCurrentPage.value = data.currentPage
    citiesTotalPages.value = data.totalPages
  } catch (error) {
    console.error('Ошибка загрузки городов:', error)
    showApiError({ title: 'Ошибка', message: 'Не удалось загрузить список городов' })
  }
}

// Загрузка колец
// То же самое для колец
async function loadRings(page = 1) {
  try {
    const data = await api.getRings(page)
    rings.value = data.content
    ringsCurrentPage.value = data.currentPage
    ringsTotalPages.value = data.totalPages
  } catch (error) {
    console.error('Ошибка загрузки колец:', error)
    showApiError({ title: 'Ошибка', message: 'Не удалось загрузить список колец' })
  }
}

// Смена страницы городов
// Запоминает номер страницы и загружает её с сервера
function changeCitiesPage(page: number) {
  citiesCurrentPage.value = page
  loadCities(page)
}

// Смена страницы колец
function changeRingsPage(page: number) {
  ringsCurrentPage.value = page
  loadRings(page)
}

// Обработка выбора города
// Сейчас ничего не делает, выбранный id уже сохраняется через v-model
function onCitySelected() {
  if (selectedCityId.value) {
  }
}

// Обработка выбора кольца
function onRingSelected() {
  if (selectedRingId.value) {
  }
}

// Загружает существо с сервера и заполняет форму, нужно при редактировании
async function loadCreature() {
  // Если номера нет, загружать нечего
  if (!props.creatureId) return

  try {
    const data = await api.getCreatureById(props.creatureId)

    // Переносим данные в форму, а если локации или кольца нет, подставляем пустые значения
    formData.value = {
      ...data,
      creatureLocation: data.creatureLocation || {
        name: '',
        area: null,
        population: null,
        establishmentDate: '',
        governor: '',
        capital: false,
        populationDensity: null
      },
      ring: data.ring || {
        name: '',
        power: null,
        weight: null
      }
    }

    // Включаем галочки в зависимости от того есть ли локация и кольцо
    hasLocation.value = !!data.creatureLocation
    hasRing.value = !!data.ring

    // Если при редактировании уже есть город/кольцо, устанавливаем их id
    if (data.creatureLocation?.id) {
      selectedCityId.value = data.creatureLocation.id
      selectExistingCity.value = true
      // Загружаем список городов и ищем нужную страницу
      await loadCities(1)
      // Текущий город может быть не на первой странице - добавляем его в список, чтобы он был виден как выбранный
      if (!cities.value.some((c: any) => c.id === data.creatureLocation.id)) {
        cities.value.unshift(data.creatureLocation)
      }
    }

    if (data.ring?.id) {
      selectedRingId.value = data.ring.id
      selectExistingRing.value = true
      // Загружаем список колец (там только свободные кольца)
      await loadRings(1)
      // Текущее кольцо занято этим же существом, поэтому его нет в списке свободных - добавляем, чтобы оно было видно
      if (!rings.value.some((r: any) => r.id === data.ring.id)) {
        rings.value.unshift(data.ring)
      }
    }
  } catch (error) {
    console.error('Ошибка загрузки объекта:', error)
    showApiError({ title: 'Ошибка', message: 'Не удалось загрузить данные объекта' })
  }
}

// Дата из поля datetime-local -> ISO со смещением (так её принимает ZonedDateTime на сервере)
function toIso(value: string) {
  return value ? new Date(value).toISOString() : null
}

// Дата из поля datetime-local: пусто - можно, иначе год должен быть от 1000 до 9999
function isValidDate(value: string) {
  if (!value) return true
  const d = new Date(value)
  return !isNaN(d.getTime()) && d.getFullYear() >= 1000 && d.getFullYear() <= 9999
}

// Вызывается при отправке формы. Собирает данные, проверяет их и отправляет на сервер
async function handleSubmit() {
  let creatureLocation = null
  let ring = null

  // Формируем данные локации (необязательна)
  if (hasLocation.value) {
    if (selectExistingCity.value) {
      if (!selectedCityId.value) {
        warning('Выберите город из списка или создайте новый')
        return
      }
      // Используем существующий город - передаем только id
      creatureLocation = { id: selectedCityId.value }
    } else {
      // Создаем новый город - передаем все данные
      if (!isValidDate(formData.value.creatureLocation.establishmentDate)) {
        warning('Дата основания города указана неверно: введите полную дату и время, год от 1000 до 9999')
        return
      }
      creatureLocation = {
        ...formData.value.creatureLocation,
        establishmentDate: toIso(formData.value.creatureLocation.establishmentDate),
        governor: formData.value.creatureLocation.governor || null
      }
    }
  }

  // Формируем данные кольца (обязательно)
  if (selectExistingRing.value) {
    if (!selectedRingId.value) {
      warning('Выберите кольцо из списка или создайте новое')
      return
    }
    // Используем существующее кольцо - передаем только id
    ring = { id: selectedRingId.value }
  } else {
    // Создаем новое кольцо - передаем все данные
    ring = formData.value.ring
  }

  // Собираем итоговые данные для сервера. Пустой возраст и пустой тип заменяем на null
  const submitData = {
    ...formData.value,
    age: formData.value.age === null || (formData.value.age as any) === '' ? null : formData.value.age,
    creatureType: formData.value.creatureType || null,
    creatureLocation,
    ring
  }

  try {
    // В режиме создания отправляем нового, иначе обновляем существующего
    if (props.mode === 'create') {
      await api.createCreature(submitData)
      showApiSuccess('Объект успешно создан!')
    } else {
      await api.updateCreature(props.creatureId, submitData)
      showApiSuccess('Объект успешно обновлен!')
    }

    // Сообщаем родителю что существо сохранено
    emit('saved')
  } catch (error: any) {
    console.error('Ошибка при сохранении:', error)
    // Ошибку сервера показываем как есть, остальные ошибки общим сообщением
    if (error.status && error.title) {
      showApiError(error)
    } else {
      showApiError({
        title: 'Ошибка',
        message: error.message || 'Произошла ошибка при сохранении'
      })
    }
  }
}

// Когда окно появилось, при редактировании загружаем существо в форму
onMounted(() => {
  if (props.mode === 'edit') {
    loadCreature()
  }
})
</script>