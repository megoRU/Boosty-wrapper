package org.boosty.entity.dialog;


import lombok.Data;
import org.boosty.entity.messages.Message;
import org.boosty.entity.api.response.ApiResponse;

import java.util.List;

@Data
public class DialogWithUser implements ApiResponse {

    private Long id;
    private Long createdAt;
    private Integer unreadMsgCount;
    private Messages messages;
    private Dialogs.Chatmate chatmate;
    private DialogRelation relation;

    @Data
    public static class Messages implements ApiResponse {

        private List<Message> data;
        private Extra extra;
    }

    @Data
    public static class Extra {

        private boolean isFirst;
        private boolean isLast;
    }
}