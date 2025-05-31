package ru.kimvlry.kittens.dto;

import org.mapstruct.Mapper;
import ru.kimvlry.kittens.entities.Owner;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    OwnerDto toDto(Owner owner);
}
