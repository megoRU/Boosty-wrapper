package org.boosty.entity.api.request;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CreateDialogRequest extends APIRequest {

    private final long userId;

    public CreateDialogRequest(@NotNull String url, long userId) {
        super(String.format("%s/v1/dialog/", url), RequestMethod.POST);
        this.userId = userId;
    }

    @Override
    public boolean isFormEncodedCreateDialog() {
        return true;
    }

    @Override
    public Map<String, String> getFormData() {
        return Map.of("user_id", String.valueOf(userId));
    }
}