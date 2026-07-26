# Vartovyi (Вартовий)

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="200">](https://play.google.com/store/apps/details?id=com.revakovskyi.vartovyi)

Vartovyi — background monitoring of Telegram notifications with a full-screen alarm whenever an
incoming message matches your triggers. Built for night watch duty during air raid alerts in
Ukraine.

> `google-services.json` is not committed to the repository — a local build requires your own
> Firebase project.

## 1) Purpose

`Vartovyi` is a utility tool for night / background watch duty:

- reads incoming Telegram notifications through `NotificationListenerService`;
- analyzes the text against the `trigger` and `stop word` lists;
- raises a loud (full-screen) alarm only on a relevant match;
- keeps a journal of events;
- keeps running reliably in the background, including after a device restart.

## 2) What it does

- Monitors notifications **only** from Telegram clients (official, web, Telegram X, Neko X).
- Supports three types of trigger rules, stop words, and an optional Telegram channel allow-list.
- Raises the alarm via `AlarmService` + `AlarmActivity` over the lock screen (sound, volume,
  duration and vibration are configurable).
- Cooldown after an alarm: repeated matches inside the window do not raise a new alarm, they are
  written to the journal with the `SKIPPED_COOLDOWN` status.
- Shows a persistent foreground notification while monitoring is active.
- Restores monitoring after a reboot (`BOOT_COMPLETED`) plus a periodic WorkManager watchdog
  (every 15 minutes).
- Deduplicates repeated Telegram notifications about the same message (see §9).
- Exports / imports triggers as JSON (merge or replace), and can reset the app to factory defaults.
- Is controlled through the screens: `Home`, `Triggers`, `Journal`, `Settings`, `Permissions`,
  `Troubleshooting`, `Onboarding`.
- Before the first use — a legal documents consent screen, followed by a short onboarding.

## 3) What it does NOT do

- Does not process personal data outside of the local device.
- Does not send messages / logs to any server (Crashlytics crash reports are the exception, and they
  carry no message content).
- Does not raise the alarm for irrelevant messages.
- Does not ignore stop words when they are present.
- Does not depend on the UI being opened manually for background monitoring to work.

## 4) Tech stack

- **Platform:** Android, `minSdk = 28`, `targetSdk = 36`, `compileSdk = 36.1`
- **Language:** Kotlin 2.3.x, JVM target 11
- **UI:** Jetpack Compose + Material 3, edge-to-edge, custom theme (`VartovyiTheme`)
- **Architecture:** Single Activity, MVI (`State + Action + Event`), Clean Architecture
- **DI:** Koin
- **Navigation:** Navigation Compose (type-safe routes, `@Serializable`)
- **Storage:** DataStore Preferences (settings, triggers, onboarding, legal consent) + Room
  (journal, with Paging 3)
- **Background:** `NotificationListenerService`, foreground service, WorkManager
- **Crash reporting:** Firebase Crashlytics
- **Tests:** JUnit 5 + assertk + MockK + Turbine (`:app`, `:domain`, `:data`), CI workflow on PRs
- **Build:** Gradle Kotlin DSL + Version Catalog + composite `build-logic` with convention plugins,
  Detekt

### 4.1) Modules

| Module                   | Responsibility                                                                                   |
|--------------------------|--------------------------------------------------------------------------------------------------|
| `:app`                   | Compose UI, services, receivers, `AppModule`/`ViewModelModule`, `startKoin` in `VartovyiApp`     |
| `:domain`                | JVM library: models, repository interfaces, use cases, domain controllers, `useCaseModule`       |
| `:data`                  | Android library: DataStore, Room, mappers, repository impls, `databaseModule`/`repositoryModule` |
| `build-logic/convention` | `vartovyi.android.*` / `vartovyi.jvm.library` / `vartovyi.android.room` plugins                  |

Inter-module dependencies go through type-safe accessors (`projects.domain`, `projects.data`).
Plugins are applied via `alias(libs.plugins.…)`.

## 5) Architecture

Dependency direction:

```
ViewModel → UseCase → Repository (interface) → RepositoryImpl (data) → DataStore/Room
```

Key rules:

- A ViewModel injects use cases only
- A use case does **not** call another use case; shared logic lives in an internal helper inside
  `:domain` (for example `syncMonitoringRuntimeWithSettings` in `MonitoringRuntimeSync.kt`)
- Work that cannot be expressed through repositories (stopping the alarm, aligning foreground
  monitoring with persisted settings) goes through domain controllers — `AlarmController`,
  `MonitoringController`
- Domain carries no Android framework API (the one exception is `androidx.paging.PagingData` for the
  journal)
- Navigation lives in `NavGraph`; screens never receive a `NavController`

## 6) Screens and UI

The root is `MainActivity` with a single `Scaffold`: top bar + bottom bar (4 tabs) + `NavGraph`.

- **Top bar** — the tab title, a permissions status indicator (tap navigates to `Permissions`), an
  emergency stop button while the alarm is playing, and per-tab contextual actions (on `Triggers` —
  export / import / clear; on `Journal` — the info dialog).
- **Bottom bar** — `Головна` (Home), `Тригери` (Triggers), `Журнал` (Journal), `Налаштування`
  (Settings).
- **Home** — a status block with the monitoring toggle (`ACTIVE` / `INACTIVE` / `SCHEDULED`), the
  triggers card (highlighted when the list is empty or still contains nothing but the seeded demo
  examples; tap → `Triggers`), and the last alarm card (tap → the highlighted journal entry). The
  root background is animated according to the monitoring state.
- **Triggers** — the rule type selector, an input field with trigger chips, the stop words section,
  and the Telegram channels section with popular channel suggestions (35 entries, grouped by
  region). The export / import / clear actions live in the top bar.
- **Journal** — a paged list of events (`ALARM_TRIGGERED` / `SKIPPED` / `SKIPPED_COOLDOWN`) with
  copyable channel name and message text, highlighting of a specific entry, and clearing the
  journal.
- **Settings** — collapsible sections: `Дані` (Data — journal size limit, alarm retrigger cooldown,
  reset to factory defaults), `Звук` (Sound — ringtone, duration, volume), `Розклад` (Schedule —
  on/off, start, end), `Документи` (Documents — Privacy Policy, Terms of Use, the onboarding guide,
  troubleshooting). Separately — the test alarm button and the version number.
- **Permissions** — statuses and shortcuts into the system settings for Notification Access, battery
  optimizations, Do Not Disturb access, `POST_NOTIFICATIONS`, and full-screen intent.
- **Troubleshooting** — a standalone screen with tips for specific OEM firmwares.
- **Onboarding** — two pages (welcome + Telegram channels setup); shown on the first launch and
  reopenable from `Settings`.
- **LegalConsent** — blocks the UI until the current version of the documents is accepted.

## 7) Trigger rules

A rule is stored as a single string and parsed by `parseTriggerKeywordRuleFromStorage`:

| Type        | Format           | Match condition                                                 |
|-------------|------------------|-----------------------------------------------------------------|
| `WORD`      | `харків`         | every token of the term is present among the tokens of the text |
| `ALL_WORDS` | `ракета+київ`    | every term is present in the text, order does not matter        |
| `PHRASE`    | `"ціль на київ"` | the normalized text contains the phrase                         |

Normalization: NFC, unifying apostrophe variants, lowercase, collapsing whitespace, stripping
invisible Unicode characters. Duplicates are detected by `normalizedSignature()` (type + sorted
normalized terms), so `ракета+київ` and `київ+ракета` are one and the same rule.

Limits: up to **200** triggers, minimum term length is 2 characters.

A stop word blocks the alarm through a plain `contains` check over the normalized text.
The Telegram channel allow-list is **fail-open**: an empty list means every channel is allowed;
there is no separate filter toggle anymore (the legacy flag is migrated on startup by
`MigrateLegacyChannelFilterUseCase`).

On the first launch demo examples are seeded: one trigger per rule type, plus typical stop words.

## 8) Runtime flow

1. Cold start → if the stored legal documents version ≠ the current one → `LegalConsentScreen` →
   the version is stored once accepted. Refusal → `finish()`.
2. If onboarding has not been completed, the start destination is `Onboarding`, otherwise `Home`.
3. The user enables monitoring on `Home`.
4. `MonitoringForegroundService` starts (persistent notification) together with the watchdog worker.
5. `TelegramListenerService` receives Telegram notifications (`FLAG_GROUP_SUMMARY` is filtered out).
6. Filters inside `ProcessIncomingTelegramNotificationUseCase`: non-blank text → Telegram package →
   monitoring active → schedule window → channel allow-list.
7. Stop words are checked, then the first matching rule is looked up.
8. No match / a stop word present → the event is written to the journal as `SKIPPED`, no alarm.
9. On a match:

- if the alarm is already playing or the cooldown is active → a `SKIPPED_COOLDOWN` entry;
- otherwise an `ALARM_TRIGGERED` entry, the cooldown window is armed, and `AlarmService` +
  the full-screen `AlarmActivity` are started.

10. After every inserted entry the journal is trimmed to the limit (`logSizeLimit`, 300 by
    default) — the old entries are deleted within the same transaction.

## 9) Telegram notification deduplication

Telegram calls `onNotificationPosted` multiple times for one and the same message: the
GROUP_SUMMARY copy, a refresh on any change in the chat, retroactive edits of `when`
and of the text (typo fixes). The signature is an SHA-256 of
`pkg | notificationKey | senderName | messageText` (normalized text).
Deduplication works as a **sliding 60-second window**:
the DAO `findRecentIdBySignature` looks for an entry with this signature having `timestamp >=
event.timestamp - 60_000`. If one is found — `UPDATE messageText` (keeping
`status` / `matchedKeyword` / `timestamp` intact, so the alarm trigger decision is not
corrupted). If not — INSERT a new entry. Outside the 60-second window entries with the
same signature coexist (which is why the `signature` index is not UNIQUE).
`FLAG_GROUP_SUMMARY` is rejected right away in `TelegramListenerService`.
`onListenerConnected` replays the active notifications from the shade —
recovering what was missed after an OEM kill of the process.

Why exactly this way:

- the normalized message text (lowercase, trim, collapse whitespace,
  strip invisible Unicode) is stable across Telegram's refresh-edit cycles — different
  real messages have different signatures and never get merged;
- a sliding window (rather than a fixed bucket) catches duplicates that cross the minute
  boundary — Telegram's 25–30s `when`-shift no longer produces a duplicate even when the
  message "migrates" from 21:36:55 to 21:37:25;
- the priority is to **never lose a single real message**, so the risk is minimal:
  a loss is only possible if two different messages have the same normalized
  text from the same sender within 60s (a theoretical edge case).

What did not work before:

- an in-memory signature buffer (5 slots, died with the service and overflowed);
- a `postTime / 60_000` bucket (a refresh jumped into the next bucket → duplicate);
- a signature with the exact `notification.when` (Telegram shifts `when` → a duplicate on
  every refresh-edit);
- `messages.size` as part of the signature (old entries with the same size after a
  Telegram reset collided with new ones and got lost).

## 10) Trigger backup

Export builds a `KeywordsBackup` JSON (`version = 2`: `keywords`, `stopWords`,
`telegramChannels`) and hands it over either as a file through SAF or through the share sheet
(`FileProvider`).
Import reads the file with a size cap, decodes it with `ignoreUnknownKeys` (v1 files carrying the
`isTelegramChannelFilterEnabled` field are still readable) and applies it with one of the
strategies:

- `REPLACE` — the current lists are fully replaced;
- `MERGE` — the missing entries are added with deduplication (the result reports how many were
  added and how many were skipped).

Reset to factory defaults (`ResetAppToFactoryDefaultsUseCase`) wipes the settings, the triggers and
the journal.

## 11) Caveats and risks

- How well the app works depends on the granted permissions (Notification Access, battery
  optimizations, full-screen intent, POST_NOTIFICATIONS, DND access).
- OEM firmwares (Xiaomi / Samsung / Huawei) may kill background work aggressively — hence the
  dedicated `Troubleshooting` screen with instructions.
- Poorly chosen triggers / stop words → false positives and false negatives.
- The cooldown intentionally suppresses repeated alarms — a window that is too long may hide a new
  event.

## 12) Canonical legal document URLs

Mirrored in code as `PRIVACY_POLICY_URL` / `TERMS_OF_USE_URL` in
[
`LegalDocumentsContract`](domain/src/main/kotlin/com/revakovskyi/vartovyi/constants/LegalDocumentsContract.kt):

- Privacy Policy: <https://sites.google.com/view/vartovyi-privacy-policy>
- Terms of Use: <https://sites.google.com/view/vartovyi-terms-of-use>

---

© 2025 Maksym Revakovskyi. All rights reserved.
