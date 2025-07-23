# Книжная полка

Веб-сервис для хранения, чтения и обмена электронными книгами между пользователями.

## Оглавление

- [Описание](#описание)
- [Основные возможности](#основные-возможности)
- [Технологический стек](#технологический-стек)
- [Требования](#требования)
- [Установка и запуск](#установка-и-запуск)
    - [1. Клонирование репозитория](#1-клонирование-репозитория)
    - [2. Настройка базы данных](#2-настройка-базы-данных)
    - [3. Настройка почты (Mailtrap)](#3-настройка-почты-mailtrap)
    - [4. Настройка reCAPTCHA](#4-настройка-recaptcha)
    - [5. Сборка и запуск backend](#5-сборка-и-запуск-backend)
    - [6. Доступ к API](#6-доступ-к-api)
- [Использование](#использование)
    - [Регистрация пользователя](#регистрация-пользователя)
    - [Загрузка книги](#загрузка-книги)
    - [Чтение книги](#чтение-книги)
    - [Управление полками](#управление-полками)
    - [Запрос доступа к книге](#запрос-доступа-к-книге)
    - [Пример запроса на загрузку книги (curl)](#пример-запроса-на-загрузку-книги-curl)
- [Структура проекта](#структура-проекта)
- [Документация API](#документация-api)
- [Лицензия](#лицензия)
- [Контакты](#контакты)

---

## Описание

**Книжная полка** — это сервис, позволяющий пользователям загружать электронные книги, группировать их по полкам, читать онлайн с сохранением прогресса, а также делиться доступом к книгам с другими пользователями. Скачивание книг запрещено, доступ предоставляется только для онлайн-чтения.

---

## Основные возможности

- **Регистрация с капчей и подтверждением email**  
  Защита от ботов, подтверждение аккаунта через email (Mailtrap для тестов).
- **Загрузка и конвертация книг**  
  Книги загружаются пользователями, автоматически преобразуются в HTML для постраничного чтения (асинхронно).
- **Управление полками**  
  Создание, удаление, переименование полок, перемещение книг между полками.
- **Чтение книг онлайн**  
  Сервис сохраняет прогресс чтения для каждого пользователя.
- **Запрос доступа к книгам**  
  Пользователь может запросить временный или постоянный доступ к чужой книге, владелец решает, разрешить или отказать.
- **Закладки**  
  Возможность создавать и использовать закладки для быстрого перехода к нужной странице.
- **Документация API через Swagger**  
  Удобный интерфейс для тестирования и изучения API.

---

## Технологический стек

- **Backend:** Spring Boot, Java 17, Maven
- **База данных:** PostgreSQL
- **Безопасность:** Spring Security, JWT, BCrypt
- **Email:** Mailtrap (SMTP)
- **Документация:** Swagger (springdoc-openapi)
- **Капча:** Google reCAPTCHA 
- **API:** REST

---

## Требования

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Аккаунт Mailtrap (для тестовой отправки писем)

---

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/your-username/bookshelf.git
cd bookshelf
```

### 2. Настройка базы данных

- Создайте базу данных `bookshelf` в PostgreSQL.
- Создайте пользователя и задайте пароль.
- Пропишите параметры подключения в `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookshelf
    username: your_db_user
    password: your_db_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### 3. Настройка почты (Mailtrap)

В `application.yml` добавьте настройки SMTP Mailtrap:

```yaml
spring:
  mail:
    host: smtp.mailtrap.io
    port: 2525
    username: your_mailtrap_user
    password: your_mailtrap_pass
    protocol: smtp
    properties.mail.smtp.auth: true
    properties.mail.smtp.starttls.enable: true
```

### 4. Настройка reCAPTCHA

Добавьте ключи reCAPTCHA в `application.yml`:

```yaml
recaptcha:
  secret: your_recaptcha_secret
  site: your_recaptcha_site_key
```

### 5. Сборка и запуск backend

```bash
mvn clean install
mvn spring-boot:run
```

### 6. Доступ к API

- Swagger UI: [http://localhost:8081/swagger-ui/](http://localhost:8081/swagger-ui/)

---

## Использование

### Регистрация пользователя

1. Перейдите на `/api/auth/register`.
2. Заполните форму (username, email, password, капча).
3. Подтвердите email по ссылке из письма (Mailtrap).

### Загрузка книги

- Эндпоинт: `POST /api/books/upload`
- Параметры: файл книги (MultipartFile), краткая информация (название, автор, описание), полка

### Чтение книги

- Эндпоинт: `GET /api/books/{id}/page/{pageNumber}`
- Прогресс чтения сохраняется автоматически.

### Управление полками

- Создание: `POST /api/shelves`
- Удаление: `DELETE /api/shelves/{id}`
- Перемещение книги: `PUT /api/shelves/{shelfId}/move/{bookId}`

### Запрос доступа к книге

- Эндпоинт: `POST /api/book-access-requests`
- Владелец книги получает уведомление и принимает решение.

### Пример запроса на загрузку книги (curl)

```bash
curl -X POST "http://localhost:8080/api/books/upload" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F "file=@/path/to/book.txt" \
  -F "title=Название книги" \
  -F "author=Автор" \
  -F "description=Описание" \
  -F "shelf=Моя полка"
```

---

## Структура проекта

- `controller/` — REST-контроллеры (API)
- `service/` — бизнес-логика
- `repository/` — доступ к данным (Spring Data JPA)
- `entity/` — сущности базы данных
- `dto/` — объекты передачи данных
- `config/` — конфигурация приложения (безопасность, Swagger, почта и т.д.)
- `exception/` — обработка ошибок

```
bookshelf/
  └── bookshelf/
      ├── src/
      │   ├── main/
      │   │   ├── java/com/example/bookshelf/
      │   │   │   ├── controller/      # REST-контроллеры
      │   │   │   ├── service/         # Сервисы бизнес-логики
      │   │   │   ├── repository/      # JPA-репозитории
      │   │   │   ├── entity/          # JPA-сущности
      │   │   │   ├── dto/             # DTO-объекты
      │   │   │   ├── config/          # Конфигурации Spring
      │   │   │   └── exception/       # Кастомные исключения
      │   │   └── resources/
      │   │       ├── application.yml  # Основные настройки приложения
      │   │       ├── static/          # Статические файлы (HTML)
      │   │       └── templates/       # Шаблоны 
      │   └── test/                    # Тесты
      ├── pom.xml                      # Maven-конфигурация
      └── books/                       # Папка с загруженными книгами
```

---

## Документация API

- Swagger UI: [http://localhost:8081/swagger-ui/](http://localhost:8081/swagger-ui/)
- Примеры эндпоинтов:
  - `POST /api/auth/register` — регистрация
  - `POST /api/auth/login` — вход
  - `POST /api/books/upload` — загрузка книги
  - `GET /api/books/{id}/page/{pageNumber}` — получение страницы книги
  - `POST /api/book-access-requests` — запрос доступа к книге

---

## Лицензия

Проект предназначен для учебных целей

---

## Контакты

- Email: janemiro2005@gmail.com
