package ru.kimvlry.kittens.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.dto.KittenDto;

@Component
@RabbitListener(queues = "kitten.queue")
@RequiredArgsConstructor
public class MessageListener {

    private final KittenService kittenService;

    @RabbitHandler
    public void handleCreate(@Payload KittenDto dto, @Header("action") String action) {
        if (!"CREATE".equals(action)) return;
        kittenService.createKitten(dto);
    }

    @RabbitHandler
    public void handleUpdate(@Payload KittenDto dto, @Header("action") String action) {
        if (!"UPDATE".equals(action)) return;
        kittenService.updateKitten(dto.id(), dto);
    }

    @RabbitHandler
    public void handleDelete(@Payload KittenDto dto, @Header("action") String action) {
        if (!"DELETE".equals(action)) return;
        kittenService.deleteKitten(dto.id());
    }
}
