package org.boosty.impl;

import org.boosty.entity.Subscriber;
import org.boosty.entity.TokenPair;
import org.boosty.entity.exceptions.UnsuccessfulHttpException;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Интерфейс для взаимодействия с Boosty API
 */
public interface BoostyAPI {

    List<Subscriber> getSubscribers(String blogName, int limit) throws IOException, UnsuccessfulHttpException;

    TokenPair refreshTokens() throws IOException, UnsuccessfulHttpException;

    String getAccessToken();

    String getRefreshToken();

    long getAccessTokenExpiresAt();

    String getUserAgent();

    class Builder {

        private String accessToken;
        private String refreshToken;
        private String deviceId;
        private String userAgent;
        private long expiresAt;

        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        @Contract("null -> fail")
        public Builder setRefreshToken(String refreshToken) {
            this.refreshToken = Objects.requireNonNull(refreshToken);
            return this;
        }

        @Contract("null -> fail")
        public Builder setDeviceId(String deviceId) {
            this.deviceId = Objects.requireNonNull(deviceId);
            return this;
        }

        public Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder setExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder setAccessTokenExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public BoostyAPI build() {
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("refreshToken cannot be null or blank!");
            }

            if (deviceId == null || deviceId.isBlank()) {
                throw new IllegalArgumentException("deviceId cannot be null or blank!");
            }

            return new BoostyAPIImpl(
                    accessToken,
                    refreshToken,
                    deviceId,
                    userAgent,
                    expiresAt
            );
        }
    }
}
