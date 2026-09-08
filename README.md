# Boosty API Wrapper for Java
[![JitPack](https://jitpack.io/v/megoRU/Boosty-wrapper.svg)](https://jitpack.io/#megoRU/Boosty-wrapper)

Легковесная Java-библиотека для работы с API [Boosty.to](https://boosty.to).

## Возможности

* Получение подписчиков блога.
* Получение личных чатов
* Создание чата с подписчиком
* Отправка сообщений в чат
* Поддержка ротации `refreshToken`.
* Свои настройки `User-Agent`.

## Установка (Maven)

Добавьте репозиторий JitPack и зависимость:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
<groupId>com.github.megoRU</groupId>
<artifactId>Boosty-wrapper</artifactId>
<version>1.0.6</version>
</dependency>
```

## Инициализация

Для создания `BoostyAPI` нужны:

Получить можно на странице `Boosty.to` через: `F12` -> `Application` -> `Storage` -> `Local storage`

* `accessToken` — ключ доступа.
* `deviceId` — идентификатор устройства. `_clientId`
* `refreshToken` — OAuth refresh token.

`userAgent` — необязательный.

```java
BoostyAPI boostyAPI = new BoostyAPI.Builder()
        .setDeviceId("your-device-id")
        .setRefreshToken("your-refresh-token")
        .setAccessToken("your-access-token")
        .setExpiresAt(expiresAt)
        .setUserAgent("MyApp/1.0")
        .build();
```

Если `User-Agent` не указан, используется стандартный Chrome User-Agent.

## Получение подписчиков

```java
List<Subscriber> subscribers = boostyAPI.getSubscribers("blogName", 100);
```

## Обновление токенов

Проверить срок действия токена можно через `getAccessTokenExpiresAt()`:

```java
if (System.currentTimeMillis() >= boostyAPI.getAccessTokenExpiresAt()) {
TokenPair tokens = boostyAPI.refreshTokens();

String accessToken = tokens.getAccessToken();
String refreshToken = tokens.getRefreshToken();
long expiresAt = tokens.getExpiresAt();
}
```

### Важно

Boosty может менять `refreshToken` при обновлении. Поэтому после `refreshTokens()` необходимо сохранить новые:

* `accessToken`
* `refreshToken`
* `expiresAt`

Библиотека **не сохраняет токены автоматически** — это должен делать пользователь библиотеки.

### Лицензия

MIT