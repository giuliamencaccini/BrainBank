Progetto sviluppato per il corso di Ingegneria del Software e Progettazione Web — Università degli Studi di Roma Tor Vergata.
![BrainBank Logo](src/main/resources/images/brand_logo.png)
## Descrizione

BrainBank permette a studenti e tutor di interagire tramite un'interfaccia CLI (e in futuro GUI JavaFX):

- Gli **studenti** possono cercare tutor per materia, prenotare lezioni e ricevere notifiche via email
- I **tutor** possono gestire la propria disponibilità e monitorare i progressi degli studenti

## Tecnologie

- Java 17
- Maven
- MySQL
- JavaFX 
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

All’avvio dell’applicazione viene richiesto di selezionare la modalità di persistenza:

- `Demo` → dati simulati in memoria
- `Database` → persistenza MySQL
- `File` → persistenza su file JSON

Successivamente viene richiesto di selezionare l’interfaccia:

- `CLI` → interfaccia testuale
- `GUI` → interfaccia grafica

Per utilizzare la modalità database è necessario configurare:

```text
src/main/resources/db.properties
```

con:
- credenziali MySQL
- configurazione database
- API key SendGrid

## Credenziali demo

| Ruolo     | Email             | Password   |
|------------|------------------|------------|
| Studente  | `student@demo`   | qualsiasi |
| Tutor     | `tutor@demo`     | qualsiasi |
| Admin     | `admin@demo`     | qualsiasi |

## Autrice
Mencaccini Giulia
