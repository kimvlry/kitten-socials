package ru.kimvlry.kittens.socials.dto.mappers;

import ru.kimvlry.kittens.socials.entities.Kitten;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kimvlry.kittens.socials.dto.KittenDto;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface KittenMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "friends", target = "friendIds")
    @Mapping(source = "birthDate", target = "birthDate")
    KittenDto toDto(Kitten kitten);

    default Set<Long> mapFriendsToIds(Set<Kitten> friends) {
        return friends == null ? null :
                friends.stream().map(Kitten::getId).collect(Collectors.toSet());
    }
}
