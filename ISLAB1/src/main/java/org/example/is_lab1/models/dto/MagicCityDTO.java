package org.example.is_lab1.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MagicCityDTO(long id,
                           @NotBlank(message = "Название города не может быть пустым") String name,
                           @NotNull(message = "Площадь города обязательна") @Positive(message = "Площадь города должна быть больше 0") Double area,
                           @NotNull(message = "Население города обязательно") @Positive(message = "Население города должно быть больше 0") Integer population,
                           java.time.ZonedDateTime establishmentDate,
                           String governor,
                           boolean capital,
                           @Positive(message = "Плотность населения должна быть больше 0") long populationDensity) {}
