import { parseApiError } from './utils/errorHandler.js'


const API_BASE_URL = '/api'

async function throwApiError(response, defaultMessage) {

  const data = await parseApiError(response)

  const err = new Error(data.message || defaultMessage)
  err.status = data.status
  err.title = data.title

  throw err
}


export const api = {
  // Получить все объекты (с пагинацией и фильтрацией на сервере)
  async getAllCreatures(params = {}) {
    const queryParams = new URLSearchParams()

    // Конвертируем 1-based в 0-based для Spring
    // (пользователь видит страницы с 1, а Spring считает с 0)
    const page = (params.page || 1) - 1
    queryParams.append('page', page)

    if (params.search) {
      queryParams.append('filterBy', params.filterBy || 'name')
      queryParams.append('filter', params.search)
    }
    // Добавляем сортировку, если она выбрана
    if (params.sort) queryParams.append('sort', params.sort)
    if (params.direction) queryParams.append('direction', params.direction)

    const response = await fetch(`${API_BASE_URL}/view?${queryParams}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения объектов')
    }

    const data = await response.json()

    // Конвертируем обратно в 1-based для фронтенда
    return {
      content: data.content, // объекты на странице
      totalElements: data.totalElements, // сколько всего объектов
      totalPages: data.totalPages, // сколько всего страниц
      currentPage: data.number + 1 // Конвертируем 0-based в 1-based
    }
  },

  // Получить один объект по ID
  async getCreatureById(id) {
    const response = await fetch(`${API_BASE_URL}/view/${id}`)
    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения объекта')
    }
    return await response.json()
  },

  // Создать новый объект
  async createCreature(data) {
    const response = await fetch(`${API_BASE_URL}/interact`, {
      method: 'POST',
      headers: {
        // Сообщаем серверу, что в теле запроса лежит JSON
        'Content-Type': 'application/json',
      },
      // Превращаем объект в текст JSON
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка создания объекта')
    }

    // Возвращаем созданный объект (уже с id)
    return await response.json()
  },

  // Обновить объект
  async updateCreature(id, data) {
    const response = await fetch(`${API_BASE_URL}/modify/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка обновления объекта')
    }

    return await response.json()
  },

  // Удалить объект
  async deleteCreature(id) {
    const response = await fetch(`${API_BASE_URL}/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка удаления объекта')
    }

    // 204 No Content - успешное удаление
    // (тела ответа нет, поэтому просто возвращаем true)
    return true
  },

  // Получить города с пагинацией
  async getCities(page = 1) {
    // Конвертируем 1-based в 0-based для Spring
    const backendPage = page - 1
    const response = await fetch(`${API_BASE_URL}/city/view?page=${backendPage}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения городов')
    }

    const data = await response.json()

    return {
      content: data.content,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      currentPage: data.number + 1
    }
  },

  // Создать новый город
  async createCity(data) {
    const response = await fetch(`${API_BASE_URL}/city/interact`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка создания города')
    }

    return await response.json()
  },

  // Получить город по ID
  async getCityById(id) {
    const response = await fetch(`${API_BASE_URL}/city/view/${id}`)
    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения города')
    }
    return await response.json()
  },

  // Обновить город
  async updateCity(id, data) {
    const response = await fetch(`${API_BASE_URL}/city/modify/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка обновления города')
    }

    return await response.json()
  },

  // Удалить город
  async deleteCity(id) {
    const response = await fetch(`${API_BASE_URL}/city/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка удаления города')
    }

    return true
  },

  // Получить кольца с пагинацией
  async getRings(page = 1) {
    // Конвертируем 1-based в 0-based для Spring
    const backendPage = page - 1
    const response = await fetch(`${API_BASE_URL}/ring/view?page=${backendPage}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения колец')
    }

    const data = await response.json()

    return {
      content: data.content,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      currentPage: data.number + 1
    }
  },

  // Создать новое кольцо
  async createRing(data) {
    const response = await fetch(`${API_BASE_URL}/ring/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка создания кольца')
    }

    return await response.json()
  },

  // Получить кольцо по ID
  async getRingById(id) {
    const response = await fetch(`${API_BASE_URL}/ring/view/${id}`)
    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения кольца')
    }
    return await response.json()
  },

  // Обновить кольцо
  async updateRing(id, data) {
    const response = await fetch(`${API_BASE_URL}/ring/modify/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка обновления кольца')
    }

    return await response.json()
  },

  // Удалить кольцо
  async deleteRing(id) {
    const response = await fetch(`${API_BASE_URL}/ring/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка удаления кольца')
    }

    return true
  },

  // Специальные операции

  // Объект с минимальными координатами
  async getMinCoordinates() {
    const response = await fetch(`${API_BASE_URL}/special/minCoordinates`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения объекта')
    }

    return await response.json()
  },

  // Поиск объектов по подстроке в имени
  // Используем encodeURIComponent для корректной обработки строк с цифрами и спецсимволами в PathVariable
  async findByNameSubstring(substring) {
    // Кодируем строку, чтобы пробелы и спецсимволы не ломали адрес
    const encoded = encodeURIComponent(substring)
    const response = await fetch(`${API_BASE_URL}/special/nameMatcher/${encoded}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка поиска по имени')
    }

    return await response.json()
  },

  // Поиск объектов, у которых кольцо слабее заданного (сравнение по силе кольца)
  async findByRingBelow(value) {
    const response = await fetch(`${API_BASE_URL}/special/ringBelow/${value}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка поиска объектов')
    }

    return await response.json()
  },

  // Забрать все кольца у хоббитов
  async takeRingsFromHobbits() {
    const response = await fetch(`${API_BASE_URL}/special/takeRings`, {
      method: 'POST'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка при изъятии колец')
    }

    return await response.json()
  },

  // Переместить хоббитов с кольцами в Мордор
  async moveHobbitsToMordor() {
    const response = await fetch(`${API_BASE_URL}/special/move`, {
      method: 'POST'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка перемещения в Мордор')
    }

    return await response.json()
  }

}