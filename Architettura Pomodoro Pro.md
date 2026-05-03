# Architettura Pomodoro Pro

## Panoramica

Pomodoro Pro è un'app Android moderna costruita con **Jetpack Compose** che implementa la tecnica Pomodoro con gestione dei task e monetizzazione tramite AdMob.

## Struttura del Progetto

```
pomodoro_pro_app/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/pomodorotimer/
│   │       │   ├── MainActivity.kt              # Activity principale
│   │       │   ├── PomodoroViewModel.kt         # Logica business
│   │       │   ├── data/
│   │       │   │   └── PomodoroPrefs.kt         # Repository dati
│   │       │   ├── ui/
│   │       │   │   ├── components/
│   │       │   │   │   └── PomodoroComponents.kt # Componenti UI riutilizzabili
│   │       │   │   └── theme/
│   │       │   │       ├── Theme.kt
│   │       │   │       └── Typography.kt
│   │       │   └── utils/
│   │       │       └── Constants.kt              # Costanti globali
│   │       ├── res/
│   │       │   ├── values/
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   └── xml/
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Pattern Architetturale

### 1. **MVVM (Model-View-ViewModel)**
- **Model**: `PomodoroPrefs` - Repository per la persistenza dati
- **ViewModel**: `PomodoroViewModel` - Gestione della logica business e dello state
- **View**: Composable UI components in `MainActivity` e `PomodoroComponents`

### 2. **Repository Pattern**
`PomodoroPrefs` implementa il repository pattern per:
- Astrarre l'accesso ai dati (SharedPreferences)
- Centralizzare la logica di persistenza
- Facilitare il testing e la manutenzione

### 3. **Separation of Concerns**
- **UI Layer**: Composable functions in `ui/components/`
- **Business Logic**: `PomodoroViewModel`
- **Data Layer**: `PomodoroPrefs`
- **Configuration**: `Constants`

## Componenti Principali

### MainActivity.kt
- Entry point dell'applicazione
- Inizializza Mobile Ads
- Crea il ViewModel e il Repository
- Configura il tema Compose

### PomodoroViewModel.kt
- Gestisce lo state del timer (timeLeft, isRunning, isBreak)
- Gestisce la lista dei task
- Implementa la logica del timer con Coroutines
- Gestisce i messaggi di errore
- Persiste i dati tramite il Repository

### PomodoroPrefs.kt (Repository)
- Gestisce la persistenza tramite SharedPreferences
- Implementa il reset giornaliero del contatore
- Fornisce error handling e logging
- Metodi: `getPomodorosToday()`, `incrementPomodoros()`, `saveTasks()`, `getTasks()`

### PomodoroComponents.kt
Componenti UI riutilizzabili:
- `TimerDisplay` - Visualizza il timer e lo stato (FOCUS/PAUSA)
- `TimerControls` - Pulsanti di controllo
- `StatsCard` - Statistiche giornaliere
- `TasksSection` - Sezione dei task
- `TaskInputField` - Campo input per singolo task
- `AdBanner` - Banner AdMob
- `ErrorMessage` - Visualizzazione errori

### Constants.kt
Centralizza tutte le costanti:
- Durate timer (25 min focus, 5 min break)
- Chiavi SharedPreferences
- ID AdMob (test)
- Dimensioni UI
- Formato data

## Flusso Dati

```
User Input
    ↓
MainActivity/Composable
    ↓
PomodoroViewModel
    ↓
PomodoroPrefs (Repository)
    ↓
SharedPreferences
```

## Gestione dello State

### State Privato nel ViewModel
```kotlin
private val _timeLeft = mutableStateOf(...)
val timeLeft: State<Int> = _timeLeft
```

Questo pattern garantisce:
- Incapsulamento: solo il ViewModel può modificare lo state
- Reattività: i Composable si aggiornano automaticamente
- Prevedibilità: source of truth unico

## Error Handling

- Try-catch blocks in tutti i metodi critici
- Logging con Android Log API
- Messaggi di errore visibili all'utente
- Graceful degradation in caso di fallimenti

## Logging

Tutti i componenti implementano logging:
- `PomodoroViewModel` - Logica business
- `PomodoroPrefs` - Operazioni dati
- `MainActivity` - Ciclo vita

Tag di log: `PomodoroViewModel`, `PomodoroPrefs`, `MainActivity`

## Ottimizzazioni

### Build Gradle
- Minification abilitata in release
- Resource shrinking in release
- Compose BOM per gestire versioni
- Versioni dipendenze aggiornate

### Performance
- Coroutines per operazioni async
- Lazy composition dei Composable
- Proper state management per evitare recomposizioni inutili

## Sicurezza

### AndroidManifest.xml
- Permessi minimi richiesti (INTERNET, ACCESS_NETWORK_STATE)
- Data extraction rules configurate
- Backup rules configurate

### SharedPreferences
- Modalità privata (MODE_PRIVATE)
- Separazione dati per app

## Testing

Struttura preparata per:
- Unit tests (PomodoroViewModel, PomodoroPrefs)
- Instrumented tests (UI components)
- Test framework: JUnit, Espresso

## Miglioramenti Futuri

1. **Database**: Migrare da SharedPreferences a Room Database per dati complessi
2. **Dependency Injection**: Aggiungere Hilt per DI
3. **Navigation**: Aggiungere Jetpack Navigation per multi-screen
4. **Analytics**: Integrare Firebase Analytics
5. **Notifications**: Aggiungere notifiche push per reminder
6. **Sync**: Sincronizzazione cloud dei dati
7. **Themes**: Supporto dark mode completo
8. **Accessibility**: Migliorare accessibilità per utenti disabili

## Versioni Dipendenze

- **Android SDK**: compileSdk 34, minSdk 24, targetSdk 34
- **Kotlin**: 1.9.10
- **Compose**: 2023.10.00
- **Material3**: Latest
- **Coroutines**: 1.7.3
- **Google Play Services (Ads)**: 22.6.0

## Convenzioni di Codice

- **Naming**: camelCase per variabili, PascalCase per classi
- **Comments**: Documentazione KDoc per funzioni pubbliche
- **Logging**: Tag di log per ogni classe
- **Error Handling**: Try-catch con logging
- **State Management**: Immutable public state, mutable private state

## Deployment

### Release Build
```bash
./gradlew assembleRelease
```

### Debug Build
```bash
./gradlew assembleDebug
```

### Firma APK
Configurare `signingConfigs` in `build.gradle` per la firma automatica.
