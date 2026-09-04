package org.boosty.entity.api.request;

import okhttp3.MediaType;
import org.jetbrains.annotations.NotNull;

public class RefreshTokenRequest extends APIRequest {

    private static final MediaType MEDIA_TYPE_FORM = MediaType.get("application/x-www-form-urlencoded");

    public RefreshTokenRequest(@NotNull String baseUrl, @NotNull String deviceId, @NotNull String refreshToken) {
        super(baseUrl + "/oauth/token/", RequestMethod.POST, new RefreshTokenData(deviceId, refreshToken));
    }

    @Override
    public MediaType getMediaType() {
        return MEDIA_TYPE_FORM;
    }
}
