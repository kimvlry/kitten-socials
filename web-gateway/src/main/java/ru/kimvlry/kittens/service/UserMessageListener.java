package ru.kimvlry.kittens.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.common.CreateOwnerForUserDto;
import ru.kimvlry.kittens.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserMessageListener {
    private final UserRepository userRepository;

    @RabbitListener(queues = "user.queue")
    public void handleSetOwnerId(CreateOwnerForUserDto dto) {
        userRepository.findById(dto.userId()).ifPresent(user -> {
            user.setOwnerId(dto.userId());
            userRepository.save(user);
        });
    }
}

