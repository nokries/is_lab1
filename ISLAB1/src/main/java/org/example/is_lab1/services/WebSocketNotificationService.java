package org.example.is_lab1.services;

import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {
    public static final String CREATURE = "CREATURE";
    public static final String CITY = "CITY";
    public static final String RING = "RING";

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyCreated(String entityType, Long entityId, Object payload) {
        send(WebSocketMessage.created(entityType, entityId, payload));
    }

    public void notifyUpdated(String entityType, Long entityId, Object payload) {
        send(WebSocketMessage.updated(entityType, entityId, payload));
    }

    public void notifyDeleted(String entityType, Long entityId) {
        send(WebSocketMessage.deleted(entityType, entityId));
    }

    private void send(WebSocketMessage message) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    messagingTemplate.convertAndSend("/topic/entities", message);
                }
            });
        } else {
            messagingTemplate.convertAndSend("/topic/entities", message);
        }
    }

}
