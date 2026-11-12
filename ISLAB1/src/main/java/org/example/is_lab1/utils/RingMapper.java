package org.example.is_lab1.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.example.is_lab1.models.dto.RingDTO;
import org.example.is_lab1.models.entity.Ring;
import org.example.is_lab1.repository.RingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class RingMapper {

    @Autowired
    protected RingRepository ringRepository;

    @Autowired
    protected Validator validator;

    // Если передан только id - возвращаем ссылку, иначе создаем новый
    @Named("toEntity")
    public Ring toEntity(RingDTO dto) {
        if (dto == null) {
            return null;
        }

        // Если передан только id без остальных полей - используем ссылку на существующую сущность
        if (dto.id() != 0 && (dto.name() == null || dto.name().isBlank())) {
            return ringRepository.getReferenceById(dto.id());
        }

        // Иначе создаем новый объект через маппинг и проверяем его по аннотациям сущности
        Ring ring = mapToEntity(dto);
        Set<ConstraintViolation<Ring>> violations = validator.validate(ring);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ring;
    }

    @Mapping(target = "id", ignore = true)
    protected abstract Ring mapToEntity(RingDTO dto);

    public abstract RingDTO toDto(Ring entity);

    @Mapping(target = "id", ignore = true)
    public abstract void updateEntityFromDto(RingDTO dto, @MappingTarget Ring entity);
}
