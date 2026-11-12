// Функция показа уведомлений
import { useToast } from '../composables/useToast.js'

// Берём из уведомлений две функции: показать ошибку и показать успех
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

// Разбирает ответ сервера с ошибкой и возвращает код, заголовок и текст ошибки
export async function parseApiError(response) {
  try {
    // формат ответа
    const contentType = response.headers.get('content-type')

    // Если это JSON (так отвечает наш обработчик ошибок на сервере)
    if (contentType && contentType.includes('application/json')) {
      const errorData = await response.json()

      // все нужные поля на месте
      if (errorData.status && errorData.error && errorData.message) {
        return {
          status: errorData.status,
          // Ищем русский заголовок в словаре, а если нет, берём как пришёл
          title: TITLES[errorData.error] || errorData.error,
          message: errorData.message
        }
      }
    }

    // Если ответ не в нашем формате, берём его как обычный текст
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

// Тост выводится через v-html, поэтому текст с сервера экранируем:
// иначе введённое пользователем значение выполнилось бы как HTML

function escapeHtml(value) {
  // Пустое значение заменяем пустой строкой и приводим всё к тексту
  return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;')
}

// уведомление об ошибке
export function showApiError(errorData) {
  const message = `<strong>${escapeHtml(errorData.title)}</strong><br>${escapeHtml(errorData.message)}`
  showError(message, 7000)
}

// уведомление об успехе
export function showApiSuccess(message) {
  showSuccess(message, 3000)
}