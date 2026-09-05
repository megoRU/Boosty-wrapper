package org.boosty.entity.dialog;

import lombok.Data;
import org.boosty.entity.messages.Message;
import org.boosty.entity.api.response.ApiResponse;

@Data
public class Dialog implements ApiResponse {

    private Long id;
    private Long createdAt;
    private Integer unreadMsgCount;
    private Message lastMessage;
    private Dialogs.Chatmate chatmate;
    private DialogRelation relation;
}