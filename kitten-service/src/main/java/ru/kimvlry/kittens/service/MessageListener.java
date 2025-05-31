package ru.kimvlry.kittens.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.dto.KittenDto;

@Component
@RequiredArgsConstructor
public class MessageListener {
    private final KittenService kittenService;

    @RabbitListener(queues = "kitten.create.queue")
    public void handleCreate(@Payload KittenDto dto) {
        kittenService.createKitten(dto);
    }

    @RabbitListener(queues = "kitten.update.queue")
    public void handleUpdate(@Payload KittenDto dto) {
        kittenService.updateKitten(dto.id(), dto);
    }

    @RabbitListener(queues = "kitten.delete.queue")
    public void handleDelete(@Payload KittenDto dto) {
        kittenService.deleteKitten(dto.id());
    }
}
