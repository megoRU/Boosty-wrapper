package org.boosty.entity.api.request;

import lombok.Getter;
import org.boosty.impl.APIRequestData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MessageRequest extends APIRequest {

    public MessageRequest(@NotNull String url, int dialogId, @NotNull String message) {
        super(String.format("%s/v1/dialog/%s/message", url, dialogId), RequestMethod.POST, new MessageData(message));
    }

    @Override
    public boolean isFormEncoded() {
        return true;
    }

    @Getter
    @SuppressWarnings("ClassCanBeRecord")
    private static class MessageData implements APIRequestData {

        @SuppressWarnings("FieldCanBeLocal")
        private final List<Map<String, String>> data;

        private MessageData(String message) {
            this.data = List.of(Map.of(
                    "type", "text",
                    "content", "[\"" + message + "\",\"unstyled\",[]]",
                    "modificator", ""
            ));
        }
    }
}
