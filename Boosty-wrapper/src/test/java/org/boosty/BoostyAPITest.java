package org.boosty;

import org.boosty.entity.TokenPair;
import org.boosty.entity.api.request.RefreshTokenData;
import org.boosty.entity.api.request.RefreshTokenRequest;
import org.boosty.entity.api.response.RefreshTokenResponse;
import org.boosty.impl.BoostyAPI;
import org.boosty.impl.BoostyAPIImpl;
import org.boosty.utils.JsonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoostyAPITest {

    @Test
    void testBuilderValidation() {
        // Missing refreshToken
        assertThrows(IllegalArgumentException.class, () ->
                new BoostyAPI.Builder().setDeviceId("device123").build()
        );

        // Blank refreshToken
        assertThrows(IllegalArgumentException.class, () ->
                new BoostyAPI.Builder().setDeviceId("device123").setRefreshToken("   ").build()
        );

        // Missing deviceId
        assertThrows(IllegalArgumentException.class, () ->
                new BoostyAPI.Builder().setRefreshToken("token123").build()
        );

        // Blank deviceId
        assertThrows(IllegalArgumentException.class, () ->
                new BoostyAPI.Builder().setRefreshToken("token123").setDeviceId("").build()
        );
    }

    @Test
    void testBuilderSuccessfulInitializationAndDefaults() {
        long expiresAt = System.currentTimeMillis() + 3600000;

        BoostyAPI api = new BoostyAPI.Builder()
                .setRefreshToken("ref123")
                .setDeviceId("dev123")
                .setAccessToken("acc123")
                .setExpiresAt(expiresAt)
                .build();

        assertEquals("acc123", api.getAccessToken());
        assertEquals("ref123", api.getRefreshToken());
        assertEquals(expiresAt, api.getAccessTokenExpiresAt());
        assertEquals(BoostyAPIImpl.DEFAULT_USER_AGENT, api.getUserAgent());
    }

    @Test
    void testCustomUserAgentAndExpiresAtAlias() {
        long expiresAt = System.currentTimeMillis() + 7200000;
        String customUA = "CustomUserAgent/1.0";

        BoostyAPI api = new BoostyAPI.Builder()
                .setRefreshToken("ref123")
                .setDeviceId("dev123")
                .setUserAgent(customUA)
                .setAccessTokenExpiresAt(expiresAt)
                .build();

        assertEquals(customUA, api.getUserAgent());
        assertEquals(expiresAt, api.getAccessTokenExpiresAt());
    }

    @Test
    void testRefreshTokenDataFormEncoding() {
        RefreshTokenData data = new RefreshTokenData("dev id", "ref/token+123");
        String payload = data.toJson();

        assertTrue(payload.contains("device_id=dev+id"));
        assertTrue(payload.contains("device_os=web"));
        assertTrue(payload.contains("grant_type=refresh_token"));
        assertTrue(payload.contains("refresh_token=ref%2Ftoken%2B123"));
    }

    @Test
    void testRefreshTokenResponseDeserialization() {
        String jsonResponse = "{\n" +
                "  \"access_token\": \"new_acc_token\",\n" +
                "  \"refresh_token\": \"new_ref_token\",\n" +
                "  \"expires_in\": 86400,\n" +
                "  \"token_type\": \"Bearer\"\n" +
                "}";

        RefreshTokenResponse response = JsonUtil.fromJson(jsonResponse, RefreshTokenResponse.class);

        assertNotNull(response);
        assertEquals("new_acc_token", response.getAccessToken());
        assertEquals("new_ref_token", response.getRefreshToken());
        assertEquals(86400, response.getExpiresIn());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void testRefreshTokenRequest() {
        RefreshTokenRequest request = new RefreshTokenRequest("https://api.boosty.to", "dev123", "ref123");

        assertEquals("https://api.boosty.to/oauth/token/", request.getUrl());
        assertEquals("application/x-www-form-urlencoded", request.getMediaType().toString());
    }

    @Test
    void testTokenPair() {
        TokenPair pair = new TokenPair("acc", "ref", 123456789L);
        assertEquals("acc", pair.getAccessToken());
        assertEquals("ref", pair.getRefreshToken());
        assertEquals(123456789L, pair.getExpiresAt());
    }
}
