package ru.kimvlry.kittens.socials.security.utils.annotation;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.socials.dto.KittenDto;
import ru.kimvlry.kittens.socials.entities.Kitten;
import ru.kimvlry.kittens.socials.entities.Owner;
import ru.kimvlry.kittens.socials.repository.KittenRepository;
import ru.kimvlry.kittens.socials.repository.UserOwnerMappingRepository;
import ru.kimvlry.kittens.socials.repository.UserRepository;
import ru.kimvlry.kittens.socials.entities.User;
import ru.kimvlry.kittens.socials.security.utils.user.UserOwnerMapping;

@Service
public class ValidationUtils {

    private final KittenRepository kittenRepository;
    private final UserOwnerMappingRepository userOwnerMappingRepository;
    private final UserRepository userRepository;

    public ValidationUtils(KittenRepository kittenRepository,
                           UserOwnerMappingRepository userOwnerMappingRepository,
                           UserRepository userRepository
    ) {
        this.kittenRepository = kittenRepository;
        this.userOwnerMappingRepository = userOwnerMappingRepository;
        this.userRepository = userRepository;
    }

    public boolean isKittenOwner(String ownerUsername, Long id) {
        User user = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + ownerUsername));
        Kitten kitten = kittenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kitten not found with id: " + id));

        return userOwnerMappingRepository
                .findByUserIdAndOwnerId(user.getId(), kitten.getOwner().getId())
                .isPresent();
    }

    public boolean isOwner(String username, Long ownerId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return userOwnerMappingRepository
                .findByUserIdAndOwnerId(user.getId(), ownerId)
                .isPresent();
    }

    public void validateOwnerAssignment(KittenDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("No authenticated user found");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Owner currentOwner = userOwnerMappingRepository.findByUserId(user.getId())
                .map(UserOwnerMapping::getOwner)
                .orElseThrow(() -> new IllegalArgumentException("No Owner associated with user: " + username));

        if (!currentOwner.getId().equals(dto.ownerId())) {
            throw new AccessDeniedException("You can't assign kitten to another owner.");
        }
    }
}