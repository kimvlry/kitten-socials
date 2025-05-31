package ru.kimvlry.kittens.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.common.CreateOwnerForUserDto;
import ru.kimvlry.kittens.dto.OwnerDto;

import java.util.Map;

@Component
@RabbitListener(queues = "owner.queue")
@RequiredArgsConstructor
public class MessageListener {

    private final OwnerService ownerService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void handleCreate(OwnerDto dto, @Header("action") String action) {
        if (!"CREATE".equals(action)) return;
        ownerService.createOwner(dto);
    }

    @RabbitHandler
    public void handleUpdate(OwnerDto dto, @Header("action") String action) {
        if (!"UPDATE".equals(action)) return;
        ownerService.updateOwner(dto.id(), dto);
    }

    @RabbitHandler
    public void handleDelete(OwnerDto dto, @Header("action") String action) {
        if (!"DELETE".equals(action)) return;
        ownerService.deleteOwner(dto.id());
    }

    @RabbitHandler
    public void handleCreateOwnerForUser(CreateOwnerForUserDto dto, @Header("action") String action) {
        if (!"CREATE_OWNER_FOR_USER".equals(action)) return;

        OwnerDto newOwner = ownerService.createOwner(new OwnerDto(null, dto.name(), null, null));

        rabbitTemplate.convertAndSend(
                "user.queue",
                Map.of("userId", dto.userId(), "ownerId", newOwner.id()),
                msg -> {
                    msg.getMessageProperties().setHeader("action", "SET_OWNER_ID");
                    return msg;
                }
        );
    }
}
