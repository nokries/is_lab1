import { ref } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

export function useWebSocket() {
  const isConnected = ref(false) // связь с сервером
  const client = ref(null) // объект STOMP для открытия связи сервером и подписки на новости
  const messageHandlers = new Map() // список получателей: имя и вызванная при новости функция

  const connect = (onConnectCallback) => {
    const socket = new SockJS('/ws')
    
    client.value = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      onConnect: () => { // реакция на событие (есть соединение с сервером)
        console.log('WebSocket connected')
        isConnected.value = true
        
        client.value.subscribe('/topic/entities', (message) => { // подписка: функция вызывается при каждой новости
          try {
            const data = JSON.parse(message.body) // текст новости превращаем в данные
            console.log('WebSocket message received:', data)
            
            messageHandlers.forEach((handler) => { // для каждого получателя из списка вызываем его функцию и передаём новость
              handler(data)
            })
          } catch (error) {
            console.error('Error parsing WebSocket message:', error)
          }
        })

        if (onConnectCallback) { // если при вызове connect передали функцию, вызываем её
          onConnectCallback()
        }
      },

      onDisconnect: () => { // соединение с сервером пропало
        console.log('WebSocket disconnected')
        isConnected.value = false
      },

      onStompError: (frame) => { // ошибка на сервере
        console.error('STOMP error:', frame)
      }
    })

    client.value.activate() // запустить подключение
  }

  const disconnect = () => {
    if (client.value) {
      client.value.deactivate()
      client.value = null
      isConnected.value = false
    }
  }

  const addMessageHandler = (id, handler) => { // берёт каждую запомненную функцию и вызывает её с новостью
    messageHandlers.set(id, handler)
  }

  const removeMessageHandler = (id) => {
    messageHandlers.delete(id)
  }

  return {
    isConnected,
    connect,
    disconnect,
    addMessageHandler,
    removeMessageHandler
  }
}
