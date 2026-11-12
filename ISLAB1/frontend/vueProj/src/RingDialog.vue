<template>
  <!-- Тёмный фон на весь экран, а по центру белое окно с формой -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Шапка окна с заголовком, который зависит от режима, и крестиком закрытия -->
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">
          {{ mode === 'create' ? 'Создать кольцо' : 'Редактировать кольцо' }}
        </h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>

      <!-- Форма. При отправке вызывается handleSubmit, а .prevent отменяет перезагрузку страницы -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <!-- Название кольца, обязательное поле. v-model связывает его с formData.name -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Название <span class="text-red-500">*</span>
          </label>
          <input
              v-model="formData.name"
              type="text"
              required
              minlength="1"
              placeholder="Например: Единое Кольцо"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
          />
        </div>

        <!-- Сила и вес в две колонки. Оба числа обязательны и не меньше 1,
             а .number превращает введённое в число -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Сила <span class="text-red-500">*</span>
            </label>
            <input
                v-model.number="formData.power"
                type="number"
                required
                min="1"
                step="1"
                placeholder="9999"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Вес <span class="text-red-500">*</span>
            </label>
            <input
                v-model.number="formData.weight"
                type="number"
                required
                min="1"
                step="1"
                placeholder="5"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
            />
          </div>
        </div>

        <!-- Кнопки. Отмена закрывает окно, а кнопка с типом submit отправляет форму.
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
              class="px-6 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 transition-colors flex items-center gap-2"
          >
            <Save :size="18" />
            {{ mode === 'create' ? 'Создать' : 'Сохранить' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
// ref делает переменную следимой, onMounted выполняется когда окно появилось
import { ref, onMounted } from 'vue'
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

// Данные которые родитель передаёт в окно, номер кольца и режим окна.
// Для режима есть проверка, допустимы только create и edit
const props = defineProps({
  ringId: {
    type: Number,
    default: null
  },
  mode: {
    type: String,
    default: 'create',
    validator: (value) => ['create', 'edit'].includes(value)
  }
})

// События которые окно отправляет родителю, закрыться и кольцо сохранено
const emit = defineEmits(['close', 'saved'])

// Данные формы, поля связаны с ними через v-model
const formData = ref({
  name: '',
  power: null,
  weight: null
})

// Загружает кольцо с сервера и заполняет форму, нужно при редактировании
async function loadRing() {
  // Если номера нет, загружать нечего
  if (!props.ringId) return

  try {
    const data = await api.getRingById(props.ringId)
    // Копируем данные в форму
    formData.value = { ...data }
  } catch (error) {
    console.error('Ошибка загрузки кольца:', error)
    showApiError({ title: 'Ошибка', message: 'Не удалось загрузить данные кольца' })
  }
}

// Вызывается при отправке формы. Проверяет данные и отправляет их на сервер
async function handleSubmit() {
  // Проверяем что название не пустое и не состоит из одних пробелов
  if (!formData.value.name || formData.value.name.trim() === '') {
    warning('Название кольца не может быть пустым')
    return
  }

  // Сила и вес должны быть больше нуля
  if (!formData.value.power || formData.value.power <= 0) {
    warning('Сила должна быть больше 0')
    return
  }

  if (!formData.value.weight || formData.value.weight <= 0) {
    warning('Вес должен быть больше 0')
    return
  }

  try {
    // В режиме создания отправляем новое кольцо, иначе обновляем существующее
    if (props.mode === 'create') {
      await api.createRing(formData.value)
      showApiSuccess('Кольцо успешно создано!')
    } else {
      await api.updateRing(props.ringId, formData.value)
      showApiSuccess('Кольцо успешно обновлено!')
    }
    // Сообщаем родителю что кольцо сохранено
    emit('saved')
  } catch (error) {
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

// Когда окно появилось, при редактировании загружаем кольцо в форму
onMounted(() => {
  if (props.mode === 'edit') {
    loadRing()
  }
})
</script>