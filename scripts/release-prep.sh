#!/bin/bash
set -euo pipefail

# ═══════════════════════════════════════════════════════════════
#  release-prep.sh — Preparazione automatica alla pubblicazione
#  Pomodoro Pro
# ═══════════════════════════════════════════════════════════════

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

log()  { echo -e "${CYAN}[$(date +%H:%M:%S)]${NC} $1"; }
ok()   { echo -e "  ${GREEN}✓${NC} $1"; }
warn() { echo -e "  ${YELLOW}⚠${NC} $1"; }
fail() { echo -e "  ${RED}✗${NC} $1"; }

echo ""
echo -e "${CYAN}══════════════════════════════════════════════════${NC}"
echo -e "${CYAN}  Pomodoro Pro — Release Preparation Script${NC}"
echo -e "${CYAN}══════════════════════════════════════════════════${NC}"
echo ""

# ──────────────────────────────────────────────────────────────
#  STEP 0: Verifica prerequisiti
# ──────────────────────────────────────────────────────────────
log "STEP 0: Verifica prerequisiti..."

PREREQ_OK=true

# Java
if command -v java &>/dev/null; then
    JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    ok "Java trovato: $(java -version 2>&1 | head -1)"
else
    fail "Java non trovato. Installa JDK 17+."
    PREREQ_OK=false
fi

# keytool
if command -v keytool &>/dev/null; then
    ok "keytool trovato"
else
    fail "keytool non trovato (dovrebbe essere incluso nel JDK)."
    PREREQ_OK=false
fi

# Gradle wrapper
if [ -f gradlew ]; then
    ok "Gradle wrapper trovato"
else
    fail "gradlew non trovato. Esegui: gradle wrapper --gradle-version 8.2"
    PREREQ_OK=false
fi

# ANDROID_HOME
if [ -n "${ANDROID_HOME:-}" ]; then
    ok "ANDROID_HOME = $ANDROID_HOME"
else
    warn "ANDROID_HOME non impostato. Imposta la variabile o specifica sdk.dir in local.properties."
fi

if [ "$PREREQ_OK" = false ]; then
    echo ""
    fail "Prerequisiti insufficienti. Correggi gli errori sopra e riprova."
    exit 1
fi

echo ""

# ──────────────────────────────────────────────────────────────
#  STEP 1: Keystore
# ──────────────────────────────────────────────────────────────
log "STEP 1: Generazione keystore..."

KEYSTORE_FILE="$PROJECT_DIR/app/pomodoro_pro_keystore.jks"

if [ -f "$KEYSTORE_FILE" ]; then
    ok "Keystore già esistente: $KEYSTORE_FILE"
    # Leggi alias dal keystore
    KEY_ALIAS_EXISTING=$(keytool -list -keystore "$KEYSTORE_FILE" -storepass "$KEYSTORE_PASSWORD" 2>/dev/null | grep -oP '^\w+(?=,)' | head -1 || echo "")
    if [ -n "$KEY_ALIAS_EXISTING" ]; then
        ok "Alias trovato nel keystore: $KEY_ALIAS_EXISTING"
    fi
else
    echo ""
    echo -e "  ${YELLOW}Il keystore non esiste. Lo genero adesso.${NC}"
    echo ""

    # Chiedi le credenziali
    read -rsp "  Scegli una password per il keystore: " STORE_PASS
    echo ""
    read -rsp "  Conferma password: " STORE_PASS_CONFIRM
    echo ""

    if [ "$STORE_PASS" != "$STORE_PASS_CONFIRM" ]; then
        fail "Le password non coincidono."
        exit 1
    fi

    read -rp "  Alias della chiave (default: pomodoro_pro_key): " KEY_ALIAS_INPUT
    KEY_ALIAS="${KEY_ALIAS_INPUT:-pomodoro_pro_key}"

    echo ""
    echo -e "  ${YELLOW}Ora inserisci i dati anagrafici per il certificato:${NC}"
    read -rp "  Nome e Cognome: " CERT_CN
    read -rp "  Unità organizzativa: " CERT_OU
    read -rp "  Organizzazione: " CERT_O
    read -rp "  Città/Località: " CERT_L
    read -rp "  Provincia/Stato: " CERT_ST
    read -rp "  Codice paese (2 lettere, es. IT): " CERT_C

    echo ""
    echo -e "  ${YELLOW}Genero il keystore...${NC}"

    keytool -genkey -v \
        -keystore "$KEYSTORE_FILE" \
        -alias "$KEY_ALIAS" \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -storepass "$STORE_PASS" \
        -keypass "$STORE_PASS" \
        -dname "CN=$CERT_CN, OU=$CERT_OU, O=$CERT_O, L=$CERT_L, ST=$CERT_ST, C=$CERT_C" \
        2>&1 | sed 's/^/  /'

    ok "Keystore generato: $KEYSTORE_FILE"

    # Esporta le variabili
    export KEYSTORE_PASSWORD="$STORE_PASS"
    export KEY_ALIAS="$KEY_ALIAS"
    export KEY_PASSWORD="$STORE_PASS"

    # Offri di salvare in .env
    echo ""
    read -rp "  Salvare le credenziali in .env.local? (s/N): " SAVE_ENV
    if [[ "$SAVE_ENV" =~ ^[Ss]$ ]]; then
        cat > "$PROJECT_DIR/.env.local" << EOF
# Pomodoro Pro — Credenziali keystore (NON committare questo file!)
KEYSTORE_PASSWORD=$STORE_PASS
KEY_ALIAS=$KEY_ALIAS
KEY_PASSWORD=$STORE_PASS
EOF
        chmod 600 "$PROJECT_DIR/.env.local"
        ok ".env.local creato (protetto: chmod 600)"
        echo -e "  ${YELLOW}Aggiungi al tuo .gitignore: .env.local${NC}"
    fi
fi

echo ""

# ──────────────────────────────────────────────────────────────
#  STEP 2: ID AdMob
# ──────────────────────────────────────────────────────────────
log "STEP 2: Configurazione ID AdMob..."

BUILD_FILE="$PROJECT_DIR/app/build.gradle"

# Controlla se ci sono ancora placeholder
if grep -q "YOUR_APP_ID\|YOUR_BANNER_ID\|ca-app-pub-3940256099942544" "$BUILD_FILE"; then
    echo ""
    echo -e "  ${YELLOW}Rilevati ID AdMob di test/placeholder.${NC}"
    echo "  Vai su https://apps.admob.com per creare la tua app e ottenere gli ID reali."
    echo ""

    read -rp "  Inserisci l'App ID AdMob (es. ca-app-pub-1234567890123456~1234567890): " ADMOB_APP_ID
    read -rp "  Inserisci il Banner Ad Unit ID (es. ca-app-pub-1234567890123456/1234567890): " ADMOB_BANNER_ID

    if [ -n "$ADMOB_APP_ID" ] && [ -n "$ADMOB_BANNER_ID" ]; then
        # Sostituisci nel build.gradle
        sed -i "s|\"ca-app-pub-3940256099942544~3347511713\"|\"$ADMOB_APP_ID\"|g" "$BUILD_FILE"
        sed -i "s|\"ca-app-pub-3940256099942544/6300978111\"|\"$ADMOB_BANNER_ID\"|g" "$BUILD_FILE"
        ok "ID AdMob aggiornati in app/build.gradle"
    else
        warn "ID non inseriti. Dovrai modificarli manualmente in app/build.gradle."
    fi
else
    ok "ID AdMob già configurati (nessun placeholder rilevato)"
fi

echo ""

# ──────────────────────────────────────────────────────────────
#  STEP 3: Icone
# ──────────────────────────────────────────────────────────────
log "STEP 3: Verifica icone..."

ICON_COUNT=$(find "$PROJECT_DIR/app/src/main/res" -name "ic_launcher*.png" 2>/dev/null | wc -l)
ADAPTIVE_COUNT=$(find "$PROJECT_DIR/app/src/main/res" -name "ic_launcher*.xml" 2>/dev/null | wc -l)

if [ "$ICON_COUNT" -ge 10 ] && [ "$ADAPTIVE_COUNT" -ge 2 ]; then
    ok "Icone trovate: $ICON_COUNT PNG + $ADAPTIVE_COUNT XML"
    echo ""
    echo -e "  ${YELLOW}Le icone attuali sono placeholder generici (rosa solidi).${NC}"
    echo "  Per sostituirle con icone professionali:"
    echo ""
    echo "    Android Studio → tasto destro su res/ → New → Image Asset"
    echo "    Carica un'immagine 512×512 px e genera tutte le densità."
    echo ""
    read -rp "  Sostituire le icone adesso? (s/N): " REPLACE_ICONS
    if [[ "$REPLACE_ICONS" =~ ^[Ss]$ ]]; then
        echo -e "  ${YELLOW}Apri Android Studio e usa Asset Studio per generare le icone.${NC}"
        echo -e "  ${YELLOW}Poi torna qui e premi Invio per continuare...${NC}"
        read -r
    fi
else
    warn "Icone mancanti! Trovati: $ICON_COUNT PNG, $ADAPTIVE_COUNT XML"
    echo "  Esegui Android Studio → Asset Studio per generare le icone."
fi

echo ""

# ──────────────────────────────────────────────────────────────
#  STEP 4: Version bump
# ──────────────────────────────────────────────────────────────
log "STEP 4: Versione dell'app..."

CURRENT_VERSION_CODE=$(grep 'versionCode' "$BUILD_FILE" | head -1 | grep -oP '\d+')
CURRENT_VERSION_NAME=$(grep 'versionName' "$BUILD_FILE" | head -1 | grep -oP '"[^"]*"' | tr -d '"')

echo ""
echo -e "  Versione attuale: ${CYAN}$CURRENT_VERSION_NAME${NC} (code: ${CYAN}$CURRENT_VERSION_CODE${NC})"
echo ""

read -rp "  Nuovo versionName (lascia vuoto per mantenere $CURRENT_VERSION_NAME): " NEW_VERSION_NAME
NEW_VERSION_NAME="${NEW_VERSION_NAME:-$CURRENT_VERSION_NAME}"

read -rp "  Nuovo versionCode (lascia vuoto per $((CURRENT_VERSION_CODE + 1))): " NEW_VERSION_CODE
NEW_VERSION_CODE="${NEW_VERSION_CODE:-$((CURRENT_VERSION_CODE + 1))}"

sed -i "s/versionCode $CURRENT_VERSION_CODE/versionCode $NEW_VERSION_CODE/" "$BUILD_FILE"
sed -i "s/versionName \"$CURRENT_VERSION_NAME\"/versionName \"$NEW_VERSION_NAME\"/" "$BUILD_FILE"

ok "Versione aggiornata: $NEW_VERSION_NAME (code: $NEW_VERSION_CODE)"

echo ""

# ──────────────────────────────────────────────────────────────
#  STEP 5: Build
# ──────────────────────────────────────────────────────────────
log "STEP 5: Build di release..."

echo ""
echo -e "  ${YELLOW}Avvio la build firmata...${NC}"
echo ""

# Carica .env.local se esiste
if [ -f "$PROJECT_DIR/.env.local" ]; then
    set -a
    source "$PROJECT_DIR/.env.local"
    set +a
    ok "Credenziali caricate da .env.local"
fi

# Verifica che le variabili d'ambiente siano impostate
if [ -z "${KEYSTORE_PASSWORD:-}" ]; then
    fail "KEYSTORE_PASSWORD non impostata."
    echo "  Esportala con: export KEYSTORE_PASSWORD=..."
    echo "  Oppure crea un file .env.local (vedi STEP 1)."
    exit 1
fi

./gradlew clean assembleRelease 2>&1 | while IFS= read -r line; do
    echo "  $line"
done

APK_PATH="$PROJECT_DIR/app/build/outputs/apk/release/app-release.apk"
AAB_PATH="$PROJECT_DIR/app/build/outputs/bundle/release/app-release.aab"

echo ""

if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    ok "APK firmato generato: $APK_PATH ($APK_SIZE)"
else
    fail "APK non trovato. La build potrebbe essere fallita."
fi

if [ -f "$AAB_PATH" ]; then
    AAB_SIZE=$(du -h "$AAB_PATH" | cut -f1)
    ok "AAB firmato generato: $AAB_PATH ($AAB_SIZE)"
fi

echo ""
echo -e "${GREEN}══════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  Release pronta!${NC}"
echo -e "${GREEN}══════════════════════════════════════════════════${NC}"
echo ""
echo "  APK: $APK_PATH"
echo "  AAB: $AAB_PATH"
echo ""
echo "  Carica su Google Play Console:"
echo "    https://play.google.com/console/"
echo ""
