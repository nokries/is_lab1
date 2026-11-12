package org.example.is_lab1.models.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime value) {
        if (value == null) {
            return null;
        }
        else {
            return Timestamp.from(value.toInstant());
        }
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp value) {
        if (value == null) {
            return null;
        } else {
            return ZonedDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault());
        }
    }
}