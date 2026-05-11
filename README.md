![BrainBank Logo](src/main/resources/images/brand_logo.png)
Progetto sviluppato per il corso di Ingegneria del Software e Progettazione Web — Università degli Studi di Roma Tor Vergata.
## Descrizione

BrainBank permette a studenti e tutor di interagire tramite un'interfaccia CLI (e in futuro GUI JavaFX):

- Gli **studenti** possono cercare tutor per materia, prenotare lezioni e ricevere notifiche via email
- I **tutor** possono gestire la propria disponibilità e monitorare i progressi degli studenti

## Tecnologie

- Java 17
- Maven
- MySQL
- JavaFX *(in sviluppo)*
- SendGrid API *(notifiche email)*
- Jitsi Meet *(link videolezioni)*

## Architettura

Pattern **MVC** con separazione netta tra:
- `controller/applicativo` — logica di business
- `controller/cli` — interfaccia utente CLI
- `dao` — accesso ai dati (DB, File, Memory)
- `model` — entità del dominio
- `bean` — oggetti di trasferimento dati

Il sistema supporta tre modalità di persistenza:
- **DATABASE** — MySQL (full-version)
- **FILE** — JSON (full-version)
- **MEMORY** — in-memory (demo-version)

## Avvio

Per avviare in **modalità demo** (no DB richiesto):
```java
// In Main.java
private static final boolean DEMO_MODE = true;
```

Per avviare in **modalità full** con MySQL:
```java
private static final boolean DEMO_MODE = false;
```

Configurare `src/main/resources/db.properties` con le credenziali del DB e la API key SendGrid.

## Credenziali demo

| Ruolo | Email | Password |
|-------|-------|----------|
| Studente | `student@demo` | qualsiasi |
| Tutor | `tutor@demo` | qualsiasi |


