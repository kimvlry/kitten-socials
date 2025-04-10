package ru.kimvlry.kittens.web.dto;

import ru.kimvlry.kittens.entities.Kitten;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface KittenMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "friends", target = "friendIds")
    KittenDto toDto(Kitten kitten);

    default Set<Long> mapFriendsToIds(Set<Kitten> friends) {
        return friends == null ? null :
                friends.stream().map(Kitten::getId).collect(Collectors.toSet());
    }

    Kitten toEntity(KittenDto dto);
}
