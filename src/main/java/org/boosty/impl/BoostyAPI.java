package org.boosty.impl;

import org.boosty.entity.dialog.Dialog;
import org.boosty.entity.Subscriber;
import org.boosty.entity.TokenPair;
import org.boosty.entity.api.response.DialogWithUserResponse;
import org.boosty.entity.api.response.DialogsResponse;
import org.boosty.entity.api.response.MessageResponse;
import org.boosty.entity.exceptions.UnsuccessfulHttpException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Интерфейс для взаимодействия с Boosty API
 */
public interface BoostyAPI {

    /**
     * Возвращает список подписчиков указанного блога.
     *
     * @param blogName название блога
     * @param limit максимальное количество подписчиков
     * @return список подписчиков
     * @throws IOException при возникновении ошибки ввода вывода
     * @throws UnsuccessfulHttpException если API вернуло ошибку
     */
    List<Subscriber> getSubscribers(String blogName, int limit) throws IOException, UnsuccessfulHttpException;

    /**
     * Отправляет сообщение в указанный диалог.
     *
     * @param message текст сообщения
     * @param dialogId идентификатор диалога
     * @return ответ с информацией об отправленном сообщении
     * @throws IOException при возникновении ошибки ввода вывода
     * @throws UnsuccessfulHttpException если API вернуло ошибку
     */
    MessageResponse sendMessage(@NotNull String message, int dialogId) throws IOException, UnsuccessfulHttpException;

    /**
     * Обновляет пару токенов доступа с использованием текущего refresh-токена.
     *
     * @return новая пара access- и refresh-токенов
     * @throws IOException при возникновении ошибки ввода вывода
     * @throws UnsuccessfulHttpException если API вернуло ошибку
     */
    TokenPair refreshTokens() throws IOException, UnsuccessfulHttpException;

    /**
     * Возвращает список диалогов текущего пользователя.
     *
     * @return ответ со списком диалогов
     * @throws IOException при возникновении ошибки ввода вывода
     * @throws UnsuccessfulHttpException если API вернуло ошибку
     */
    DialogsResponse dialog() throws IOException, UnsuccessfulHttpException;

    /**
     * Возвращает диалог с указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return ответ с информацией о диалоге
     * @throws IOException при возникновении ошибки ввода вывода
     * @throws UnsuccessfulHttpException если API вернуло ошибку
     */
    DialogWithUserResponse dialogWithUser(long userId) throws IOException, UnsuccessfulHttpException;

    /**
     * Создаёт новый диалог с указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return созданный диалог
     * @throws IOException при возникновении ошибки ввода вывода
     * @throws UnsuccessfulHttpException если API вернуло ошибку
     */
    Dialog createDialog(long userId) throws IOException, UnsuccessfulHttpException;

    /**
     * Возвращает текущий access токен.
     *
     * @return access-токен
     */
    String getAccessToken();

    /**
     * Возвращает текущий refresh токен.
     *
     * @return refresh-токен
     */
    String getRefreshToken();

    /**
     * Возвращает время истечения текущего access токена.
     *
     * @return время истечения access токена
     */
    long getAccessTokenExpiresAt();

    /**
     * Возвращает User-Agent, используемый при выполнении API-запросов.
     *
     * @return значение User-Agent
     */
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
