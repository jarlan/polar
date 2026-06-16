# Dance Partner Challenge

Nettapplikasjon for danseeventer. Deltakere skanner hverandres QR-koder etter dans, og applikasjonen registrerer unike dansepartnerskap og viser et sanntids-leaderboard.

---

## Kom i gang

### 1. Start applikasjonen

```bash
mkdir -p data
mvn spring-boot:run
```

Åpne i nettleser: `https://localhost:8443`

> Appen kjører på HTTPS med et selvsignert sertifikat. Nettleseren vil vise en advarsel første gang — trykk **Avansert → Fortsett til nettstedet**.

---

## Bruk på arrangement

For at deltakere skal kunne skanne QR-koder med mobilen, må alle enheter være på samme nettverk (f.eks. WiFi).

### 1. Finn serverens IP-adresse

```bash
hostname -I
# eksempel: 192.168.1.100
```

### 2. Start serveren

```bash
mkdir -p data && mvn spring-boot:run
```

### 3. Åpne admin-siden via IP (ikke localhost!)

```
https://192.168.1.100:8443/admin
```

> Viktig: Åpne admin-siden via IP-adressen slik at base URL-feltet fylles riktig automatisk.

### 4. Importer deltakere (CSV)

Format:
```
id;name
1;Anne
2;Ola
3;Per
```

Admin → **Import Participants** → last opp CSV-fil.

### 5. Generer QR-koder

Admin → **Generate QR Codes**

- Base URL fylles automatisk fra adressen du bruker (f.eks. `https://192.168.1.100:8443`)
- QR-koder lagres i `./generated-qr/`
- Last ned alle som ZIP via **Download QR ZIP**

QR-innhold: `https://192.168.1.100:8443/register?partner={id}`

### 6. Skriv ut etiketter på BT M110

Hver QR-fil er en ferdig etikettbilde (384 × 240 px, ≈ 48 mm × 30 mm ved 203 DPI) med:
- **QR-kode** — skannes av deltakeren som registrerer dans
- **Navn** — tydelig lesbar tekst
- **ID** — deltakernes unike nummer

**Utskrift:**
1. Last ned ZIP fra admin-panelet og pakk ut
2. Overfør PNG-filene til mobilen (AirDrop, kabel, e-post, osv.)
3. Åpne produsentens app for BT M110
4. Velg **Skriv ut fra bilde** og velg en PNG-fil
5. Still inn papirbredde til **40 mm** i apppens innstillinger
6. Skriv ut — en etikett per deltaker

> Tips: Du kan også skrive ut hele partiet fra en datamaskin som er paret med skriveren via Bluetooth, ved å bruke utskriftsdialog og velge 40 mm papirbredde.

### 7. Del og bruk QR-koder

Deltakere skanner hverandres QR-kode etter dans. Applikasjonen registrerer og teller unike partnerskap.

---

## Admin

| | |
|---|---|
| URL | `https://localhost:8443/admin` |
| Brukernavn | `admin` |
| Passord | `polarsirkelrock` |

**Funksjoner:**
- Importer deltakere via CSV
- Generer etikettbilder med QR-kode, navn og ID
- Last ned alle etiketter som ZIP
- Se live leaderboard
- Kjør trekning (premieutdeling)
- Eksporter statistikk (JSON)

---

## Sider

| URL | Beskrivelse |
|-----|-------------|
| `/` | Forside med topp 10 og statistikk |
| `/leaderboard` | Fullskjerm sanntids-leaderboard |
| `/register?partner={id}` | Registrer dans (åpnes via QR) |
| `/participant/{id}` | Deltaker-statistikk |
| `/admin` | Admin-panel (krever innlogging) |

---

## REST API

| Metode | URL | Beskrivelse |
|--------|-----|-------------|
| `POST` | `/api/dances` | Registrer danspar |
| `GET` | `/api/leaderboard` | Hent leaderboard |
| `GET` | `/api/statistics` | Hent statistikk |

### POST /api/dances

```json
{ "participantId": 1, "partnerId": 2 }
```

---

## WebSocket

Leaderboard oppdateres automatisk i sanntid via STOMP/WebSocket.

- Endepunkt: `/ws` (SockJS)
- Topic: `/topic/leaderboard`

---

## Docker

```bash
docker compose up -d --build
```

Appen er tilgjengelig på `https://localhost:8443`.

Data lagres i `./data/` (SQLite) og `./generated-qr/` (QR-etikettbilder).

> Ingen `mvnw` nødvendig — Docker-bygget bruker `maven:3.9-eclipse-temurin-21-alpine` med Maven innebygd.

---

## Teknologi

| | |
|---|---|
| Rammeverk | Java 21, Spring Boot 3.5 |
| Database | SQLite + Spring Data JPA |
| Frontend | Thymeleaf + Bootstrap 5 |
| Sanntid | Spring WebSocket (STOMP) |
| Sikkerhet | Spring Security (HTTPS + innlogging) |
| QR-generering | ZXing + Java2D (etikettkomponering) |
| Bygg | Maven, Lombok |
