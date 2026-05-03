# Changelog

Tutte le modifiche notevoli a questo progetto saranno documentate in questo file.

## [1.1.0] - 2024 (Refactoring)

### Aggiunte
- **Constants.kt**: Centralizzazione di tutte le costanti dell'app
- **PomodoroComponents.kt**: Componenti UI riutilizzabili e ben organizzati
- **Error Handling**: Gestione errori completa con messaggi visibili all'utente
- **Logging**: Logging strutturato in tutti i componenti
- **XML Resources**: File `data_extraction_rules.xml` e `backup_rules.xml`
- **ARCHITECTURE.md**: Documentazione completa dell'architettura
- **Migliorato README.md**: Guida completa con troubleshooting

### Modifiche
- **PomodoroViewModel.kt**: 
  - Aggiunto error state management
  - Migliorato logging
  - Aggiunto cleanup in `onCleared()`
  - Migliorata gestione delle eccezioni
  
- **PomodoroPrefs.kt**:
  - Implementato Repository Pattern
  - Aggiunto logging completo
  - Migliorata gestione errori
  - Aggiunto metodo `clearAllData()`
  
- **MainActivity.kt**:
  - Separazione della logica UI dai componenti
  - Migliorata leggibilità
  - Aggiunto error handling
  
- **build.gradle**:
  - Aggiornate versioni dipendenze
  - Abilitato minification e resource shrinking in release
  - Aggiornato Kotlin Compiler Extension Version

### Rimosso
- Logica UI mescolata in MainActivity
- Hardcoded values sparsi nel codice
- Mancanza di error handling

### Risolto
- File XML mancanti per backup/data extraction rules
- Architettura non separata
- State management fragile
- Mancanza di logging e error handling

## [1.0.0] - 2024 (Release Iniziale)

### Aggiunte
- Timer Pomodoro (25 min focus, 5 min break)
- Gestione task (3 task per giorno)
- Persistenza dati con SharedPreferences
- Reset automatico a mezzanotte
- Integrazione AdMob Banner
- UI con Jetpack Compose
- Material Design 3 Theme

### Caratteristiche
- MVVM Architecture
- Jetpack Compose UI
- Kotlin Coroutines
- SharedPreferences persistence
- AdMob monetization

---

## Convenzioni di Versioning

Questo progetto segue [Semantic Versioning](https://semver.org/):
- **MAJOR**: Cambiamenti incompatibili
- **MINOR**: Nuove funzionalità compatibili
- **PATCH**: Bug fixes

## Roadmap Futuro

### v1.2.0
- [ ] Dark Mode support
- [ ] Notifiche push
- [ ] Statistiche avanzate

### v1.3.0
- [ ] Firebase Cloud Sync
- [ ] Multi-language support
- [ ] Widget home screen

### v2.0.0
- [ ] Room Database
- [ ] Dependency Injection (Hilt)
- [ ] Advanced analytics
