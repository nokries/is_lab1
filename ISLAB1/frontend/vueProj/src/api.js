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
  async getAllCreatures(params = {}) {
    const queryParams = new URLSearchParams()

    const page = (params.page || 1) - 1
    queryParams.append('page', page)

    if (params.search) {
      queryParams.append('filterBy', params.filterBy || 'name')
      queryParams.append('filter', params.search)
    }

    if (params.sort) queryParams.append('sort', params.sort)
    if (params.direction) queryParams.append('direction', params.direction)

    const response = await fetch(`${API_BASE_URL}/view?${queryParams}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения объектов')
    }

    const data = await response.json()

    return {
      content: data.content,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      currentPage: data.number + 1
    }
  },


  async getCreatureById(id) {
    const response = await fetch(`${API_BASE_URL}/view/${id}`)
    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения объекта')
    }
    return await response.json()
  },

  async createCreature(data) {
    const response = await fetch(`${API_BASE_URL}/interact`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка создания объекта')
    }

    return await response.json()
  },

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


  async deleteCreature(id) {
    const response = await fetch(`${API_BASE_URL}/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка удаления объекта')
    }

    return true
  },

  async getCities(page = 1) {
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


  async getCityById(id) {
    const response = await fetch(`${API_BASE_URL}/city/view/${id}`)
    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения города')
    }
    return await response.json()
  },

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

  async deleteCity(id) {
    const response = await fetch(`${API_BASE_URL}/city/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка удаления города')
    }

    return true
  },

  async getRings(page = 1) {
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

  async getRingById(id) {
    const response = await fetch(`${API_BASE_URL}/ring/view/${id}`)
    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения кольца')
    }
    return await response.json()
  },

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


  async deleteRing(id) {
    const response = await fetch(`${API_BASE_URL}/ring/delete/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка удаления кольца')
    }

    return true
  },


  async getMinCoordinates() {
    const response = await fetch(`${API_BASE_URL}/special/minCoordinates`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка получения объекта')
    }

    return await response.json()
  },


  async findByNameSubstring(substring) {
    const encoded = encodeURIComponent(substring)
    const response = await fetch(`${API_BASE_URL}/special/nameMatcher/${encoded}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка поиска по имени')
    }

    return await response.json()
  },


  async findByRingBelow(value) {
    const response = await fetch(`${API_BASE_URL}/special/ringBelow/${value}`)

    if (!response.ok) {
      await throwApiError(response, 'Ошибка поиска объектов')
    }

    return await response.json()
  },


  async takeRingsFromHobbits() {
    const response = await fetch(`${API_BASE_URL}/special/takeRings`, {
      method: 'POST'
    })

    if (!response.ok) {
      await throwApiError(response, 'Ошибка при изъятии колец')
    }

    return await response.json()
  },


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