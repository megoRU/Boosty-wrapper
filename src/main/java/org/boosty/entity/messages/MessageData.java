package org.boosty.entity.messages;

import lombok.Data;

@Data
public class MessageData {

    private String content;
    private String modificator;
    private String type;
}