package ru.kimvlry.kittens.web.security.utils.annotation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.web.entities.Kitten;
import ru.kimvlry.kittens.web.repository.KittenRepository;
import ru.kimvlry.kittens.web.repository.security.UserOwnerMappingRepository;
import ru.kimvlry.kittens.web.repository.security.UserRepository;
import ru.kimvlry.kittens.web.entities.security.User;

@Service
public class SecurityAnnotationUtils {

    private final KittenRepository kittenRepository;
    private final UserOwnerMappingRepository userOwnerMappingRepository;
    private final UserRepository userRepository;

    public SecurityAnnotationUtils(KittenRepository kittenRepository,
                                   UserOwnerMappingRepository userOwnerMappingRepository,
                                   UserRepository userRepository
    ) {
        this.kittenRepository = kittenRepository;
        this.userOwnerMappingRepository = userOwnerMappingRepository;
        this.userRepository = userRepository;
    }

    public boolean isKittenOwner(String username, Long kittenId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Kitten kitten = kittenRepository.findById(kittenId)
                .orElseThrow(() -> new IllegalArgumentException("Kitten not found with id: " + kittenId));

        return userOwnerMappingRepository
                .findByUserIdAndOwnerId(user.getId(), kitten.getOwner().getId())
                .isPresent();
    }

    public boolean isOwnerOrAdmin(String username, Long ownerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return userOwnerMappingRepository
                .findByUserIdAndOwnerId(user.getId(), ownerId)
                .isPresent();
    }
}