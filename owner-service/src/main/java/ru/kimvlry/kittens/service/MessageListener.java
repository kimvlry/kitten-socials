package ru.kimvlry.kittens.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.common.CreateOwnerForUserDto;
import ru.kimvlry.kittens.dto.OwnerDto;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageListener {
    private final OwnerService ownerService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "owner.create.queue")
    public void handleCreate(OwnerDto dto) {
        ownerService.createOwner(dto);
    }

    @RabbitListener(queues = "owner.update.queue")
    public void handleUpdate(OwnerDto dto) {
        ownerService.updateOwner(dto.id(), dto);
    }

    @RabbitListener(queues = "owner.delete.queue")
    public void handleDelete(OwnerDto dto) {
        ownerService.deleteOwner(dto.id());
    }

    @RabbitListener(queues = "owner.create-for-user.queue")
    public void handleCreateOwnerForUser(CreateOwnerForUserDto dto) {
        OwnerDto newOwner = ownerService.createOwner(new OwnerDto(null, dto.name(), null, null));

        rabbitTemplate.convertAndSend(
                "",
                "user.queue",
                Map.of("userId", dto.userId(), "ownerId", newOwner.id()));
    }
}
