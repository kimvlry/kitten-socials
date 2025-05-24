package ru.kimvlry.kittens.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.common.MessageRequest;

import java.util.List;
import java.util.Map;

@Component
public class MessageClient {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MessageClient(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> T sendRequest(String queue, String action, Object payload, Class<T> responseType) {
        Object response = rabbitTemplate.convertSendAndReceive(queue, new MessageRequest(action, payload));
        if (response == null) {
            throw new IllegalStateException("No response from service for action: " + action);
        }
        return objectMapper.convertValue(response, responseType);
    }

    public <T> Page<T> sendPageRequest(String queue, String action, Object payload, Class<T> contentType) {
        Object response = rabbitTemplate.convertSendAndReceive(queue, new MessageRequest(action, payload));
        if (response == null) {
            throw new IllegalStateException("No response from service for action: " + action);
        }
        Map<String, Object> pageMap = objectMapper.convertValue(response, Map.class);
        List<T> content = objectMapper.convertValue(
                pageMap.get("content"),
                TypeFactory.defaultInstance().constructCollectionType(List.class, contentType));
        long total = ((Number) pageMap.get("totalElements")).longValue();
        int page = ((Number) pageMap.get("number")).intValue();
        int size = ((Number) pageMap.get("size")).intValue();
        return new PageImpl<>(content, Pageable.ofSize(size).withPage(page), total);
    }

    public void sendVoidRequest(String queue, String action, Object payload) {
        rabbitTemplate.convertAndSend(queue, new MessageRequest(action, payload));
    }
}
