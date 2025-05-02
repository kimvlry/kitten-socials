package ru.kimvlry.kittens.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.web.repository.KittenRepository;
import ru.kimvlry.kittens.web.repository.UserOwnerMappingRepository;
import ru.kimvlry.kittens.web.repository.UserRepository;
import ru.kimvlry.kittens.web.security.user.User;

@Service
public class SecurityService {

    @Autowired
    private KittenRepository kittenRepository;

    @Autowired
    private UserOwnerMappingRepository userOwnerMappingRepository;

    @Autowired
    private UserRepository userRepository;

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