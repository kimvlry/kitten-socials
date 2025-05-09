package ru.kimvlry.kittens.socials.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.socials.security.utils.user.UserOwnerMapping;

import java.util.Optional;

public interface UserOwnerMappingRepository extends JpaRepository<UserOwnerMapping, Long> {
    Optional<UserOwnerMapping> findByUserId(Long userId);
    Optional<UserOwnerMapping> findByUserIdAndOwnerId(Long userId, Long ownerId);
}