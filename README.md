# Vartovyi (Вартовий)

Android-додаток для моніторингу Telegram-сповіщень у фоні та запуску тривоги тільки при релевантних
повідомленнях (за trigger-словами користувача).

## 1) Призначення

`Vartovyi` — утилітарний інструмент нічного/фонового чергування:

- читає вхідні Telegram-сповіщення через `NotificationListenerService`;
- аналізує текст за списками `trigger words` та `stop words`;
- запускає гучну тривогу (full-screen) тільки при релевантному збігу;
- веде журнал подій;
- працює стабільно у фоні після перезапуску пристрою.

## 2) Що робить

- Моніторить сповіщення **тільки** з Telegram-клієнтів (офіційний, web, Telegram X, Neko X).
- Підтримує trigger-слова, stop-слова, опційний фільтр за каналами.
- Запускає тривогу через `AlarmService` + `AlarmActivity` поверх lock screen.
- Показує persistent foreground-сповіщення коли моніторинг активний.
- Відновлює роботу після reboot (`BOOT_COMPLETED`) + WorkManager watchdog.
- Керування через екрани: `Home`, `Keywords`, `Logs`, `Settings`, `Permissions`.
- Перед першим використанням — екран згоди з юридичними документами.

## 3) Що НЕ робить

- Не обробляє особисті дані за межами локального пристрою.
- Не відправляє повідомлення/логи на сервери.
- Не запускає тривогу для нерелевантних повідомлень.
- Не ігнорує stop-слова при їх присутності.
- Не залежить від ручного відкриття UI для фонового моніторингу.

## 4) Технічний стек

- **Platform:** Android, `minSdk = 28`, `targetSdk = 36`
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** Single Activity, MVI (`State + Action + Event`), Clean Architecture
- **DI:** Koin
- **Navigation:** Navigation Compose (type-safe routes, `@Serializable`)
- **Storage:** DataStore Preferences + Room (журнал)
- **Build:** Gradle Kotlin DSL + Version Catalog + composite `build-logic` з convention-плагінами

## 4.1) Модулі

| Модуль                   | Роль                                                                                                    |
|--------------------------|---------------------------------------------------------------------------------------------------------|
| `:app`                   | Compose UI, сервіси, receivers, `AppModule`/`ViewModelModule`, `startKoin` у `VartovyiApp`              |
| `:domain`                | JVM library: моделі, інтерфейси репозиторіїв, use cases, доменні контролери, `useCaseModule`            |
| `:data`                  | Android library: DataStore, Room, mappers, реалізації репозиторіїв, `databaseModule`/`repositoryModule` |
| `build-logic/convention` | `vartovyi.android.*`/`vartovyi.jvm.library`/`vartovyi.android.room` плагіни                             |

Залежності між модулями через type-safe accessors (`projects.domain`, `projects.data`).
Плагіни підключаються через `alias(libs.plugins.…)`. Детально — у [CLAUDE.md](CLAUDE.md), розділ
**Gradle, Version Catalog, and build-logic**.

## 5) Архітектура

Напрямок залежностей:

```
ViewModel → UseCase → Repository (interface) → RepositoryImpl (data) → DataStore/Room
```

Ключові правила:

- ViewModel інжектить тільки UseCase
- Use case **не** викликає інший use case; для shared логіки — internal helper у `:domain`
  (наприклад `syncMonitoringRuntimeWithSettings` у `MonitoringRuntimeSync.kt`)
- Domain — без Android framework API (виняток: `androidx.paging.PagingData` для журналу)
- Навігація — у `NavGraph`, екрани не мають `NavController`
- Усі правила — у [CLAUDE.md](CLAUDE.md)

## 6) Runtime-процес

1. Cold start → якщо збережена версія legal-документів ≠ актуальній → `LegalConsentScreen` →
   після підтвердження зберігається версія, відкривається UI. Відмова → `finish()`.
2. Користувач вмикає моніторинг на `Home`.
3. Стартує `MonitoringForegroundService` (persistent notification).
4. `TelegramListenerService` отримує Telegram-сповіщення.
5. Фільтри: моніторинг активний → Telegram-пакет → (опц.) дозволений канал →
   (опц.) вікно розкладу.
6. `ProcessIncomingTelegramNotificationUseCase` перевіряє trigger/stop-слова.
7. Якщо match: запис у лог + `AlarmService` + full-screen `AlarmActivity`.
   Якщо no match / stop-word: запис у лог як SKIPPED, без тривоги.

## 7) Особливості та ризики

- Якість роботи залежить від дозволів (Notification Access, battery optimizations,
  full-screen intent, POST_NOTIFICATIONS).
- OEM-прошивки (Xiaomi/Samsung/Huawei) можуть агресивно вбивати фон.
- Неправильно підібрані trigger/stop-слова → false positive/negative.

## 8) Дедуплікація Telegram-сповіщень

Telegram викликає `onNotificationPosted` багаторазово для того самого повідомлення:
GROUP_SUMMARY-копія, refresh при змінах у чаті, ретроактивне редагування `when`
і тексту (виправлення опечаток). Сигнатура — `pkg + sbn.key +
messagingStyle.messages.size`. Дедуп працює як **ковзне 60-секундне вікно**:
DAO `findRecentIdBySignature` шукає запис із цією сигнатурою з `timestamp >=
event.timestamp - 60_000`. Якщо знайшов — `UPDATE messageText` (зберігаючи
`status` / `matchedKeyword` / `timestamp`, щоб не корумпувати рішення про тригер
тривоги). Якщо ні — INSERT новий запис. Поза 60-секундним вікном записи з тією
самою сигнатурою співіснують (тому `signature`-індекс не UNIQUE).
`FLAG_GROUP_SUMMARY` відсікається одразу в `TelegramListenerService`.
`onListenerConnected` робить replay активних нотифікацій із шторки —
відновлює пропущене після OEM-kill процесу.

Чому саме так:

- `messages.size` зростає на кожне нове повідомлення — реально різні події
  завжди мають різні сигнатури і не зливаються;
- ковзне вікно (а не фіксований бакет) ловить дублі що перетинають хвилинну
  межу — `when`-shift Telegram'а на 25–30с тепер не створює дубль навіть якщо
  повідомлення «мігрує» з 21:36:55 у 21:37:25;
- пріоритет — **не втратити жодне реальне повідомлення**, тому ризик мінімальний:
  втрата можлива лише якщо Telegram скидає список MessagingStyle двічі поспіль
  в межах 60с з одним розміром (теоретичний edge case).

Не спрацювало раніше:

- in-memory буфер сигнатур (5 слотів, гинув з сервісом і переповнювався);
- бакет `postTime / 60_000` (refresh стрибав у наступний бакет → дубль);
- сигнатура з точним `notification.when` (Telegram зсуває `when` → дубль на
  кожен refresh-edit);
- сам `messages.size` без часу (старі записи з тим самим size після reset
  Telegram колізіонували з новими і губилися).

## 9) Канонічні URL юридичних документів

Дублюються у коді як `PRIVACY_POLICY_URL` / `TERMS_OF_USE_URL` у
[
`LegalDocumentsContract`](domain/src/main/kotlin/com/revakovskyi/vartovyi/constants/LegalDocumentsContract.kt):

- Privacy Policy: <https://sites.google.com/view/vartovyi-privacy-policy>
- Terms of Use: <https://sites.google.com/view/vartovyi-terms-of-use>
