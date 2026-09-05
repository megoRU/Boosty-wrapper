package org.boosty.entity.messages;

import lombok.Data;
import org.boosty.entity.dialog.Dialogs;

import java.util.List;
import java.util.Map;

@Data
public class Message {

    private Long id;
    private Long dialogId;
    private Long authorId;
    private Long createdAt;
    private Integer price;
    private boolean isDeleted;
    private boolean isRead;
    private boolean isPaid;
    private boolean isFeePaid;
    private String previewType;
    private List<MessageData> data;
    private Dialogs.Attachments attachments;
    private Map<String, Double> currencyPrices;
    private Dialogs.Donation donation;
    private List<Object> teaser;
}