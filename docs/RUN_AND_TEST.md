# 🎮 Инструкция по запуску и тестированию Tic-Tac-Toe Backend (JWT)

---

## 📌 1. Быстрый запуск проекта

### Шаг 1.1. Запуск PostgreSQL в Docker
Откройте терминал в папке проекта (`src/AP1-Jv-T04B`) и выполните команду:

```bash
docker compose -f src/main/docker/docker-compose.yml up -d
```

> **Параметры БД:**
> - Хост: `localhost:5432` | База: `tictactoe` | Пользователь / Пароль: `app` / `app`
> - Hibernate автоматически создает/обновляет схему таблиц (`users`, `user_roles`, `games`).

Проверка статуса:
```bash
docker ps
```

---

### Шаг 1.2. Сборка и запуск Spring Boot

```bash
./gradlew bootRun
```

После появления строки `Started Application in ... seconds` бэкенд готов к приему запросов по адресу: **`http://localhost:8080`**.  
Веб-интерфейс в браузере: **`http://localhost:8080`**.

---

## 🧪 2. Готовые cURL-команды для терминала (Copy & Paste)

> 💡 **Изоляция тестовых пользователей:** В командах ниже используются отдельные пользователи **Charlie** (`charlie`) и **Diana** (`diana`), чтобы они не конфликтовали с аккаунтами из браузера.

---

### Сценарий 1. Регистрация и JWT-Аутентификация

#### 1.1. Регистрация Игрока 1 (Charlie)
```bash
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"login": "charlie", "password": "charliepassword123"}'
```
*Ожидаемый ответ:* `HTTP 201 Created`, тело `{"success": true}`.

#### 1.2. Регистрация Игрока 2 (Diana)
```bash
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"login": "diana", "password": "dianapassword123"}'
```
*Ожидаемый ответ:* `HTTP 201 Created`, тело `{"success": true}`.

#### 1.3. Вход в систему (Login) и получение токенов
```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login": "charlie", "password": "charliepassword123"}'
```
*Ожидаемый ответ:* `HTTP 200 OK`
```json
{
  "type": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1Ni...",
  "refreshToken": "eyJhbGciOiJIUzI1Ni..."
}
```

> 💡 **Лайфхак:** Сохраните полученные токены в переменные терминала для автоматической подстановки:
> ```bash
> export ACCESS_TOKEN="ВСТАВЬ_СЮДА_ЗНАЧЕНИЕ_ACCESSTOKEN"
> export REFRESH_TOKEN="ВСТАВЬ_СЮДА_ЗНАЧЕНИЕ_REFRESHTOKEN"
> ```

---

#### 1.4. Обновление только Access-токена
```bash
curl -i -X POST http://localhost:8080/api/auth/token \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\": \"$REFRESH_TOKEN\"}"
```
*Ожидаемый ответ:* `HTTP 200 OK` со свежим `accessToken`.

---

#### 1.5. Ротация токенов (выпуск новых Access И Refresh)
```bash
curl -i -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\": \"$REFRESH_TOKEN\"}"
```
*Ожидаемый ответ:* `HTTP 200 OK` с новой парой токенов.

---

### Сценарий 2. Профиль пользователя по токену

#### 2.1. Получение информации о себе по Access-токену
```bash
curl -i -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```
*Ожидаемый ответ:* `HTTP 200 OK`
```json
{
  "uuid": "4c946e31-891a-464a-939a-6ca6ea17c6e7",
  "login": "charlie"
}
```

#### 2.2. Проверка защиты: запрос БЕЗ токена
```bash
curl -i -X GET http://localhost:8080/api/user/me
```
*Ожидаемый ответ:* `HTTP 401 Unauthorized` (эндпоинт защищен).

---

### Сценарий 3. Игровой процесс (Крестики-Нолики)

#### 3.1. Создание новой игры против ИИ (vsBot = true)
```bash
curl -i -X POST http://localhost:8080/api/game \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"vsBot": true}'
```
*В ответе вернется JSON с `"uuid"` игры. Сохраните его:*
```bash
export GAME_ID="ВСТАВЬ_UUID_ИГРЫ_ИЗ_ОТВЕТА"
```

#### 3.2. Просмотр состояния игры по ID
```bash
curl -i -X GET "http://localhost:8080/api/game/$GAME_ID" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

#### 3.3. Ход игрока: ставим крестик (1) в центр [1, 1]
```bash
curl -i -X POST "http://localhost:8080/api/game/$GAME_ID" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"matrix": [[0,0,0],[0,1,0],[0,0,0]]}'
```
*Бот мгновенно сделает ответный ход ноликом (2)!*

---

### Сценарий 4. История завершенных игр (Задание 2)

#### 4.1. Получение завершенных игр (побед и ничьих) текущего игрока
```bash
curl -i -X GET http://localhost:8080/api/game/history \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```
*Ожидаемый ответ:* `HTTP 200 OK`
```json
[
  {
    "uuid": "e2f26102-1f35-46e8-b38a-55ed4f76f0c7",
    "field": [[1, 2, 1], [2, 1, 2], [1, 0, 0]],
    "currentPlayer": 1,
    "status": "X_WON",
    "winnerId": "4c946e31-891a-464a-939a-6ca6ea17c6e7",
    "createdAt": "2026-11-04T18:45:12"
  }
]
```

#### 4.2. Проверка защиты: запрос истории БЕЗ токена
```bash
curl -i -X GET http://localhost:8080/api/game/history
```
*Ожидаемый ответ:* `HTTP 401 Unauthorized`.

---

### Сценарий 5. Таблица лидеров / Leaderboard (Задание 3)

#### 5.1. Получение ТОП-10 игроков по винрейту
```bash
curl -i -X GET "http://localhost:8080/api/game/leaderboard?limit=10&n=10" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```
*Ожидаемый ответ:* `HTTP 200 OK`
```json
[
  {
    "uuid": "4c946e31-891a-464a-939a-6ca6ea17c6e7",
    "login": "charlie",
    "winRate": 2.0
  },
  {
    "uuid": "8b518c23-281b-419a-941a-7db6fa28c7f8",
    "login": "diana",
    "winRate": 0.5
  }
]
```

#### 5.2. Получение ТОП-5 игроков
```bash
curl -i -X GET "http://localhost:8080/api/game/leaderboard?n=5" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

#### 5.3. Проверка защиты: запрос таблицы лидеров БЕЗ токена
```bash
curl -i -X GET http://localhost:8080/api/game/leaderboard?limit=10
```
*Ожидаемый ответ:* `HTTP 401 Unauthorized`.

---

## 💻 3. Файл requests.http для IntelliJ IDEA

Если вы запускаете запросы кликом в IntelliJ IDEA / VS Code:  
Откройте файл **`src/test/java/requests.http`**, где те же сценарии описаны с автосохранением токенов в переменные.
