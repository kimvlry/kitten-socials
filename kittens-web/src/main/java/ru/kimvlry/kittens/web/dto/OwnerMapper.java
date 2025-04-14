package ru.kimvlry.kittens.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.Owner;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    @Mapping(source = "ownedKittens", target = "ownedKittensIds")
    @Mapping(source = "birthDateTime", target = "birthTimestamp")
    OwnerDto toDto(Owner owner);

    default Set<Long> mapKittensToIds(Set<Kitten> ownedKittens) {
        return ownedKittens == null ? null :
                ownedKittens.stream().map(Kitten::getId).collect(Collectors.toSet());
    }
}
