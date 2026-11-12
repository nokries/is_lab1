<template>
  <!-- Серый фон на весь экран, внутри белая карточка по центру -->
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-7xl mx-auto">
      <div class="bg-white rounded-lg shadow-sm p-6">
        <!-- Шапка с заголовком и кнопкой создания. Кнопка открывает меню выбора что создавать -->
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-3xl font-bold text-gray-900">Управление BookCreature</h1>
          <button
              @click="openCreateDialog"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <Plus :size="20" />
            Создать объект
          </button>
        </div>

        <!-- Фильтры и поиск -->
        <!-- Первый список выбирает по какой колонке фильтровать, при смене сбрасывается поиск -->
        <div class="mb-6 flex gap-4">
          <select
              v-model="filterBy"
              @change="handleFilterBy"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              title="Колонка, по которой фильтруем"
          >
            <option value="name">Имя</option>
            <option value="creatureType">Тип</option>
            <option value="city">Город</option>
            <option value="ring">Кольцо</option>
          </select>
          <!-- Если фильтруем по типу, показываем список типов, иначе обычное поле для текста.
               Поиск запускается сразу при выборе или при вводе каждой буквы -->
          <div class="flex-1">
            <select
                v-if="filterBy === 'creatureType'"
                v-model="searchQuery"
                @change="handleSearch"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Все типы</option>
              <option value="HOBBIT">HOBBIT</option>
              <option value="ELF">ELF</option>
              <option value="HUMAN">HUMAN</option>
              <option value="GOLLUM">GOLLUM</option>
            </select>
            <input
                v-else
                v-model="searchQuery"
                @input="handleSearch"
                type="text"
                placeholder="Фильтр: точное совпадение..."
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <!-- Список для выбора поля сортировки. Значение попадёт в параметр sort на сервере -->
          <select
              v-model="sortField"
              @change="handleSort"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Сортировка</option>
            <option value="id">По ID</option>
            <option value="name">По имени</option>
            <option value="age">По возрасту</option>
            <option value="creatureType">По типу</option>
            <option value="attackLevel">По атаке</option>
            <option value="creationDate">По дате создания</option>
            <option value="creatureLocation.name">По городу</option>
            <option value="ring.name">По кольцу</option>
          </select>
          <!-- Направление сортировки показываем только когда поле сортировки выбрано -->
          <select
              v-if="sortField"
              v-model="sortDirection"
              @change="handleSortDirection"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              title="Направление сортировки"
          >
            <option value="asc">По возрастанию ↑</option>
            <option value="desc">По убыванию ↓</option>
          </select>
        </div>

        <!-- Таблица -->
        <!-- Если таблица широкая, появляется горизонтальная прокрутка -->
        <div class="overflow-x-auto">
          <table class="w-full">
            <!-- Заголовки колонок -->
            <thead class="bg-gray-100 border-b border-gray-200">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Имя</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Возраст</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Тип</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Координаты</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Атака</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Дата создания</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Город</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Кольцо</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Действия</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
            <!-- Для каждого существа из списка рисуется своя строка, key помогает Vue не путать строки -->
            <tr
                v-for="creature in creatures"
                :key="creature.id"
                class="hover:bg-gray-50 transition-colors"
            >
              <td class="px-4 py-3 text-sm text-gray-900">{{ creature.id }}</td>
              <td class="px-4 py-3 text-sm text-gray-900 font-medium">{{ creature.name }}</td>
              <!-- Если возраста или типа нет, показываем прочерк -->
              <td class="px-4 py-3 text-sm text-gray-600">{{ creature.age ?? '—' }}</td>
              <td class="px-4 py-3 text-sm">
                  <span class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs">
                    {{ creature.creatureType ?? '—' }}
                  </span>
              </td>
              <td class="px-4 py-3 text-sm text-gray-600">
                ({{ creature.coordinates.x }}, {{ creature.coordinates.y }})
              </td>
              <td class="px-4 py-3 text-sm text-gray-600">
                {{ creature.attackLevel }}
              </td>
              <!-- Дату красиво оформляет функция formatDate -->
              <td class="px-4 py-3 text-sm text-gray-600">
                {{ formatDate(creature.creationDate) }}
              </td>
              <!-- Знак ? после названия защищает от ошибки если города или кольца нет -->
              <td class="px-4 py-3 text-sm text-gray-600">{{ creature.creatureLocation?.name ?? '—' }}</td>
              <td class="px-4 py-3 text-sm text-gray-600">{{ creature.ring?.name ?? '—' }}</td>
              <!-- Три кнопки в строке. Просмотр, редактирование и удаление нужного существа -->
              <td class="px-4 py-3 text-sm">
                <div class="flex gap-2">
                  <button
                      @click="viewCreature(creature.id)"
                      class="p-1 text-blue-600 hover:bg-blue-50 rounded"
                      title="Просмотр"
                  >
                    <Eye :size="18" />
                  </button>
                  <button
                      @click="editCreature(creature.id)"
                      class="p-1 text-green-600 hover:bg-green-50 rounded"
                      title="Редактировать"
                  >
                    <Edit :size="18" />
                  </button>
                  <button
                      @click="deleteCreature(creature.id)"
                      class="p-1 text-red-600 hover:bg-red-50 rounded"
                      title="Удалить"
                  >
                    <Trash2 :size="18" />
                  </button>
                </div>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <!-- Низ таблицы. Слева надпись сколько записей показано, справа кнопки страниц -->
        <div class="mt-6 flex justify-between items-center">
          <div class="text-sm text-gray-600">
            Показано {{ (currentPage - 1) * pageSize + 1 }}-{{ Math.min(currentPage * pageSize, totalItems) }} из {{ totalItems }}
          </div>
          <!-- Кнопка назад отключена на первой странице, кнопка вперёд на последней -->
          <div class="flex gap-2">
            <button
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 1"
                class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronLeft :size="20" />
            </button>
            <span class="px-4 py-2 border border-gray-300 rounded-lg bg-blue-50 text-blue-700">
              {{ currentPage }}
            </span>
            <button
                @click="changePage(currentPage + 1)"
                :disabled="currentPage >= totalPages"
                class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronRight :size="20" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Диалоги. Каждый показывается только когда его флаг равен true.
         Слушаем события от диалогов, close закрывает окно, saved значит что данные сохранены -->
    <!-- Меню выбора что создавать, существо, кольцо или город -->
    <CreateMenuDialog
        v-if="showCreateMenu"
        @close="showCreateMenu = false"
        @create-creature="openCreatureDialog"
        @create-ring="openRingDialog"
        @create-city="openCityDialog"
    />

    <!-- Окно существа. Передаём ему номер и режим создания или редактирования -->
    <CreatureDialog
        v-if="showCreatureDialog"
        :creature-id="selectedCreatureId"
        :mode="dialogMode"
        @close="closeCreatureDialog"
        @saved="handleSaved"
    />

    <!-- Окно кольца -->
    <RingDialog
        v-if="showRingDialog"
        :ring-id="selectedRingId ?? undefined"
        :mode="ringDialogMode"
        @close="closeRingDialog"
        @saved="handleSaved"
    />

    <!-- Окно города -->
    <CityDialog
        v-if="showCityDialog"
        :city-id="selectedCityId ?? undefined"
        :mode="cityDialogMode"
        @close="closeCityDialog"
        @saved="handleSaved"
    />

    <!-- Окно просмотра существа. Из него можно перейти к редактированию -->
    <ViewDialog
        v-if="showViewDialog"
        :creature-id="selectedCreatureId as number"
        @close="showViewDialog = false"
        @edit="editFromView"
    />
  </div>
</template>

<script setup lang="ts">
// Нужные функции Vue
import { ref, onMounted, onUnmounted } from 'vue'
// Иконки для кнопок
import { Plus, Eye, Edit, Trash2, ChevronLeft, ChevronRight } from 'lucide-vue-next'
// Все диалоги которые открываются из таблицы
import CreateMenuDialog from './CreateMenuDialog.vue'
import CreatureDialog from './CreatureDialog.vue'
import RingDialog from './RingDialog.vue'
import CityDialog from './CityDialog.vue'
import ViewDialog from './ViewDialog.vue'
// Запросы к серверу и показ уведомлений
import { api } from './api.js'
import { showApiError, showApiSuccess } from './utils/errorHandler.js'
// Подключение к серверу по WebSocket
import { useWebSocket } from './composables/useWebSocket.js'

// Описание того как выглядят данные с сервера. Это нужно только для TypeScript,
// в работающую программу эти описания не попадают
// Координаты
interface Coordinates {
  x: number
  y: number
}

// Город
interface MagicCity {
  id: number
  name: string
  area: number
  population: number
  establishmentDate: string
  governor: string | null
  capital: boolean
  populationDensity: number
}

// Кольцо
interface Ring {
  id: number
  name: string
  power: number
  weight: number
}

// Существо, в нём лежат координаты, город и кольцо. Город может быть пустым, кольцо нет
interface BookCreature {
  id: number
  name: string
  coordinates: Coordinates
  creationDate: string
  age: number | null
  creatureType: 'HOBBIT' | 'ELF' | 'HUMAN' | 'GOLLUM' | null
  creatureLocation: MagicCity | null
  attackLevel: number
  ring: Ring
}

// Список существ на текущей странице и данные про страницы
const creatures = ref<BookCreature[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = ref(0)
// Текст поиска, колонка для фильтра, поле и направление сортировки
const searchQuery = ref('')
const filterBy = ref('name')
const sortField = ref('')
const sortDirection = ref<'asc' | 'desc'>('asc')

// Какие окна сейчас открыты
const showCreateMenu = ref(false)
const showCreatureDialog = ref(false)
const showRingDialog = ref(false)
const showCityDialog = ref(false)
const showViewDialog = ref(false)

// С каким объектом сейчас работаем, когда нужно открыть окно редактирования или просмотра
const selectedCreatureId = ref<number | null>(null)
const selectedRingId = ref<number | null>(null)
const selectedCityId = ref<number | null>(null)

// Режим каждого окна, создание или редактирование
const dialogMode = ref<'create' | 'edit'>('create')
const ringDialogMode = ref<'create' | 'edit'>('create')
const cityDialogMode = ref<'create' | 'edit'>('create')

// Берём из useWebSocket функции для подключения и работы с сообщениями
const { isConnected, connect, disconnect, addMessageHandler, removeMessageHandler } = useWebSocket()


function getCacheKey() {
  return JSON.stringify({
    page: currentPage.value,
    filterBy: filterBy.value,
    search: searchQuery.value,
    sort: sortField.value,
    direction: sortDirection.value
  })
}

interface PageData {
  content: BookCreature[]
  totalElements: number
  totalPages: number
  currentPage: number
}

// Загружает с сервера текущую страницу таблицы с учётом фильтра и сортировки
const pageCache = new Map<string, PageData>()

async function fetchCreatures() {
  const hasSearch = searchQuery.value.trim() !== ''
  const key = getCacheKey()

  // Результаты поиска из кеша не берём
  if (!hasSearch) {
    const cached = pageCache.get(key)

    if (cached) {
      creatures.value = cached.content
      totalItems.value = cached.totalElements
      totalPages.value = cached.totalPages
      return
    }
  }

  try {
    const data = await api.getAllCreatures({
      page: currentPage.value,
      size: pageSize.value,
      filterBy: filterBy.value,
      search: searchQuery.value,
      sort: sortField.value,
      direction: sortDirection.value
    })

    // Сохраняем только обычные страницы без поиска
    if (!hasSearch) {
      pageCache.set(key, data)
    }

    creatures.value = data.content
    totalItems.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (error) {
    console.error('Ошибка загрузки объектов:', error)

    showApiError({
      title: 'Ошибка загрузки',
      message: 'Не удалось загрузить данные с сервера'
    })
  }
}



// Удаляет существо. Сначала спрашиваем подтверждение у пользователя
async function deleteCreature(id: number) {
  if (!confirm('Вы уверены, что хотите удалить этот объект? Связанные объекты также будут удалены.')) {
    return
  }

  try {
    await api.deleteCreature(id)

    showApiSuccess('Объект успешно удален!')
    // После удаления загружаем таблицу заново
    await fetchCreatures()
  } catch (error: any) {
    // Если это ошибка сервера, показываем её как есть, иначе общее сообщение
    if (error.status && error.title) {
      showApiError(error)
    } else {
      showApiError({
        title: 'Ошибка удаления',
        message: error.message || 'Не удалось удалить объект'
      })
    }
  }
}

// Открывает меню выбора что создавать
function openCreateDialog() {
  showCreateMenu.value = true
}

// Закрывает меню и открывает пустое окно создания существа
function openCreatureDialog() {
  showCreateMenu.value = false
  dialogMode.value = 'create'
  selectedCreatureId.value = null
  showCreatureDialog.value = true
}

// То же самое для кольца
function openRingDialog() {
  showCreateMenu.value = false
  ringDialogMode.value = 'create'
  selectedRingId.value = null
  showRingDialog.value = true
}

// То же самое для города
function openCityDialog() {
  showCreateMenu.value = false
  cityDialogMode.value = 'create'
  selectedCityId.value = null
  showCityDialog.value = true
}

// Открывает окно существа в режиме редактирования
function editCreature(id: number) {
  dialogMode.value = 'edit'
  selectedCreatureId.value = id
  showCreatureDialog.value = true
}

// Открывает окно просмотра
function viewCreature(id: number) {
  selectedCreatureId.value = id
  showViewDialog.value = true
}

// Из окна просмотра переходим к редактированию. Просмотр закрываем и открываем редактирование
function editFromView(id: number) {
  showViewDialog.value = false
  editCreature(id)
}

// Функции закрытия окон. Прячут окно и забывают выбранный номер
function closeCreatureDialog() {
  showCreatureDialog.value = false
  selectedCreatureId.value = null
}

function closeRingDialog() {
  showRingDialog.value = false
  selectedRingId.value = null
}

function closeCityDialog() {
  showCityDialog.value = false
  selectedCityId.value = null
}

// Когда любое окно сообщило что сохранило данные, закрываем окна и обновляем таблицу
function handleSaved() {
  closeCreatureDialog()
  closeRingDialog()
  closeCityDialog()

  pageCache.clear()
  fetchCreatures()
}

// Переход на другую страницу
function changePage(page: number) {
  currentPage.value = page
  fetchCreatures()
}

// При новом поиске возвращаемся на первую страницу и загружаем данные
let searchTimer: ReturnType<typeof setTimeout> | null = null

function handleSearch() {
  currentPage.value = 1

  if (searchTimer) {
    clearTimeout(searchTimer)
  }

  searchTimer = setTimeout(() => {
    fetchCreatures()
  }, 400)
}

// При смене колонки для фильтра очищаем текст поиска
function handleFilterBy() {
  searchQuery.value = ''
  currentPage.value = 1
  fetchCreatures()
}

// При смене сортировки тоже начинаем с первой страницы
function handleSort() {
  currentPage.value = 1
  fetchCreatures()
}

function handleSortDirection() {
  currentPage.value = 1
  fetchCreatures()
}

// Превращает дату с сервера в понятный вид по-русски, например 25.10.2026, 14:30
function formatDate(dateString: string): string {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleString('ru-RU', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Вызывается когда от сервера по WebSocket пришло сообщение, что кто-то что-то изменил
function handleWebSocketMessage(message: any) {
  // Достаём из сообщения действие, номер объекта и сам объект
  const { action, entityId, payload } = message

  // Сервер присылает тип объекта (CREATURE / CITY / RING) в поле entityType
  let entityType = message.entityType || null

  // Запасной вариант (старый формат сообщения без entityType): определяем тип по структуре payload
  if (!entityType && payload) {
    if ('creatureType' in payload || ('age' in payload && 'coordinates' in payload)) {
      entityType = 'CREATURE'
    } else if ('power' in payload && 'weight' in payload && !('area' in payload) && !('creatureType' in payload)) {
      entityType = 'RING'
    } else if (('area' in payload || 'population' in payload || 'governor' in payload) && !('creatureType' in payload)) {
      entityType = 'CITY'
    }
  }

  // Если тип так и не определили, сообщение игнорируем
  if (!entityType) {
    return
  }

  // Кольцо или город изменились - в таблице есть их названия, поэтому просто перечитываем страницу
  if (entityType === 'RING' || entityType === 'CITY') {

    pageCache.clear()

    fetchCreatures()
    return
  }

  // Обрабатываем только сообщения о BookCreature
  if (entityType !== 'CREATURE') {
    return
  }

  // Смотрим что именно сделали с существом
  switch (action) {
    case 'CREATED':
      pageCache.clear()
      fetchCreatures()
      break

    case 'UPDATED':
      pageCache.clear()
      fetchCreatures()
      break

    case 'DELETED': {

      pageCache.clear()

      // Сразу убираем строку у себя, затем перечитываем страницу (чтобы подтянуть следующую запись и счётчик)
      const deleteIndex = creatures.value.findIndex(c => c.id === entityId)
      if (deleteIndex !== -1) {
        creatures.value.splice(deleteIndex, 1)
      }
      // Если страница опустела, переходим на предыдущую
      if (creatures.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
      }

      fetchCreatures()
      break
    }
  }
}



// Когда страница появилась, загружаем таблицу, подключаемся к серверу и подписываемся на сообщения
onMounted(() => {
  fetchCreatures()

  connect(() => {
    console.log('WebSocket подключён и готов к работе')
  })

  addMessageHandler('mainTable', handleWebSocketMessage)
})

// Когда страница пропала, отписываемся и отключаемся
onUnmounted(() => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  removeMessageHandler('mainTable')
  disconnect()
})
</script>