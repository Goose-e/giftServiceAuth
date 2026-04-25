# Технический протокол обмена сообщениями между Frontend и Backend (GiftServiceAuth)

## 1) Общие правила обмена

- **Базовый URL API**: `http://<host>:<port>` (по умолчанию для Spring Boot обычно `8080`, если не переопределено).
- **Формат тела запросов/ответов**: `application/json`.
- **Авторизация**: Bearer JWT в заголовке:
  - `Authorization: Bearer <accessToken>`
- **Исключения (эндпоинты без токена)**:
  - `POST /auth/register`
  - `POST /auth/login`
  - `POST /auth/refresh`

## 2) Единая обёртка ответа backend

Большинство ответов от backend приходят в едином формате:

```json
{
  "requestId": "",
  "responseCode": "OC_OK",
  "message": "Success",
  "error": null,
  "responseEntity": {},
  "errors": []
}
```

### Поля обёртки

- `requestId`: строка (в текущей реализации чаще пустая).
- `responseCode`:
  - `OC_OK` — успешный бизнес-ответ.
  - `BAD_REQUEST`, `UNAUTHORIZED` — ошибки из обработчика исключений.
- `message`: текст статуса (например, `Success`).
- `error`: строка ошибки или `null`.
- `responseEntity`: полезная нагрузка конкретного метода.
- `errors`: массив расширенных ошибок (обычно пустой).

## 3) Обработка ошибок (важно для frontend)

Есть **два формата ошибок**:

1. **Ошибки бизнес-логики и валидации** (через `ApiExceptionHandler`):
   - Обычно приходят в стандартной обёртке с `responseCode=BAD_REQUEST` или `UNAUTHORIZED`.
   - Пример:

```json
{
  "requestId": "",
  "responseCode": "BAD_REQUEST",
  "message": "Login already exists",
  "error": "Login already exists",
  "responseEntity": {
    "error": "Login already exists"
  },
  "errors": [
    { "error": "Error", "errorMsg": "Login already exists" }
  ]
}
```

2. **Ошибка отсутствия/невалидности авторизации на уровне security entry point**:
   - HTTP статус: `401`
   - Формат (НЕ стандартная обёртка):

```json
{
  "error": "unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

Рекомендация frontend: проверять и `HTTP status`, и наличие поля `responseCode`.

## 4) Контракт эндпоинтов

---

## AUTH

### `POST /auth/register`
Регистрация пользователя.

**Request body:**
```json
{
  "username": "Иван",
  "login": "ivan_login",
  "password": "strong_password"
}
```

**Успешный `responseEntity` (`AuthResponse`):**
```json
{
  "userId": 1,
  "username": "Иван",
  "login": "ivan_login",
  "accessToken": "<jwt>",
  "accessExpiresIn": 3600,
  "refreshToken": "<jwt>",
  "refreshExpiresIn": 86400,
  "tokenType": "Bearer"
}
```

### `POST /auth/login`
Логин пользователя.

**Request body:**
```json
{
  "login": "ivan_login",
  "password": "strong_password"
}
```

**Успешный `responseEntity`:** такой же как у `/auth/register`.

### `POST /auth/refresh`
Обновление токенов по refresh token.

**Request body:**
```json
{
  "refreshToken": "<jwt_refresh_token>"
}
```

**Успешный `responseEntity` (`RefreshResponseDTO`):**
```json
{
  "refreshToken": "<new_refresh_jwt>",
  "accessToken": "<new_access_jwt>",
  "accessExpiresIn": 3600,
  "refreshExpiresIn": 86400,
  "tokenType": "Bearer"
}
```

---

## FRIENDS

> Для всех методов ниже требуется `Authorization: Bearer <accessToken>`.

### `POST /friends/add`
Добавить пользователя в друзья (двусторонняя запись).

**Request body:**
```json
{
  "friendLogin": "friend_login"
}
```

**Успешный `responseEntity`:** пустой объект типа `EmptyResponseDTO`.

### `DELETE /friends/remove`
Удалить из друзей.

**Request body:**
```json
{
  "friendLogin": "friend_login"
}
```

**Успешный `responseEntity`:** пустой объект типа `EmptyResponseDTO`.

### `GET /friends`
Получить список друзей текущего пользователя.

**Успешный `responseEntity` (`FriendListResponseDTO`):**
```json
{
  "friends": [
    {
      "userId": 2,
      "username": "Петр",
      "login": "petr_login"
    }
  ]
}
```

### `GET /friends/search?username=<part>`
Поиск пользователей по части `username` (без учёта регистра).

**Query params:**
- `username` — обязательный.

**Успешный `responseEntity`:** формат как в `GET /friends`.

---

## GIFT IDEAS

### `GET /gift-ideas/catalog`
Каталог подарков (публичный).

**Успешный `responseEntity` (`GiftListResponseDTO`):**
```json
{
  "gifts": [
    {
      "giftId": 10,
      "giftName": "Наушники",
      "avgPrice": 5000,
      "tagName": "tech"
    }
  ]
}
```

### `POST /gift-ideas/catalog`
Создать подарок в каталоге.

**Request body:**
```json
{
  "giftName": "Настольная игра",
  "giftAvgPrice": 2500,
  "tagName": "gaming"
}
```

**Успешный ответ:** возвращается полный обновлённый каталог (`GiftListResponseDTO`).

### `POST /gift-ideas/wishlist`
Добавить подарок в мой вишлист.

**Auth:** требуется.

**Request body:**
```json
{
  "giftId": 10
}
```

**Успешный `responseEntity`:** `EmptyResponseDTO`.

### `DELETE /gift-ideas/wishlist`
Удалить подарок из моего вишлиста.

**Auth:** требуется.

**Request body:**
```json
{
  "giftId": 10
}
```

**Успешный `responseEntity`:** `EmptyResponseDTO`.

### `GET /gift-ideas/wishlist/me`
Получить мой вишлист.

**Auth:** требуется.

**Успешный `responseEntity`:** `GiftListResponseDTO`.

### `GET /gift-ideas/wishlist/friend/{friendLogin}`
Получить вишлист друга.

**Auth:** требуется.

**Path params:**
- `friendLogin` — логин друга.

**Условие доступа:** пользователь должен быть в друзьях.

### `GET /gift-ideas/recommendations/friend/{friendLogin}`
Получить рекомендации подарков для друга (по весам тегов из его вишлиста).

**Auth:** требуется.

**Успешный `responseEntity` (`RecommendationResponseDTO`):**
```json
{
  "recommendations": [
    {
      "giftId": 15,
      "giftName": "Геймпад",
      "reason": "Высокий интерес к тегу 'gaming' (вес=3)",
      "tagName": "gaming",
      "avgPrice": 4200
    }
  ]
}
```

### `POST /gift-ideas/victim`
Создать анкету “victim” для персональных рекомендаций.

**Auth:** требуется.

**Request body (`VictimRequest`):**
```json
{
  "gender": "male",
  "birthdate": "1999-05-20T00:00:00",
  "country": "Russia",
  "city": "Moscow",
  "info": "Любит музыку, путешествия и спорт"
}
```

> `birthdate` должен отправляться в ISO-формате даты-времени (`yyyy-MM-dd'T'HH:mm:ss`).

**Успешный ответ:** список всех моих анкет (`VictimListResponseDTO`).

### `GET /gift-ideas/victim`
Получить список моих анкет.

**Auth:** требуется.

**Успешный `responseEntity`:**
```json
{
  "victims": [
    {
      "victimId": 1,
      "gender": "male",
      "birthdate": "1999-05-20T00:00:00",
      "country": "Russia",
      "city": "Moscow",
      "info": "Любит музыку"
    }
  ]
}
```

### `GET /gift-ideas/recommendations/victim/{victimId}`
Рекомендации по анкете victim.

**Auth:** требуется.

**Path params:**
- `victimId` — ID анкеты.

**Успешный `responseEntity`:** `RecommendationResponseDTO` (как выше).

---

## 5) Что должен реализовать frontend

1. **Хранение токенов**
   - после `login/register` сохранять `accessToken` + `refreshToken` и время жизни.
2. **Подстановка access token**
   - для всех защищённых методов добавлять `Authorization: Bearer ...`.
3. **Авто-refresh**
   - при `401` или истечении `accessExpiresIn` вызывать `/auth/refresh`.
4. **Унифицированный парсинг ошибок**
   - обрабатывать оба формата ошибок (стандартная обёртка и security-401).
5. **Сериализация даты**
   - `VictimRequest.birthdate` отправлять как ISO `LocalDateTime`.

## 6) Краткий пример последовательности вызовов

1. `POST /auth/register` или `POST /auth/login`.
2. Сохранить токены.
3. `GET /gift-ideas/catalog` для показа каталога.
4. `POST /gift-ideas/wishlist` и `GET /gift-ideas/wishlist/me` для работы с личным списком.
5. При `401` выполнить `POST /auth/refresh`, обновить токены, повторить исходный запрос.

---

Если нужно, следующим шагом могу сделать отдельный вариант этого же документа в формате **OpenAPI YAML** (чтобы фронту можно было сгенерировать типы/клиент автоматически).
