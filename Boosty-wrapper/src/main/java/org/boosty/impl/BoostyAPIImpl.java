package org.boosty.impl;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.boosty.entity.Subscriber;
import org.boosty.entity.TokenPair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BoostyAPIImpl implements BoostyAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoostyAPIImpl.class);

    private static final String API_URL = "https://api.boosty.to";

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static final MediaType MEDIA_TYPE_FORM =
            MediaType.get("application/x-www-form-urlencoded");

    private final ObjectMapper objectMapper;
    private final String deviceId;

    private String accessToken;
    private String refreshToken;

    protected BoostyAPIImpl(
            String accessToken,
            String refreshToken,
            String deviceId
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Subscriber> getSubscribers(
            String blogName,
            int limit
    ) throws IOException {

        ensureAccessToken();

        String url = API_URL
                + "/v1/blog/"
                + URLEncoder.encode(blogName, StandardCharsets.UTF_8)
                + "/subscribers"
                + "?sort_by=on_time"
                + "&limit=" + limit
                + "&order=gt";

        Response response = sendGet(url);

        if (response.code() == 401) {
            response.close();

            refreshTokens();

            response = sendGet(url);
        }

        try (Response finalResponse = response) {
            if (!finalResponse.isSuccessful()) {
                String responseBody = finalResponse.body() != null
                        ? finalResponse.body().string()
                        : "";

                throw new IOException(
                        "Boosty API returned "
                                + finalResponse.code()
                                + ": "
                                + responseBody
                );
            }

            String responseBody = finalResponse.body() != null
                    ? finalResponse.body().string()
                    : "";

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode data = root.path("data");

            List<Subscriber> subscribers = new ArrayList<>();

            for (JsonNode user : data) {
                JsonNode level = user.path("level");

                subscribers.add(new Subscriber(
                        user.path("id").asLong(),
                        user.path("name").asString(),
                        user.path("avatarUrl").asString(null),
                        user.path("onTime").asLong(),
                        user.path("status").asString(),
                        user.path("payments").asInt(),
                        user.path("price").asInt(),
                        user.path("subscribed").asBoolean(),
                        level.path("id").asLong(),
                        level.path("name").asString(null),
                        level.path("price").asInt()
                ));
            }

            return subscribers;
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

            String responseBody = response.body() != null
                    ? response.body().string()
                    : "";

            if (!response.isSuccessful()) {
                LOGGER.error(
                        "Boosty token refresh failed. status={} durationMs={} body={}",
                        response.code(),
                        duration,
                        responseBody
                );

                throw new IOException(
                        "Unable to refresh Boosty token: "
                                + response.code()
                                + ": "
                                + responseBody
                );
            }

            JsonNode json = objectMapper.readTree(responseBody);

            String newAccessToken =
                    json.path("access_token").asString(null);

            String newRefreshToken =
                    json.path("refresh_token").asString(null);

            long expiresIn =
                    json.path("expires_in").asLong(0);

            if (newAccessToken == null || newAccessToken.isBlank()) {
                throw new IOException(
                        "Boosty did not return access_token: "
                                + responseBody
                );
            }

            accessToken = newAccessToken;

            /*
             * Boosty может ротировать refresh token.
             * В таком случае старый refresh token больше использовать нельзя.
             */
            if (newRefreshToken != null && !newRefreshToken.isBlank()) {
                refreshToken = newRefreshToken;
            }

            LOGGER.debug(
                    "Boosty token refreshed. expiresIn={} durationMs={}",
                    expiresIn,
                    duration
            );

            return new TokenPair(
                    accessToken,
                    refreshToken,
                    expiresIn
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
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
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