package org.boosty.entity.api.request;

import lombok.Getter;
import okhttp3.MediaType;
import org.boosty.impl.APIRequestData;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class APIRequest {

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.get("application/json");

    private final String url;
    private final RequestMethod requestMethod;
    private final APIRequestData data;

    protected APIRequest(@NotNull String url, @NotNull RequestMethod requestMethod, APIRequestData data) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.data = data;
    }

    protected APIRequest(@NotNull String url, @NotNull RequestMethod method) {
        this(url, method, null);
    }

    public MediaType getMediaType() {
        return DEFAULT_MEDIA_TYPE;
    }

    public enum RequestMethod {
        GET,
        POST
    }

    public boolean isFormEncoded() {
        return false;
    }
}
