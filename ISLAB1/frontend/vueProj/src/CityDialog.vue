<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">
          {{ mode === 'create' ? 'Создать город' : 'Редактировать город' }}
        </h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>


      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Название <span class="text-red-500">*</span>
          </label>
          <input
              v-model="formData.name"
              type="text"
              required
              minlength="1"
              placeholder="Например: Шир"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Площадь <span class="text-red-500">*</span>
            </label>
            <input
                v-model.number="formData.area"
                type="number"
                required
                min="0.01"
                step="any"
                placeholder="1000"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Население <span class="text-red-500">*</span>
            </label>
            <input
                v-model.number="formData.population"
                type="number"
                required
                min="1"
                step="1"
                placeholder="5000"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Правитель
          </label>
          <select
              v-model="formData.governor"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          >
            <option value="">Не указан</option>
            <option value="HOBBIT">Хоббит</option>
            <option value="ELF">Эльф</option>
            <option value="HUMAN">Человек</option>
            <option value="GOLLUM">Голлум</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Плотность населения <span class="text-red-500">*</span>
          </label>
          <input
              v-model.number="formData.populationDensity"
              type="number"
              required
              min="1"
              step="1"
              placeholder="5"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Дата основания
          </label>
          <input
              v-model="formData.establishmentDate"
              type="datetime-local"
              min="1000-01-01T00:00"
              max="9999-12-31T23:59"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          />
          <p class="text-xs text-gray-500 mt-1">Необязательно. Если не указана, будет подставлено текущее время. Год от 1000 до 9999</p>
        </div>


        <div class="flex items-center gap-2">
          <input
              v-model="formData.capital"
              type="checkbox"
              id="capital"
              class="w-4 h-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
          />
          <label for="capital" class="text-sm text-gray-700">Столица</label>
        </div>


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
              class="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center gap-2"
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

import { ref, onMounted } from 'vue'
import { X, Save } from 'lucide-vue-next'
import { api } from './api.js'
import { useToast } from './composables/useToast.js'
import { showApiError, showApiSuccess } from './utils/errorHandler.js'

const { warning } = useToast()

const props = defineProps({
  cityId: {
    type: Number,
    default: null
  },
  mode: {
    type: String,
    default: 'create',
    validator: (value) => ['create', 'edit'].includes(value)
  }
})

const emit = defineEmits(['close', 'saved'])

const formData = ref({
  name: '',
  area: null,
  population: null,
  governor: '',
  establishmentDate: '',
  capital: false,
  populationDensity: null
})


function toLocalInput(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}`
}

async function loadCity() {
  if (!props.cityId) return

  try {
    const data = await api.getCityById(props.cityId)
    formData.value = {
      ...data,
      governor: data.governor || '',
      establishmentDate: toLocalInput(data.establishmentDate)
    }
  } catch (error) {
    console.error('Ошибка загрузки города:', error)
    showApiError({ title: 'Ошибка', message: 'Не удалось загрузить данные города' })
  }
}

function isValidDate(value) {
  if (!value) return true
  const d = new Date(value)
  return !isNaN(d.getTime()) && d.getFullYear() >= 1000 && d.getFullYear() <= 9999
}

async function handleSubmit() {
  if (!formData.value.name || formData.value.name.trim() === '') {
    warning('Название города не может быть пустым')
    return
  }

  if (!isValidDate(formData.value.establishmentDate)) {
    warning('Дата основания указана неверно: введите полную дату и время, год от 1000 до 9999')
    return
  }

  if (!formData.value.area || formData.value.area <= 0) {
    warning('Площадь должна быть больше 0')
    return
  }

  if (!formData.value.population || formData.value.population <= 0) {
    warning('Население должно быть больше 0')
    return
  }

  if (!formData.value.populationDensity || formData.value.populationDensity <= 0) {
    warning('Плотность населения должна быть больше 0')
    return
  }

  const submitData = {
    ...formData.value,
    governor: formData.value.governor || null,
    establishmentDate: formData.value.establishmentDate
        ? new Date(formData.value.establishmentDate).toISOString()
        : null
  }

  try {
    if (props.mode === 'create') {
      await api.createCity(submitData)
      showApiSuccess('Город успешно создан!')
    } else {
      await api.updateCity(props.cityId, submitData)
      showApiSuccess('Город успешно обновлен!')
    }
    emit('saved')
  } catch (error) {
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

onMounted(() => {
  if (props.mode === 'edit') {
    loadCity()
  }
})
</script>