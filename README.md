# Pomodoro Pro

Un'app Android moderna e leggera per la gestione della tecnica Pomodoro, costruita con **Jetpack Compose** e **Kotlin**.

## Caratteristiche

- ⏱️ **Timer Pomodoro**: Sessioni di focus da 25 minuti e pause da 5 minuti
- 📝 **Gestione Task**: Fino a 3 task personalizzabili per ogni giorno
- 📊 **Statistiche**: Contatore giornaliero dei pomodori completati
- 💾 **Persistenza Locale**: Salvataggio automatico con reset a mezzanotte
- 📱 **UI Moderna**: Interfaccia intuitiva con Material Design 3
- 💰 **Monetizzazione**: Banner AdMob integrato (con test ID)
- 🎨 **Tema Personalizzato**: Design pulito e minimalista

## Requisiti

- **Android Studio**: Hedgehog (2023.1.1) o superiore
- **Android SDK**: API 34 (target), API 24 (minimo)
- **Kotlin**: 1.9.10 o superiore
- **Java**: JDK 17 o superiore
- **Gradle**: 8.1.2 o superiore

## Installazione

### 1. Clonare il repository
```bash
git clone <repository-url>
cd pomodoro_pro_app
```

### 2. Aprire in Android Studio
```bash
# Android Studio dovrebbe riconoscere automaticamente il progetto
# In alternativa: File > Open > selezionare la cartella del progetto
```

### 3. Sincronizzare Gradle
```bash
# Android Studio sincronizzerà automaticamente
# Oppure da terminale:
./gradlew sync
```

### 4. Eseguire l'app
```bash
# Su emulatore o dispositivo collegato
./gradlew installDebug

# Oppure tramite Android Studio: Run > Run 'app'
```

## Architettura

L'app segue il pattern **MVVM** con **Separation of Concerns**:

```
UI Layer (Composable)
        ↓
ViewModel (Business Logic)
        ↓
Repository (Data Access)
        ↓
SharedPreferences (Local Storage)
```

### Struttura Directory

```
app/src/main/java/com/example/pomodorotimer/
├── MainActivity.kt              # Activity principale
├── PomodoroViewModel.kt         # Logica business
├── data/
│   └── PomodoroPrefs.kt         # Repository
├── ui/
│   ├── components/
│   │   └── PomodoroComponents.kt # Componenti UI
│   └── theme/
│       ├── Theme.kt
│       └── Typography.kt
└── utils/
    └── Constants.kt             # Costanti globali
```

Per documentazione dettagliata dell'architettura, vedi [ARCHITECTURE.md](ARCHITECTURE.md).

## Utilizzo

### Avviare una Sessione di Focus
1. Premi il pulsante **"Start 25 min"**
2. Il timer inizia il conto alla rovescia
3. Lavora concentrato fino al suono di fine sessione

### Fare una Pausa
1. Premi il pulsante **"Break 5 min"**
2. Rilassati durante i 5 minuti di pausa
3. Il timer si resetta automaticamente

### Gestire i Task
1. Inserisci i tuoi task nei campi di testo
2. I task vengono salvati automaticamente
3. I dati persistono tra le sessioni

### Visualizzare le Statistiche
- Visualizza il numero di pomodori completati nella card inferiore
- Il contatore si resetta automaticamente a mezzanotte

## Configurazione AdMob

L'app utilizza **Test ID di AdMob** per lo sviluppo:
- **Banner ID**: `ca-app-pub-3940256099942544/6300978111`
- **App ID**: `ca-app-pub-3940256099942544~3347511713`

### Per la Produzione

1. Registrati su [Google AdMob](https://admob.google.com)
2. Crea una proprietà e ottieni i tuoi ID
3. Aggiorna i valori in `Constants.kt`:
   ```kotlin
   const val ADMOB_BANNER_TEST_ID = "ca-app-pub-xxxxxxxxxxxxxxxx/xxxxxxxxxx"
   const val ADMOB_APP_TEST_ID = "ca-app-pub-xxxxxxxxxxxxxxxx~xxxxxxxxxx"
   ```
4. Aggiorna anche `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.gms.ads.APPLICATION_ID"
       android:value="ca-app-pub-xxxxxxxxxxxxxxxx~xxxxxxxxxx"/>
   ```

## Build e Release

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Configurare la Firma

Crea un file `keystore.properties` nella root del progetto:
```properties
storeFile=path/to/keystore.jks
storePassword=your_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Aggiungi a `app/build.gradle`:
```gradle
signingConfigs {
    release {
        storeFile file(keystoreProperties['storeFile'])
        storePassword keystoreProperties['storePassword']
        keyAlias keystoreProperties['keyAlias']
        keyPassword keystoreProperties['keyPassword']
    }
}
```

## Dipendenze Principali

- **androidx.compose**: UI framework moderno
- **androidx.lifecycle**: Lifecycle management
- **kotlinx.coroutines**: Async programming
- **com.google.android.gms**: Google Play Services (AdMob)
- **androidx.preference**: SharedPreferences wrapper

Per la lista completa, vedi `app/build.gradle`.

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Troubleshooting

### Errore: "Gradle sync failed"
- Verifica che Java 17 sia installato
- Esegui `./gradlew clean`
- Sincronizza di nuovo in Android Studio

### Errore: "AdMob banner non carica"
- Verifica che il dispositivo/emulatore abbia connessione internet
- Controlla che gli ID AdMob siano corretti
- In debug, gli ID test dovrebbero funzionare sempre

### Timer non funziona
- Verifica che il ViewModel sia inizializzato correttamente
- Controlla i log: `adb logcat | grep PomodoroViewModel`

### Task non vengono salvati
- Verifica che l'app abbia permessi di scrittura
- Controlla `SharedPreferences` in Android Studio Device Explorer

## Logging

L'app implementa logging completo per debug:

```bash
# Visualizza tutti i log
adb logcat

# Filtra per tag specifico
adb logcat | grep "PomodoroViewModel"
adb logcat | grep "PomodoroPrefs"
adb logcat | grep "MainActivity"
```

## Contribuire

Per contribuire al progetto:

1. Fork il repository
2. Crea un branch per la feature (`git checkout -b feature/AmazingFeature`)
3. Commit i cambiamenti (`git commit -m 'Add some AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Apri una Pull Request

## Roadmap

- [ ] Supporto Dark Mode completo
- [ ] Notifiche push per reminder
- [ ] Sincronizzazione cloud (Firebase)
- [ ] Statistiche avanzate e grafici
- [ ] Temi personalizzabili
- [ ] Supporto multi-lingua
- [ ] Widget home screen
- [ ] Esportazione dati (CSV, PDF)

## License

Questo progetto è distribuito sotto la licenza MIT. Vedi il file [LICENSE](LICENSE) per i dettagli.

## Supporto

Per segnalare bug o richiedere feature:
- Apri un Issue su GitHub
- Contatta lo sviluppatore

## Autore

Sviluppato con ❤️ per la comunità Android

---

**Versione**: 1.0  
**Ultimo aggiornamento**: 2024  
**Stato**: Attivo e in sviluppo
