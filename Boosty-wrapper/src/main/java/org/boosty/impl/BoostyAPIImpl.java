package org.boosty.impl;

import okhttp3.*;
import org.boosty.entity.Subscriber;
import org.boosty.entity.TokenPair;
import org.boosty.entity.api.request.APIRequest;
import org.boosty.entity.api.request.SubscriberRequest;
import org.boosty.entity.api.response.ApiResponse;
import org.boosty.entity.api.response.SubscriberResponse;
import org.boosty.entity.exceptions.ThreeUIException;
import org.boosty.entity.exceptions.UnsuccessfulHttpException;
import org.boosty.utils.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BoostyAPIImpl implements BoostyAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoostyAPIImpl.class);
    private static final String API_URL = "https://api.boosty.to";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json");
    private static final MediaType MEDIA_TYPE_FORM = MediaType.get("application/x-www-form-urlencoded");

    private final ObjectMapper objectMapper;
    private final String deviceId;

    private String accessToken;
    private String refreshToken;

    protected BoostyAPIImpl(String accessToken, String refreshToken, String deviceId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Subscriber> getSubscribers(String blogName, int limit) throws IOException, InterruptedException, ThreeUIException, UnsuccessfulHttpException {
        return parseResponse(SubscriberResponse.class, new SubscriberRequest(API_URL, blogName, limit)).getData();
    }

    private <T extends ApiResponse> T parseResponse(Class<T> tClass, @NotNull APIRequest apiRequest) throws IOException, UnsuccessfulHttpException {
        String url = apiRequest.getUrl();
        APIRequest.RequestMethod method = apiRequest.getRequestMethod();

        String payload = apiRequest.getData() != null ? apiRequest.getData().toJson() : "{}";
        LOGGER.debug("API request start. method={} url={} payload={}", method, url, payload);

        Request.Builder requestBuilder = new Request.Builder().url(url).addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + accessToken);

        if (method == APIRequest.RequestMethod.GET) {
            requestBuilder.get();
        } else if (method == APIRequest.RequestMethod.POST) {
            requestBuilder.post(RequestBody.create(payload, MEDIA_TYPE_JSON));
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
    public TokenPair refreshTokens() throws IOException {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IOException("Refresh token is empty");
        }

        if (deviceId == null || deviceId.isBlank()) {
            throw new IOException("Device ID is empty");
        }

        String body = "device_id="
                + encode(deviceId)
                + "&device_os=web"
                + "&grant_type=refresh_token"
                + "&refresh_token="
                + encode(refreshToken);

        Request request = new Request.Builder()
                .url(API_URL + "/oauth/token/")
                .post(RequestBody.create(body, MEDIA_TYPE_FORM))
                .addHeader("Accept", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("DNT", "1")
                .addHeader("User-Agent", getUserAgent())
                .build();

        long start = System.currentTimeMillis();

        try (Response response = CLIENT.newCall(request).execute()) {
            long duration = System.currentTimeMillis() - start;

            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                LOGGER.error(
                        "Boosty token refresh failed. status={} durationMs={} body={}",
                        response.code(),
                        duration,
                        responseBody
                );

                throw new IOException("Unable to refresh Boosty token: " + response.code() + ": " + responseBody
                );
            }

            JsonNode json = objectMapper.readTree(responseBody);

            String newAccessToken = json.path("access_token").asString(null);

            String newRefreshToken = json.path("refresh_token").asString(null);

            long expiresIn = json.path("expires_in").asLong(0);

            if (newAccessToken == null || newAccessToken.isBlank()) {
                throw new IOException("Boosty did not return access_token: " + responseBody);
            }

            accessToken = newAccessToken;

            /*
             * Boosty может ротировать refresh token.
             * В таком случае старый refresh token больше использовать нельзя.
             */
            if (newRefreshToken != null && !newRefreshToken.isBlank()) {
                refreshToken = newRefreshToken;
            }

            LOGGER.debug("Boosty token refreshed. expiresIn={} durationMs={}", expiresIn, duration);

            return new TokenPair(accessToken, refreshToken, expiresIn
            );
        }
    }

    private void ensureAccessToken() throws IOException {
        if (accessToken == null || accessToken.isBlank()) {
            refreshTokens();
        }
    }

    @NotNull
    private Response sendGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Accept", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("DNT", "1")
                .addHeader("User-Agent", getUserAgent())
                .build();

        return CLIENT.newCall(request).execute();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String getUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                + "AppleWebKit/537.36 (KHTML, like Gecko) "
                + "Chrome/140.0.0.0 Safari/537.36";
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }
}