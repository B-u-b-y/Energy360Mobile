# Energy360

Energy360 è un'applicazione Android sviluppata per la gestione del benessere fisico, il monitoraggio delle attività e la prenotazione di servizi sportivi. L'app integra funzionalità avanzate come il tracciamento dei passi tramite sensori hardware, la persistenza locale dei dati e la sincronizzazione in tempo reale con Firebase.

## 🚀 Come Iniziare

### Prerequisiti
* **Android Studio** (versione Koala o successiva consigliata)
* **JDK 17** o superiore
* Un account **Firebase** (per la sincronizzazione cloud)

### Clonazione e Avvio
1. **Clona il repository:**
   ```bash
   git clone https://github.com/B-u-b-y/Energy360Mobile.git
   ```
2. **Importa il progetto:**
   Apri Android Studio e seleziona `File > Open...`, quindi scegli la cartella del progetto clonata.
3. **Configura Firebase:**
   * Crea un progetto nella [Console di Firebase](https://console.firebase.google.com/).
   * Scarica il file `google-services.json` e inseriscilo nella cartella `app/`.
   * Abilita **Authentication** e **Realtime Database** (o Firestore, a seconda dell'implementazione).
4. **Esegui l'app:**
   Collega un dispositivo fisico o avvia un emulatore e clicca sul tasto **Run** in Android Studio.

---

## 🏗️ Architettura e Componenti

L'applicazione segue i pattern di sviluppo moderni di Android, utilizzando **Jetpack Compose** per l'interfaccia utente e l'architettura **MVVM** (Model-View-ViewModel).

### Componenti Grafici (UI)
* **Home Page**: Dashboard principale che mostra il riepilogo delle attività e lo stato dell'utente.
* **Bottom Bar**: Navigazione rapida tra le sezioni principali (Home, Ricerca, Prenotazioni, Profilo, Messaggi).
* **Training & Exercises**: Interfaccia dedicata alla visualizzazione e gestione degli allenamenti.
* **Services & Reservations**: Schermate per la consultazione dei servizi disponibili e la gestione delle prenotazioni effettuate.
* **User Profile**: Gestione delle informazioni personali dell'utente.
* **Chat & Messaging**: Sistema di messaggistica integrato per la comunicazione.
* **Collektive (Nearby Features)**: Funzionalità sperimentale basata su programmazione aggregata per rilevare dispositivi vicini e gestire sfide di comunità (es. conteggio passi collettivo nel raggio di 100m) in modo decentralizzato.
* **Offline Support**: Schermate dedicate (`noConnection`) che permettono la visualizzazione dei dati memorizzati localmente anche in assenza di rete.
* **Auth (Login/Registration)**: Gestione dell'accesso e della registrazione degli utenti.

### Componenti Non Grafici (Logica e Dati)
* **ViewModels**: Gestiscono la logica di business e mantengono lo stato della UI (es. `UserViewModel`, `StepsViewModel`, `ReservationsViewModel`).
* **Databases (Room)**: Utilizzato per la persistenza locale dei dati (User, Steps, Services, Exercises, Reservations) garantendo il funzionamento offline.
* **Firebase Data Source**: Gestisce la sincronizzazione dei dati con il cloud per il backup e l'uso multi-dispositivo.
* **Sensor Integration**: Utilizza il `SensorManager` di Android per interfacciarsi con il sensore di passi (`TYPE_STEP_COUNTER`) del dispositivo.
* **Navigation (NavGraph)**: Gestione centralizzata del flusso di navigazione tra le diverse schermate tramite `NavHost`.

---

## 🛠️ Tecnologie Utilizzate
* **Kotlin**: Linguaggio di programmazione primario.
* **Jetpack Compose**: Toolkit moderno per la creazione di UI native.
* **Room Database**: Astrazione su SQLite per il salvataggio locale.
* **Firebase**: Per autenticazione e database remoto.
* **Kotlin Coroutines & Flow**: Per la gestione dei flussi di dati asincroni.

---

## 👥 Sviluppatori
* Budaes Tanaru Riccardo Gabriele & Andrea Driza - *Energy360 Project*
