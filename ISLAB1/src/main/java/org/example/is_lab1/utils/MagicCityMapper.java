package org.example.is_lab1.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.entity.BookCreatureType;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.repository.CityRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Mapper(componentModel = "spring", imports = BookCreatureType.class)
public abstract class MagicCityMapper {

    @Autowired
    protected CityRepository cityRepository;

    @Autowired
    protected Validator validator;

    // Если передан только id - возвращаем ссылку, иначе создаем новый
    @Named("toEntity")
    public MagicCity toEntity(MagicCityDTO dto) {
        if (dto == null) {
            return null;
        }

        // Если передан только id без остальных полей - используем ссылку на существующую сущность
        if (dto.id() != 0 && (dto.name() == null || dto.name().isBlank())) {
            return cityRepository.getReferenceById(dto.id());
        }

        // Иначе создаем новый объект через маппинг и проверяем его по аннотациям сущности
        MagicCity city = mapToEntity(dto);
        Set<ConstraintViolation<MagicCity>> violations = validator.validate(city);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return city;
    }

    @Mapping(target = "governor", expression = "java(BookCreatureType.getType(dto.governor()))")
    @Mapping(target = "id", ignore = true)
    protected abstract MagicCity mapToEntity(MagicCityDTO dto);

    public abstract MagicCityDTO toDto(MagicCity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "governor", expression = "java(BookCreatureType.getType(dto.governor()))")
    public abstract void updateEntityFromDto(MagicCityDTO dto, @MappingTarget MagicCity entity);
}
