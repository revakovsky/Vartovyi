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

- Моніторить сповіщення **тільки** з офіційного клієнта Telegram (`org.telegram.messenger`).
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
5. Фільтри: моніторинг активний → пакет `org.telegram.messenger` → (опц.) дозволений канал →
   (опц.) вікно розкладу.
6. `ProcessIncomingTelegramNotificationUseCase` перевіряє trigger/stop-слова.
7. Якщо match: запис у лог + `AlarmService` + full-screen `AlarmActivity`.
   Якщо no match / stop-word: запис у лог як SKIPPED, без тривоги.

## 7) Особливості та ризики

- Якість роботи залежить від дозволів (Notification Access, battery optimizations,
  full-screen intent, POST_NOTIFICATIONS).
- OEM-прошивки (Xiaomi/Samsung/Huawei) можуть агресивно вбивати фон.
- Неправильно підібрані trigger/stop-слова → false positive/negative.

## 8) Перед публічним релізом (Play Store)

### `BuildTypes.kt` — hardcoded keystore credentials

- **Файл:**
  `build-logic/convention/src/main/java/com/revakovskyi/vartovyi/convention/application/BuildTypes.kt`
- **Що зробити:** перенести шлях до keystore + паролі у `local.properties` (gitignored), env vars
  або CI secrets. АБО видалити signing config повністю, якщо підписувати плануєш через інший
  mechanism.
- **Поки лишаємо** для локального dev/тестування.

### Manual QA на малих екранах

Протестувати UI на пристрої / емуляторі з невеликим екраном (наприклад 4.5"–5.0", низька щільність):
чи коректно відображається контент на всіх екранах (`Home`, `Keywords`, `Logs`, `Settings`,
`Permissions`, `AlarmActivity`, `OnboardingScreen`, `LegalConsentScreen`), без обрізань тексту,
overflow або зламаних layout-ів.

### Прогін Android Studio inspect / lint

Запустити вбудовані інструменти:

- **Code → Inspect Code…** на весь проєкт
- **Analyze → Run Inspection by Name…** → Unused resources / Unused declarations
- **Build → Analyze APK** на release-збірку — перевірити розмір і знайти зайве

Подивитися чи можна видалити невикористані ресурси (drawables, strings, layouts), unused imports,
deprecated API, проблеми продуктивності або якості коду.

## 9) Канонічні URL юридичних документів

Дублюються у коді як `PRIVACY_POLICY_URL` / `TERMS_OF_USE_URL` у
[
`LegalDocumentsContract`](domain/src/main/kotlin/com/revakovskyi/vartovyi/constants/LegalDocumentsContract.kt):

- Privacy Policy: <https://sites.google.com/view/vartovyi-privacy-policy>
- Terms of Use: <https://sites.google.com/view/vartovyi-terms-of-use>
