package ru.kimvlry.kittens.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String action;
    private Object payload;

    public MessageRequest(String action, Object payload) {
        this.action = action;
        this.payload = payload;
    }
}
