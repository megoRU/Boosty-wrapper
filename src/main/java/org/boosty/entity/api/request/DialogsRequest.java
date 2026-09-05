package org.boosty.entity.api.request;

import org.jetbrains.annotations.NotNull;

public class DialogsRequest extends APIRequest {

    public DialogsRequest(@NotNull String url) {
        super(String.format("%s/v1/dialog/", url), RequestMethod.GET);
    }
}
