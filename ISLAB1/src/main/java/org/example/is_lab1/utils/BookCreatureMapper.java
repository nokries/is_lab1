package org.example.is_lab1.utils;

import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.BookCreatureType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = {MagicCityMapper.class, RingMapper.class}, imports = BookCreatureType.class)
public interface BookCreatureMapper {

    @Mapping(target = "creatureType", expression = "java(BookCreatureType.getType(dto.creatureType()))")
    @Mapping(target = "creatureLocation", qualifiedByName = "toEntity")
    @Mapping(target = "ring", qualifiedByName = "toEntity")
    @Mapping(target = "creationDate", ignore = true) // генерируется автоматически (@PrePersist)
    @Mapping(target = "id", ignore = true) // id всегда генерирует БД
    BookCreature toEntity(BookCreatureDTO dto);

    BookCreatureDTO toDto(BookCreature entity);

    // Обновление существующей сущности (при modify)
    @Mapping(target = "id", ignore = true) // id не трогаем
    @Mapping(target = "creationDate", ignore = true) // дату создания не меняем
    @Mapping(target = "creatureType", expression = "java(BookCreatureType.getType(dto.creatureType()))")
    @Mapping(target = "creatureLocation", qualifiedByName = "toEntity")
    @Mapping(target = "ring", qualifiedByName = "toEntity")
    void updateEntityFromDto(BookCreatureDTO dto, @MappingTarget BookCreature entity);
}
