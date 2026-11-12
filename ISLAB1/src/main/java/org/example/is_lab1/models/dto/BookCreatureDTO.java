package org.example.is_lab1.models.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record BookCreatureDTO(long id,
                              @NotBlank(message = "Имя не может быть пустым") String name,
                              @NotNull(message = "Координаты обязательны") @Valid CoordinatesDTO coordinates,
                              java.util.Date creationDate,
                              @Positive(message = "Возраст должен быть больше 0") Long age,
                              String creatureType,
                              MagicCityDTO creatureLocation,
                              @Positive(message = "Уровень атаки должен быть больше 0") long attackLevel,
                              @NotNull(message = "Кольцо обязательно") RingDTO ring) {}
