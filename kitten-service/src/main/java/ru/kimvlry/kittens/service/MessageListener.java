package ru.kimvlry.kittens.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import ru.kimvlry.kittens.common.MessageRequest;
import ru.kimvlry.kittens.dto.KittenDto;

@Component
public class MessageListener {
    private final KittenService kittenService;
    private final ObjectMapper objectMapper;

    public MessageListener(KittenService kittenService, ObjectMapper objectMapper) {
        this.kittenService = kittenService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.KITTENS_REQUEST_QUEUE)
    public Object handle(MessageRequest message) {
        try {
            return switch (message.getAction()) {
                case "GET_KITTEN" -> {
                    Long id = objectMapper.convertValue(message.getPayload(), Long.class);
                    yield kittenService.getKittenById(id);
                }
                case "SEARCH_KITTENS" -> {
                    KittenFilter filter = objectMapper.convertValue(message.getPayload(), KittenFilter.class);
                    PageRequest pageRequest = PageRequest.of(0, 10);
                    yield kittenService.getKittensFiltered(filter, pageRequest);
                }
                case "CREATE_KITTEN" -> {
                    KittenDto request = objectMapper.convertValue(message.getPayload(), KittenDto.class);
                    yield kittenService.createKitten(request);
                }
                case "UPDATE_KITTEN" -> {
                    KittenDto request = objectMapper.convertValue(message.getPayload(), KittenDto.class);
                    yield kittenService.updateKitten(request.id(), request);
                }
                case "DELETE_KITTEN" -> {
                    Long id = objectMapper.convertValue(message.getPayload(), Long.class);
                    kittenService.deleteKitten(id);
                    yield null;
                }
                default -> throw new IllegalArgumentException("Unexpected action: " + message.getAction());
            };
        }
        catch (EntityNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to process action " + message.getAction(), e);
        }
    }
}
