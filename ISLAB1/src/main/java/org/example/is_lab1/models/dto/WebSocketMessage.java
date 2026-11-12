package org.example.is_lab1.models.dto;


public record WebSocketMessage(
        String action,
        String entityType,
        Long entityId,
        Object payload
) {
    public static WebSocketMessage created(String entityType, Long entityId, Object payload) {
        return new WebSocketMessage("CREATED", entityType, entityId, payload);
    }

    public static WebSocketMessage updated(String entityType, Long entityId, Object payload) {
        return new WebSocketMessage("UPDATED", entityType, entityId, payload);
    }

    public static WebSocketMessage deleted(String entityType, Long entityId) {
        return new WebSocketMessage("DELETED", entityType, entityId, null);
    }
}
