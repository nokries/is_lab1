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
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    BookCreature toEntity(BookCreatureDTO dto);
    BookCreatureDTO toDto(BookCreature entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creatureType", expression = "java(BookCreatureType.getType(dto.creatureType()))")
    @Mapping(target = "creatureLocation", qualifiedByName = "toEntity")
    @Mapping(target = "ring", qualifiedByName = "toEntity")
    void updateEntityFromDto(BookCreatureDTO dto, @MappingTarget BookCreature entity);
}
