# KKBN – aplikacja mobilna Krajowej Konferencji Badań Nieniszczących

Oficjalna aplikacja na Androida dla uczestników **Krajowej Konferencji Badań Nieniszczących (KKBN)**. Zbiera w jednym miejscu agendę, aktualności, informacje o komitecie naukowym, partnerach i wystawcach, a zalogowanym uczestnikom pozwala zapisywać się na warsztaty.

<a href="https://play.google.com/store/apps/details?id=pl.directsoftware.kkbn">
  <img alt="Pobierz z Google Play" height="60" src="https://play.google.com/intl/en_us/badges/static/images/badges/pl_badge_web_generic.png">
</a>
## Funkcje

**Dla wszystkich użytkowników**

- **Agenda** – program konferencji z podziałem na dni i szczegółami wydarzeń
- **Aktualności** – lista wpisów i widok pojedynczego wpisu
- **Komitet naukowy**
- **Partnerzy** i **Wystawcy** – lista oraz szczegóły z linkiem do strony firmy
- **Miejsce** – informacje o lokalizacji konferencji (m.in. plan hotelu w PDF)
- **Kontakty**
- **Materiały** – pliki do pobrania otwierane bezpośrednio w aplikacji
  **Dla zalogowanych uczestników**

- Logowanie adresem e-mail i 6-znakowym kodem dostępu
- **Moje konto** – dane uczestnika i lista warsztatów, na które jest zapisany
- Zapisywanie się na warsztaty i rezygnacja z udziału (z kontrolą liczby wolnych miejsc)
  **Dla organizatorów** (w zależności od uprawnień konta)

- **Zarządzanie warsztatami** – edycja tytułu, godzin, prowadzącego i limitu uczestników
- **Zarządzanie uczestnikami** – podgląd zapisanych osób, wypisywanie, aktywacja kont
- **Zarządzanie prowadzącymi** – aktywacja i dezaktywacja prowadzących
## Konto demo

Aby sprawdzić funkcje dostępne dla uczestnika, zaloguj się w aplikacji na konto demonstracyjne:

| Pole | Wartość |
| --- | --- |
| E-mail | `demo@kkbn.pl` |
| Kod | `7B404F` |

## Technologie

| Obszar | Rozwiązanie |
| --- | --- |
| Język | Kotlin 2.1 |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Sieć | Retrofit 2 + Gson, OkHttp (z cache odpowiedzi) |
| Obrazy | Coil 3 |
| Przechowywanie danych | Jetpack DataStore (Preferences) |
| Build | Gradle 8.7 (Kotlin DSL), Android Gradle Plugin 8.5, version catalog |
| Min. / docelowy SDK | 27 (Android 8.1) / 35 (Android 15) |

## API

Aplikacja korzysta z dedykowanego REST API, napisanego specjalnie na jej potrzeby. Backend nie jest częścią tego repozytorium. Adres bazowy jest zdefiniowany w `ApiClient.BASE_URL`:

```kotlin
// app/src/main/java/pl/directsoftware/kkbn/api/ApiClient.kt
const val BASE_URL = "https://kkbn-bkp.simptestcert.pl/api/"
```

Pliki multimedialne (np. obrazki aktualności) są serwowane z katalogu `uploads/` na tym samym serwerze.

### Format odpowiedzi

Każdy endpoint zwraca obiekt w tej samej kopercie:

```json
{
  "success": true,
  "data": { },
  "errors": null
}
```

### Uwierzytelnianie

Każde żądanie zawiera nagłówek `Authorization: Bearer <token>`, dodawany przez `AuthInterceptor`.

- Żądania publiczne korzystają ze stałego tokenu aplikacji (`ApiClient.BASE_TOKEN`).
- Po zalogowaniu (`POST authorize` z polami `email` i `code`) API zwraca typ konta, a żądania prywatne (`ApiPrivateClient`) są wysyłane z tokenem użytkownika.
- Dane sesji (e-mail, typ konta, token, identyfikator) są zapisywane w DataStore, więc użytkownik pozostaje zalogowany po ponownym uruchomieniu aplikacji.
### Endpointy publiczne (`ApiService`)

| Metoda | Ścieżka | Opis |
| --- | --- | --- |
| GET | `news` | Lista aktualności |
| GET | `news/{id}` | Pojedyncza aktualność |
| GET | `agenda` | Agenda konferencji (bez cache) |
| GET | `committee` | Komitet naukowy |
| GET | `partners` | Partnerzy |
| GET | `exhibitors` | Wystawcy |
| GET | `place` | Miejsce konferencji |
| GET | `downloads` | Materiały do pobrania |
| GET | `contacts` | Kontakty |
| POST | `authorize` | Logowanie (`email`, `code`) |

Odpowiedzi publiczne są buforowane lokalnie przez OkHttp na 5 minut (cache 10 MB).

### Endpointy wymagające zalogowania (`ApiPrivateService`)

| Metoda | Ścieżka | Opis |
| --- | --- | --- |
| GET | `profile` | Profil zalogowanego użytkownika |
| GET | `profile/participant-events/{eventId}/is-registered` | Czy użytkownik jest zapisany na warsztat |
| POST | `events/{id}/register` | Zapis na warsztat |
| POST | `events/{id}/unregister` | Rezygnacja z warsztatu |
| GET | `events` | Lista warsztatów (zarządzanie) |
| POST | `events/{id}` | Edycja warsztatu |
| GET | `events/{id}/participants` | Uczestnicy warsztatu |
| POST | `events/{eventId}/{participantId}/unregister` | Wypisanie uczestnika przez organizatora |
| GET | `event-hosts` | Lista prowadzących |
| POST | `event-hosts/{id}` | Aktywacja / dezaktywacja prowadzącego |
| GET | `event-participants` | Lista uczestników |
| POST | `event-participants/{id}` | Aktywacja / dezaktywacja uczestnika |

Żądania `POST` z danymi są wysyłane jako `application/x-www-form-urlencoded`.

### Typy kont

Typ konta zwracany przez API decyduje o tym, które ekrany zarządzania są dostępne (`EventMemberType`, `SessionManager.isGranted`):

| Kod | Typ | Uprawnienia |
| --- | --- | --- |
| -1 | `ANONYMOUS` | Treści publiczne |
| 1 | `PARTICIPANT` | Uczestnik – zapisy na warsztaty |
| 2 | `PARTICIPANT_MANAGEMENT` | Zarządzanie uczestnikami |
| 4 | `HOST` | Prowadzący |
| 8 | `HOST_MANAGEMENT` | Zarządzanie prowadzącymi |
| 16 | `MANAGEMENT` | Pełne zarządzanie |

## Struktura projektu

```
app/src/main/java/pl/directsoftware/kkbn/
├── api/            # Klienci Retrofit, interceptor autoryzacji
│   ├── data/       # Modele danych (News, Event, Partner, Profile…)
│   ├── http/       # Interfejsy endpointów (ApiService, ApiPrivateService)
│   └── response/   # Koperta odpowiedzi API
├── auth/           # Typy kont i role
├── common/         # Trasy nawigacji (Route) i elementy wspólne
├── repository/     # Repozytoria pobierające dane z API
├── ui/theme/       # Ekrany Compose i komponenty UI
├── utils/          # Sesja, ustawienia (DataStore), pliki, logowanie
├── App.kt
├── AppViewModel.kt
└── MainActivity.kt
```

## Uruchomienie projektu

### Wymagania

- Android Studio (Ladybug lub nowsze)
- JDK 17
- Android SDK 35
### Kroki

1. Sklonuj repozytorium:
```bash
git clone https://github.com/dominikgubala/kkbn-android.git
```

2. Otwórz katalog w Android Studio i poczekaj na synchronizację Gradle.
3. Skonfiguruj podpisywanie (patrz niżej).
4. Uruchom aplikację na emulatorze lub urządzeniu (Android 8.1+).
   Z linii poleceń:

```bash
./gradlew assembleDebug       # APK debug
./gradlew bundleRelease       # AAB do Google Play
```

### Podpisywanie

Konfiguracja `kkbn` w `app/build.gradle.kts` oczekuje keystore'a w:

```
~/.android/keystores/android_app.jks
```

Ta konfiguracja jest używana zarówno dla wersji `release`, jak i `debug`. Bez tego pliku build się nie powiedzie. Aby zbudować wersję deweloperską bez klucza produkcyjnego, usuń linię `signingConfig = …` z bloku `debug` – Android Studio użyje wtedy domyślnego klucza debug.
 