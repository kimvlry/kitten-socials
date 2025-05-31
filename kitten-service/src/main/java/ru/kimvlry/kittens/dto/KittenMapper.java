package ru.kimvlry.kittens.dto;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kimvlry.kittens.entities.Kitten;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface KittenMapper {
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "friends", target = "friendIds")
    @Mapping(source = "birthDate", target = "birthDate")
    KittenDto toDto(Kitten kitten);

    default Set<Long> mapFriendsToIds(Set<Kitten> friends) {
        return friends == null ? null :
                friends.stream().map(Kitten::getId).collect(Collectors.toSet());
    }
}
