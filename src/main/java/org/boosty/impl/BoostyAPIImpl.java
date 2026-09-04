package org.boosty.impl;

import okhttp3.*;
import org.boosty.entity.Subscriber;
import org.boosty.entity.TokenPair;
import org.boosty.entity.api.request.APIRequest;
import org.boosty.entity.api.request.RefreshTokenRequest;
import org.boosty.entity.api.request.SubscriberRequest;
import org.boosty.entity.api.response.ApiResponse;
import org.boosty.entity.api.response.RefreshTokenResponse;
import org.boosty.entity.api.response.SubscriberResponse;
import org.boosty.entity.exceptions.UnsuccessfulHttpException;
import org.boosty.utils.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class BoostyAPIImpl implements BoostyAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoostyAPIImpl.class);
    private static final String API_URL = "https://api.boosty.to";
    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/140.0.0.0 Safari/537.36";

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private final String deviceId;
    private final String userAgent;

    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresAt;

    protected BoostyAPIImpl(String accessToken, String refreshToken, String deviceId, String userAgent, long expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
        this.userAgent = (userAgent != null && !userAgent.isBlank()) ? userAgent : DEFAULT_USER_AGENT;
        this.accessTokenExpiresAt = expiresAt;
    }

    @Override
    public List<Subscriber> getSubscribers(String blogName, int limit) throws IOException, UnsuccessfulHttpException {
        return parseResponse(SubscriberResponse.class, new SubscriberRequest(API_URL, blogName, limit)).getData();
    }

    private <T extends ApiResponse> T parseResponse(Class<T> tClass, @NotNull APIRequest apiRequest) throws IOException, UnsuccessfulHttpException {
        String url = apiRequest.getUrl();
        APIRequest.RequestMethod method = apiRequest.getRequestMethod();

        String payload = apiRequest.getData() != null ? apiRequest.getData().toJson() : "{}";
        LOGGER.debug("API request start. method={} url={} payload={}", method, url, payload);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("DNT", "1")
                .addHeader("User-Agent", userAgent);

        if (accessToken != null && !accessToken.isBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer " + accessToken);
        }

        if (method == APIRequest.RequestMethod.GET) {
            requestBuilder.get();
        } else if (method == APIRequest.RequestMethod.POST) {
            requestBuilder.post(RequestBody.create(payload, apiRequest.getMediaType()));
        }

        Request request = requestBuilder.build();
        long start = System.currentTimeMillis();

        try (Response response = CLIENT.newCall(request).execute()) {
            long duration = System.currentTimeMillis() - start;
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                LOGGER.error("API request failed. method={} url={} status={} message={} durationMs={} body={}", method, url, response.code(), response.message(), duration, responseBody);
                throw new UnsuccessfulHttpException(response.code(), response.message());
            }

            LOGGER.debug("API request success. method={} url={} status={} durationMs={}", method, url, response.code(), duration);
            LOGGER.debug("API response body. url={} body={}", url, responseBody);

            return JsonUtil.fromJson(responseBody, tClass);
        }
    }

    @Override
    public TokenPair refreshTokens() throws IOException, UnsuccessfulHttpException {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IOException("Refresh token is empty");
        }

        if (deviceId == null || deviceId.isBlank()) {
            throw new IOException("Device ID is empty");
        }

        RefreshTokenResponse response = parseResponse(
                RefreshTokenResponse.class,
                new RefreshTokenRequest(API_URL, deviceId, refreshToken)
        );

        if (response == null || response.getAccessToken() == null || response.getAccessToken().isBlank()) {
            throw new IOException("Boosty did not return access_token");
        }

        this.accessToken = response.getAccessToken();

        /*
         * Boosty может ротировать refresh token.
         * В таком случае старый refresh token больше использовать нельзя.
         */
        if (response.getRefreshToken() != null && !response.getRefreshToken().isBlank()) {
            this.refreshToken = response.getRefreshToken();
        }

        long expiresIn = response.getExpiresIn();
        this.accessTokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L);

        LOGGER.debug("Boosty token refreshed. expiresIn={} expiresAt={}", expiresIn, accessTokenExpiresAt);

        return new TokenPair(this.accessToken, this.refreshToken, this.accessTokenExpiresAt);
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public long getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }
}
