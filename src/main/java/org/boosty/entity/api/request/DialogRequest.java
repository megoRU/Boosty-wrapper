package org.boosty.entity.api.request;

import org.jetbrains.annotations.NotNull;

public class DialogRequest extends APIRequest {

    public DialogRequest(@NotNull String url) {
        super(String.format("%s/v1/dialog/", url), RequestMethod.GET);
    }
}
