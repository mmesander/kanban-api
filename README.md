# Kanban API

REST API voor het beheren van taken op een Kanban-bord, gebouwd met Spring Boot volgens een hexagonale architectuur (ports & adapters).

## Inhoud

- [Vereisten](#vereisten)
- [Applicatie starten](#applicatie-starten)
- [Profielen en configuratie](#profielen-en-configuratie)
- [Endpoints](#endpoints)
- [Swagger UI](#swagger-ui)
- [H2-console](#h2-console)

## Vereisten

- Java 17
- Maven (niet verplicht: de Maven Wrapper `mvnw` zit in het project)

## Applicatie starten

**Via de terminal**

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

**Via IntelliJ**

Open `KanbanApiApplication.java` en klik op het groene ▶-icoon naast de `main`-methode.

De applicatie draait als in de console `Tomcat started on port 8095` verschijnt.
De basis-URL is daarna:

```
http://localhost:8095
```

## Profielen en configuratie

De configuratie staat in twee bestanden:

| Bestand                         | Inhoud                                                   |
|---------------------------------|----------------------------------------------------------|
| `application.properties`        | Algemene instellingen (naam, poort, actief profiel)      |
| `application-h2.properties`     | H2 in-memory database, JPA/Hibernate en de H2-console    |

Het profiel `h2` staat standaard aan via `spring.profiles.active=h2`.
Wil je het profiel bij het starten zelf kiezen:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```

> **Let op:** H2 draait in het geheugen. Bij elke herstart van de applicatie is de database weer leeg.

## Endpoints

| Methode | URL          | Omschrijving               |
|---------|--------------|----------------------------|
| `POST`  | `/api/tasks` | Nieuwe taak aanmaken       |

### Taak aanmaken

```
POST http://localhost:8095/api/tasks
Content-Type: application/json
```

**Request body**

| Veld          | Type   | Verplicht | Regels                                                      |
|---------------|--------|-----------|-------------------------------------------------------------|
| `title`       | string | ja        | Niet leeg, maximaal 100 tekens                              |
| `description` | string | nee       | Maximaal 500 tekens                                         |
| `status`      | string | nee       | `TO_DO`, `IN_PROGRESS`, `DONE` of `To Do`, `In Progress`, `Done` (hoofdletterongevoelig). Standaard: `TO_DO` |

**Voorbeeld request**

```json
{
  "title": "Eerste taak",
  "description": "H2 testen",
  "status": "TO_DO"
}
```

**Voorbeeld response** - `201 Created`

De header `Location` bevat de URL van de nieuwe taak, bijvoorbeeld
`http://localhost:8095/api/tasks/3f2b8c1e-5d4a-4e7b-9c1a-2b6d8e0f7a91`.

```json
{
  "id": "3f2b8c1e-5d4a-4e7b-9c1a-2b6d8e0f7a91",
  "title": "Eerste taak",
  "description": "H2 testen",
  "status": "TO_DO",
  "createdAt": "2026-11-18T10:15:30.123456Z",
  "updatedAt": "2026-11-18T10:15:30.123456Z"
}
```

**Testen met PowerShell**

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8095/api/tasks `
  -ContentType "application/json" `
  -Body '{"title":"Eerste taak","description":"H2 testen","status":"TO_DO"}'
```

**Testen met curl**

```bash
curl -X POST http://localhost:8095/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Eerste taak","description":"H2 testen","status":"TO_DO"}'
```

## Swagger UI

Alle endpoints kun je bekijken en uitproberen in Swagger UI:

```
http://localhost:8095/swagger-ui/index.html
```

Klap een endpoint open, klik op **Try it out**, vul de body in en klik op **Execute**.

## H2-console

In de H2-console bekijk je de inhoud van de in-memory database.

1. Start de applicatie.
2. Ga naar **http://localhost:8095/h2-console**
3. Vul het inlogscherm in:

   | Veld         | Waarde                  |
   |--------------|-------------------------|
   | Driver Class | `org.h2.Driver`         |
   | JDBC URL     | `jdbc:h2:mem:kanbandb`  |
   | User Name    | `sa`                    |
   | Password     | *(leeg laten)*          |

4. Klik op **Connect** en voer bijvoorbeeld uit:

   ```sql
   SELECT * FROM TASKS;
   ```

> **Let op:** vervang de standaard JDBC URL (`jdbc:h2:~/test`) door `jdbc:h2:mem:kanbandb`. Anders maakt H2 een nieuwe, lege database aan en zie je de tabel `TASKS` niet.
