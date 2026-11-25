import { useToast } from '../composables/useToast.js'

const { error: showError, success: showSuccess } = useToast()


const TITLES = {
  'Bad Request': 'Некорректный запрос',
  'Validation Failed': 'Ошибка проверки данных',
  'Resource Not Found': 'Объект не найден',
  'Data integrity violation': 'Нарушение целостности данных',
  'Database error': 'Ошибка базы данных',
  'JPA error': 'Ошибка запроса к данным',
  'Unexpected error': 'Непредвиденная ошибка'
}

export async function parseApiError(response) {
  try {
    const contentType = response.headers.get('content-type')

    if (contentType && contentType.includes('application/json')) {
      const errorData = await response.json()

      if (errorData.status && errorData.error && errorData.message) {
        return {
          status: errorData.status,
          title: TITLES[errorData.error] || errorData.error,
          message: errorData.message
        }
      }
    }

    const text = await response.text()
    return {
      status: response.status,
      title: 'Ошибка',
      message: text || response.statusText
    }
  } catch (e) {
    return {
      status: response.status,
      title: 'Ошибка',
      message: response.statusText
    }
  }
}

function escapeHtml(value) {
  return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;')
}

export function showApiError(errorData) {
  const message = `<strong>${escapeHtml(errorData.title)}</strong><br>${escapeHtml(errorData.message)}`
  showError(message, 7000)
}

export function showApiSuccess(message) {
  showSuccess(message, 3000)
}