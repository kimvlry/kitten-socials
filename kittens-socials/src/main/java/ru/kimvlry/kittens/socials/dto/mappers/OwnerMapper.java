package ru.kimvlry.kittens.socials.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kimvlry.kittens.socials.entities.Kitten;
import ru.kimvlry.kittens.socials.entities.Owner;
import ru.kimvlry.kittens.socials.dto.OwnerDto;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    @Mapping(source = "ownedKittens", target = "ownedKittensIds")
    @Mapping(source = "birthDate", target = "birthDate")
    OwnerDto toDto(Owner owner);

    default Set<Long> mapKittensToIds(Set<Kitten> ownedKittens) {
        return ownedKittens == null ? null :
                ownedKittens.stream().map(Kitten::getId).collect(Collectors.toSet());
    }
}
