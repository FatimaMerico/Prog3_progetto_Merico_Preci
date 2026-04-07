# CatMail 🐱

CatMail è un'applicazione Client-Server scritta in **Java** con interfaccia grafica basata su **JavaFX**. Simula un servizio di posta elettronica che permette agli utenti registrati (con dominio `@catmail.com`) di scambiarsi messaggi, leggere la posta in arrivo, rispondere, inoltrare ed eliminare le email.

Il progetto è suddiviso in due moduli principali: un **Client** (con architettura MVC) e un **Server** (multithreaded con gestione della concorrenza sui file JSON).

---

## Tecnologie utilizzate

* **Linguaggio:** Java
* **Interfaccia Grafica:** JavaFX (con file `.fxml` per la struttura visiva)
* **Networking:** Socket TCP/IP (`java.net.Socket`, `java.net.ServerSocket`)
* **Formato Dati:** JSON (tramite la libreria `org.json`)
* **Architettura:** Client-Server, MVC (Model-View-Controller) sul Client
* **Gestione Dati:** File di testo locali (`users.txt` per l'autenticazione, `{username}.json` per lo storage delle mail) gestiti tramite percorsi relativi.

---

## Funzionalità principali

### Lato Client
* **Autenticazione:** Accesso protetto con validazione tramite Regex per il dominio `@catmail.com`.
* **Inbox in tempo reale:** La lista delle email si aggiorna automaticamente grazie a un task schedulato (polling tramite `ScheduledExecutorService` ogni secondo).
* **Composizione e invio:** Creazione di nuove email con mittente, destinatario (anche multipli), oggetto e corpo del testo.
* **Interazioni avanzate:** Possibilità di Rispondere, Rispondere a tutti e Inoltrare i messaggi ricevuti.
* **Notifiche UI:** Sistema di notifiche visive temporizzate (Pattern Singleton con `NotificationManager`) per avvisare della ricezione di nuove email.
* **Gestione stato server:** L'interfaccia indica visivamente se il server è online o offline.

### Lato Server
* **Server GUI:** Interfaccia grafica dedicata per avviare/stoppare il server e visualizzare i log delle operazioni in tempo reale.
* **Multithreading:** Ogni client connesso viene gestito da un thread separato (`ClientHandler`), permettendo la gestione simultanea di più utenti.
* **Gestione della concorrenza:** Implementazione di un sistema di lock basato su mappa (`Map<String, Object> fileLocks`) per garantire la thread-safety durante la lettura e la scrittura simultanea sui file JSON degli utenti.

---

## Struttura del Progetto

Il codice sorgente è organizzato nei seguenti percorsi relativi:

### Client
```text
client/src/main/java/com/progiii/client/client/
├── Client.java                     # Main class per l'avvio della GUI client
├── controllers/                    # Gestione eventi UI
│   ├── InboxController.java        # Gestione della schermata principale
│   ├── LoginController.java        # Gestione accesso utente
│   ├── MessageController.java      # Composizione e invio
│   └── ReplyController.java        # Risposta e inoltro messaggi
├── models/                         # Logica di business e comunicazione socket
│   ├── InboxModel.java             # Fetching ed eliminazione email
│   ├── LoginModel.java             # Validazione credenziali
│   └── MessageModel.java           # Invio payload JSON al server
└── utils/
    └── NotificationManager.java    # Singleton per le notifiche pop-up
```
### Server
```text
server/src/main/java/com/progiii/demo/demo/
├── Server.java                     # Main class per l'avvio della GUI server
├── controllers/
│   └── ServerController.java       # Controllo interfaccia log server
├── models/
│   └── ClientHandler.java          # Thread worker per processare le richieste client
└── network/
    └── MailServer.java             # Listener dei socket sulla porta 3000 e setup lock
```

## Protocollo di comunicazione

Il server e i client comunicano scambiandosi messaggi in formato **JSON** sulla porta `3000`. I tipi di richiesta supportati (`type`) sono:

1.  `LOGIN`: Invia l'email dell'utente. Il server risponde con `SUCCESS` se presente nel DB (`users.txt`), altrimenti `ERROR`. *Nota: trattandosi di un progetto universitario, l'autenticazione è volutamente semplificata e avviene solo tramite verifica dell'email, senza richiedere una password.*
2.  `PING`: Richiesta periodica inviata dal Client per ottenere la lista aggiornata delle email con stato "DA LEGGERE". Il server restituisce un `JSONArray`.
3.  `EMAIL`: Trasmissione di un nuovo messaggio. Contiene mittente, destinatario/i, oggetto, corpo, timestamp e un ID univoco (generato tramite hash).
4.  `ELIMINA`: Richiede l'eliminazione di un'email specifica (tramite `email_id`) dal file JSON dell'utente.

---

## Come avviare il progetto

### Prerequisiti
* JDK 17 o superiore installato.
* JavaFX SDK configurato nel progetto (o gestito tramite Maven/Gradle).
* Libreria `org.json` aggiunta alle dipendenze.

### Esecuzione
1.  **Avvia il Server:** Esegui il file `Server.java` situato nel modulo server. Clicca su "Start" nell'interfaccia grafica per mettere il server in ascolto sulla porta 3000. L'applicazione creerà e gestirà i file JSON degli utenti in automatico sfruttando percorsi relativi.
2.  **Avvia i Client:** Esegui una o più istanze del file `Client.java` nel modulo client per simulare diversi utenti contemporaneamente.
3.  **Accesso:** Effettua il login con una delle email registrate nel file `users.txt` e inizia a scambiare messaggi!

