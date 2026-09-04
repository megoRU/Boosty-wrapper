# Boosty API Wrapper for Java

Легковесная Java-библиотека (Оболочка / Wrapper) для работы с API сервиса [Boosty.to](https://boosty.to).

## Возможности

- Получение списка подписчиков блога (`getSubscribers`).
- Ручное обновление OAuth 2.0 токенов (`refreshTokens`).
- Отслеживание точного срока действия access token (`expiresAt`).
- Поддержка ротации `refreshToken` со стороны Boosty.
- Гибкая настройка `User-Agent`.
- Отсутствие принудительной привязки к БД или файловой системе (сохранением токенов управляет пользователь).

---

## Авторизация и необходимые данные

Для инициализации `BoostyAPI` необходимы следующие данные:

1. **`refreshToken`** (обязательный параметр) — OAuth refresh token, используемый для получения новой пары токенов access/refresh.
2. **`deviceId`** (обязательный параметр) — уникальный идентификатор устройства/клиента, зарегистрированный в Boosty для данной сессии авторизации.
3. **`accessToken`** (опциональный параметр) — действующий access token для выполнения запросов к защищённым эндпоинтам Boosty.
4. **`expiresAt`** (опциональный параметр) — абсолютное время истечения срока действия `accessToken` в миллисекундах (`System.currentTimeMillis() + expires_in * 1000`).

---

## Настройка User-Agent

Библиотека отправляет заголовок `User-Agent` во всех HTTP-запросах к Boosty (включая запросы на обновление токенов).

- **По умолчанию**: если `User-Agent` не указан, используется стандартный заголовок браузера Chrome:
  `Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36`
- **Кастомный User-Agent**: вы можете передать свой `User-Agent` при сборке экземпляра через `Builder`.

---

## Создание `BoostyAPI` через `Builder`

```java
import org.boosty.impl.BoostyAPI;

BoostyAPI boostyAPI = new BoostyAPI.Builder()
        .setDeviceId("your-device-id")
        .setRefreshToken("your-refresh-token")
        .setAccessToken("your-access-token") // Опционально
        .setExpiresAt(savedExpiresAtTimestamp)  // Опционально (в мс)
        .setUserAgent("MyCustomApp/1.0")        // Опционально
        .build();
```

---

## Получение срока действия access token

Вы можете узнать текущие токены и время истечения срока действия access token:

```java
String accessToken = boostyAPI.getAccessToken();
String refreshToken = boostyAPI.getRefreshToken();
long expiresAt = boostyAPI.getAccessTokenExpiresAt(); // Время в мс (epoch millis)

// Проверка, истекает ли токен в ближайшее время (например, в течение 5 минут)
boolean isExpiringSoon = System.currentTimeMillis() + (5 * 60 * 1000) >= expiresAt;
```

---

## Ручное обновление токенов (`refreshTokens`)

Для обновления токенов вызывается метод `refreshTokens()`:

```java
import org.boosty.entity.TokenPair;

TokenPair newTokens = boostyAPI.refreshTokens();

String newAccessToken = newTokens.getAccessToken();
String newRefreshToken = newTokens.getRefreshToken();
long newExpiresAt = newTokens.getExpiresAt();
```

> **Важно!**
> 1. Boosty может ротировать `refreshToken` при каждом обновлении. После вызова `refreshTokens()` старый `refreshToken` может стать недействительным.
> 2. Библиотека **НЕ сохраняет** токены автоматически в файл, базу данных или настройки.
> 3. Приложение пользователя **должно самостоятельно сохранить** полученную пару токенов (`accessToken`, `refreshToken`, `expiresAt`), чтобы использовать их при следующем запуске.

---

## Полный пример жизненного цикла (Lifecycle)

Ниже приведён концептуальный пример работы приложения с библиотекой: запуск, проверка срока действия токена, обновление при необходимости, сохранение новых токенов и следующий запуск.

```java
import org.boosty.entity.Subscriber;
import org.boosty.entity.TokenPair;
import org.boosty.impl.BoostyAPI;

import java.util.List;

public class ApplicationLifecycleExample {

    public static void main(String[] args) {
        // 1. Загрузка ранее сохранённых данных из локального хранилища/БД
        SavedTokenState savedState = Storage.loadTokenState();

        // 2. Инициализация BoostyAPI через Builder
        BoostyAPI boostyAPI = new BoostyAPI.Builder()
                .setDeviceId(savedState.getDeviceId())
                .setRefreshToken(savedState.getRefreshToken())
                .setAccessToken(savedState.getAccessToken())
                .setExpiresAt(savedState.getExpiresAt())
                .setUserAgent("MyBot/1.0.0")
                .build();

        try {
            // 3. Проверяем, не истекает ли access token
            long currentTime = System.currentTimeMillis();
            if (boostyAPI.getAccessToken() == null || boostyAPI.getAccessTokenExpiresAt() <= currentTime) {
                System.out.println("Access token истек или отсутствует, обновляем токены...");

                // Обновляем токены через API
                TokenPair newTokens = boostyAPI.refreshTokens();

                // 4. СОХРАНЯЕМ новые токены в хранилище приложения!
                Storage.saveTokenState(
                        newTokens.getAccessToken(),
                        newTokens.getRefreshToken(),
                        newTokens.getExpiresAt()
                );
                System.out.println("Новые токены успешно сохранены!");
            }

            // 5. Выполнение запросов к Boosty API
            List<Subscriber> subscribers = boostyAPI.getSubscribers("blogName", 100);
            for (Subscriber subscriber : subscribers) {
                System.out.println("Подписчик: " + subscriber.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## Методы `BoostyAPI`

| Метод | Описание |
|---|---|
| `getSubscribers(blogName, limit)` | Возвращает список подписчиков указанного блога. |
| `refreshTokens()` | Выполняет запрос на обновление OAuth2 токенов и возвращает новый `TokenPair`. |
| `getAccessToken()` | Возвращает текущий `accessToken`. |
| `getRefreshToken()` | Возвращает текущий `refreshToken`. |
| `getAccessTokenExpiresAt()` | Возвращает timestamp (в миллисекундах), когда истекает текущий `accessToken`. |
| `getUserAgent()` | Возвращает переданный или дефолтный `User-Agent`. |
