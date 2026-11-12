package org.example.is_lab1.models.dto;

import jakarta.validation.constraints.Max;

public record CoordinatesDTO(@Max(value = 735,
        message = "Координата x не может быть больше 735") int x, int y) {
}
