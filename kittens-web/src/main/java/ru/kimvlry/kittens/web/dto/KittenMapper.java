package ru.kimvlry.kittens.web.dto;

import ru.kimvlry.kittens.entities.Kitten;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
interface KittenMapper {
    @Mapping(source = "owner.id", target = "ownerID")
    @Mapping(source = "friends", target = "friendIds")
    KittenDto toDto(Kitten kitten);

    default List<Long> mapFriendsToIds(List<Kitten> friends) {
        return friends == null ? null :
                friends.stream().map(Kitten::getId).collect(Collectors.toList());
    }

    Kitten toEntity(KittenDto dto);
}
