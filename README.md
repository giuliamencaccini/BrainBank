Progetto sviluppato per il corso di Ingegneria del Software e Progettazione Web — Università degli Studi di Roma Tor Vergata.
![BrainBank Logo](src/main/resources/images/brand_logo.png)
## Descrizione

BrainBank permette a studenti e tutor di interagire tramite un'interfaccia CLI o GUI (JavaFX):

- Gli **studenti** possono cercare tutor per materia, prenotare lezioni e ricevere notifiche via email
- I **tutor** possono gestire la propria disponibilità e monitorare i progressi degli studenti
- Gli **amministatori** possono visualizzare report e statistiche sull'utilizzo della piattaforma

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
- `controller/gui` — interfaccia utente GUI
- `dao` — accesso ai dati (DB, File, Memory)
- `model` — entità del dominio
- `bean` — oggetti di trasferimento dati
- `view/cli` — boundary view CLI
- `view/gui` — boundary view GUI
- `pattern` — GoF patterns (Singleton, Observer, State)

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
con il seguente contenuto:
```text
db.url=jdbc:mysql://localhost:3306/brainbankdb
db.user.login=bb_login
db.user.login.password=bb_login
db.user.student=bb_student
db.user.student.password=bb_student
db.user.tutor=bb_tutor
db.user.tutor.password=bb_tutor
db.user.admin=bb_admin
db.user.admin.password=bb_admin
sendgrid.api.key=YOUR_SENDGRID_API_KEY
```

## Credenziali demo

| Ruolo     | Email             | Password   |
|------------|------------------|------------|
| Studente  | `student@demo`   | qualsiasi |
| Tutor     | `tutor@demo`     | qualsiasi |
| Admin     | `admin@demo`     | qualsiasi |


## Credenziali Database (modalità MySQL)

| Ruolo     | Email             | Password   |
|------------|------------------|------------|
| Studente  | `emma@test.com`      | password123 |
| Tutor     | `gabriele@test.com`  | password123 |
| Admin     | `admin@test.com`     | password123 |


Other test accounts available — see BrainBank_db.sql for the full list.
## Autrice
Mencaccini Giulia
