package ru.kimvlry.kittens.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.common.MessageRequest;
import ru.kimvlry.kittens.dto.OwnerDto;

@Component
public class MessageListener {
    private final OwnerService ownerService;
    private final ObjectMapper objectMapper;

    public MessageListener(OwnerService ownerService, ObjectMapper objectMapper) {
        this.ownerService = ownerService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.OWNERS_REQUEST_QUEUE)
    public Object processMessage(MessageRequest message) {
        try {
            return switch (message.getAction()) {
                case "CREATE_OWNER" -> {
                    OwnerDto request = objectMapper.convertValue(message.getPayload(), OwnerDto.class);
                    yield ownerService.createOwner(request);
                }
                case "GET_OWNER" -> {
                    Long id = objectMapper.convertValue(message.getPayload(), Long.class);
                    yield ownerService.getOwnerById(id);
                }
                case "UPDATE_OWNER" -> {
                    OwnerDto request = objectMapper.convertValue(message.getPayload(), OwnerDto.class);
                    yield ownerService.updateOwner(request.id(), request);
                }
                case "DELETE_OWNER" -> {
                    Long id = objectMapper.convertValue(message.getPayload(), Long.class);
                    ownerService.deleteOwner(id);
                    yield null;
                }
                case "SEARCH_OWNERS" -> {
                    OwnerFilter filter = objectMapper.convertValue(message.getPayload(), OwnerFilter.class);
                    PageRequest pageRequest = PageRequest.of(0, 10);
                    yield ownerService.getOwnersFiltered(filter, pageRequest);
                }
                default -> throw new IllegalArgumentException("Unknown action: " + message.getAction());
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
