# Dance Partner Challenge

A Spring Boot web application for tracking unique dance partnerships at dance events.

## Quick Start

```bash
mkdir -p data
mvn spring-boot:run
```

Then open: http://localhost:8080

## Docker

```bash
docker-compose up -d
```

## Admin Access

URL: http://localhost:8080/admin  
Username: `admin`  
Password: `polarsirkelrock`

## CSV Import Format

```
id;name
1;Anne
2;Ola
3;Per
```

Upload via Admin → Import Participants

## QR Code Generation

1. Import participants  
2. Admin → Generate QR Codes  
3. QR codes saved to `./generated-qr/`  
4. Download all as ZIP  

QR content: `http://SERVER/register?partner={id}`

## WebSocket

Leaderboard updates are pushed live via STOMP WebSocket.  
Topic: `/topic/leaderboard`  
Endpoint: `/ws` (SockJS)

## API

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/dances | Register dance pair |
| GET | /api/leaderboard | Get leaderboard |
| GET | /api/statistics | Get event statistics |

## Technology Stack

- Java 21, Spring Boot 3.5
- SQLite + Spring Data JPA
- Thymeleaf + Bootstrap 5
- Spring WebSocket (STOMP)
- Spring Security
- ZXing QR generation
- Lombok
