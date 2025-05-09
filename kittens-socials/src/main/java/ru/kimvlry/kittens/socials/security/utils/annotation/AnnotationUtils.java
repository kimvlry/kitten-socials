package ru.kimvlry.kittens.socials.security.utils.annotation;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.socials.entities.Kitten;
import ru.kimvlry.kittens.socials.repository.KittenRepository;
import ru.kimvlry.kittens.socials.repository.security.UserOwnerMappingRepository;
import ru.kimvlry.kittens.socials.repository.security.UserRepository;
import ru.kimvlry.kittens.socials.entities.security.User;

@Service
public class AnnotationUtils {

    private final KittenRepository kittenRepository;
    private final UserOwnerMappingRepository userOwnerMappingRepository;
    private final UserRepository userRepository;

    public AnnotationUtils(KittenRepository kittenRepository,
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

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("No authentication");
        }

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOwnerOrAdmin(String username, Long ownerId) {
        return isAdmin() || isOwner(username, ownerId);
    }
}