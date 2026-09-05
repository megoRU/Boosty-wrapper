package org.boosty.entity.api.response;

import lombok.Data;
import org.boosty.entity.dialog.DialogRelation;
import org.boosty.entity.dialog.Dialogs;
import org.boosty.entity.messages.Messages;

@Data
public class DialogWithUserResponse implements ApiResponse {

    private Long id;
    private Long createdAt;
    private Integer unreadMsgCount;
    private Dialogs.Chatmate chatmate;
    private DialogRelation relation;
    private Messages messages;
    private String signedQuery;
    private String wsChannel;
}