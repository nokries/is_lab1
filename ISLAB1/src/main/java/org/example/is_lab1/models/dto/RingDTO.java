package org.example.is_lab1.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RingDTO(long id,
                      @NotBlank(message = "Название кольца не может быть пустым") String name,
                      @NotNull(message = "Сила кольца обязательна") @Positive(message = "Сила кольца должна быть больше 0") Integer power,
                      @NotNull(message = "Вес кольца обязателен") @Positive(message = "Вес кольца должен быть больше 0") Integer weight) {}
