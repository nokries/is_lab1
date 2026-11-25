package org.example.is_lab1.models.entity;

public enum BookCreatureType {
    HOBBIT,
    ELF,
    HUMAN,
    GOLLUM;

    public static BookCreatureType getType(String name){
        if (name == null || name.isBlank()) {
            return null;
        }

        try {
            return BookCreatureType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown creature type: " + name);
        }
    }
}
