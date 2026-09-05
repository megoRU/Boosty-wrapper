package org.boosty.entity.api.request;

import org.jetbrains.annotations.NotNull;

public class DialogWithUserRequest extends APIRequest {

    public DialogWithUserRequest(@NotNull String url, long userId) {
        super(String.format("%s/v1/dialog?user_id=%d", url, userId), RequestMethod.GET);
    }
}