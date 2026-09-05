package org.boosty.entity.api.response;

import lombok.Data;
import org.boosty.entity.dialog.Dialogs;
import org.boosty.entity.messages.MessageData;

import java.util.List;
import java.util.Map;

@Data
public class MessageResponse implements ApiResponse {

    private boolean isPaid;
    private boolean payWall;
    private Dialogs.Attachments attachments;
    private long authorId;
    private long createdAt;
    private Map<String, Double> currencyPrices;
    private List<MessageData> data;
    private long dialogId;
    private long id;
    private boolean isDeleted;
    private boolean isFeePaid;
    private boolean isRead;
    private String previewType;
    private int price;
    private List<Object> teaser;
}