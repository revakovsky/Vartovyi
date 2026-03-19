# Vartovyi (Вартовий)

Android-додаток для моніторингу Telegram-сповіщень у фоні та запуску тривоги тільки при релевантних
повідомленнях (за trigger-словами користувача).

## 1) Призначення проєкту

`Vartovyi` створюється як утилітарний інструмент нічного/фонового чергування:

- читає вхідні Telegram-сповіщення через `NotificationListenerService`;
- аналізує текст повідомлення за списками `trigger words` та `stop words`;
- запускає гучну тривогу (full-screen) тільки коли є релевантний збіг;
- веде журнал подій для прозорості та контролю;
- працює стабільно у фоні після перезапуску пристрою.

## 2) Що додаток має робити

- Моніторити тільки вибрані Telegram-пакети (`org.telegram.messenger` та інші, які обере
  користувач).
- Підтримувати:
    - trigger-слова (позитивний тригер);
    - stop-слова (скасування тривоги навіть при trigger-збігу);
    - опційний фільтр за каналами.
- Запускати тривогу через окремий `AlarmService` + `AlarmActivity` поверх lock screen.
- Показувати постійне foreground-сповіщення, коли моніторинг активний.
- Відновлювати роботу після reboot (`BOOT_COMPLETED`) та контролювати живучість (
  watchdog/WorkManager).
- Давати керування через екрани: `Home`, `Keywords`, `Logs`, `Settings`, `Permissions`.

## 3) Що додаток не має робити

- Не має обробляти особисті дані за межами локального пристрою.
- Не має відправляти повідомлення/логи на сервери (поточний scope: локальне зберігання).
- Не має запускати тривогу для нерелевантних повідомлень без trigger-збігу.
- Не має ігнорувати stop-слова, якщо вони присутні у повідомленні.
- Не має залежати від ручного відкриття UI для фонового моніторингу.

## 4) Базовий технічний стек

- **Platform:** Android, `minSdk = 28`, `targetSdk = 36`
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** Single Activity, MVI (`State + Action + Event`), Clean Architecture
- **DI:** Koin
- **Navigation:** Navigation Compose (type-safe routes, `@Serializable`)
- **Storage:** DataStore Preferences + Room (журнал)
- **Build:** Gradle Kotlin DSL + Version Catalog

## 5) Архітектура і шари

Напрямок залежностей:

`ViewModel -> UseCase -> Repository (interface) -> RepositoryImpl (data) -> DataStore/Room/Android components`

Ключові правила:

- `ViewModel` інжектить тільки `UseCase`.
- Domain-шар без Android-залежностей.
- Репозиторії та mappers ізольовані по шарах.
- Навігація в `NavGraph`, екрани не працюють напряму з `NavController`.

## 6) Поточна структура модулів/пакетів

- `ui/` — екрани, контракти, компоненти, тема, навігація
- `domain/` — моделі, use cases, repository interfaces
- `data/` — repository implementations, DataStore, Room, mappers
- `service/` — `TelegramListenerService`, `AlarmService`
- `receiver/` — `BootReceiver`
- `di/` — Koin modules

## 7) Runtime-процес (цільова модель роботи)

1. Користувач вмикає моніторинг на `Home`.
2. Запускається monitoring foreground service (persistent notification).
3. `TelegramListenerService` отримує нові Telegram-сповіщення.
4. Повідомлення проходить фільтри:
    - моніторинг активний;
    - дозволений пакет;
    - (опційно) дозволений канал;
    - (опційно) вікно розкладу.
5. `AnalyzeMessageUseCase` перевіряє trigger/stop-слова.
6. Якщо match:
    - запис у лог;
    - запуск `AlarmService`;
    - full-screen `AlarmActivity`.
7. Якщо no match/stop-word:
    - запис у лог як пропущена подія;
    - без тривоги.

## 8) Поточний стан реалізації (as-is)

### UI / MVI

- [x] `HomeScreen`, `KeywordsScreen`, `LogsScreen`, `SettingsScreen`, `PermissionsScreen` існують.
- [x] `HomeViewModel`, `KeywordsViewModel` підключені до use cases.
- [x] `Keywords` CRUD (trigger/stop/channel filter) працює через DataStore.
- [x] У `Keywords` додано режими trigger-правил: `WORD`, `ALL_WORDS`, `PHRASE` (вибір режиму,
  підказки,
  сортування чіпсів за режимом + текстом).
- [x] Trigger-чіпси показують префікс режиму, long-press копіює лише текст правила (без префікса),
  видалення виконується через `X`.
- [x] Для полів вводу в `Keywords` додано trailing `X` (очистка поля) та long-press tooltip.
- [x] На `Home` підключено live-оновлення `lastAlertEvent` із логу.
- [x] Клік по `Last alert` на `Home` відкриває `Logs` і прокручує до конкретного запису, якщо він ще
  існує в БД.
- [x] На `Home` додано індикатор cooldown повторного спрацювання тривоги (`mm:ss`) після останнього
  alarm trigger з формулюванням “наступна тривога не раніше ніж за …”.
- [x] `Test Alarm` перенесено в `SettingsScreen` і підключено до реального alarm-flow.
- [x] Якщо моніторинг активний, тест тривоги блокується зі snackbar-підказкою та action переходу на
  `Home`.
- [x] Для `TopAppBar` підключено `TopAppBarScrollBehavior`: на `Logs` top bar ховається/з'являється
  під час скролу.
- [x] Snackbar queue вимкнено: новий snackbar скасовує попередній.
- [x] Додано haptic feedback для add/remove дій у `Keywords`, для `VartovyiSwitch` та toggle-кнопки
  моніторингу.
- [x] У `Settings` додано керування `alarmDurationSeconds` через `Slider` (5..300 с, крок 30) з live
  preview значення.
- [x] У `Settings` додано керування `alarmVolumePercent` через `Slider` (0..100%, крок 10).

### Alarm

- [x] `AlarmService` реалізований (звук, вібрація, foreground notification, stop action).
- [x] `AlarmActivity` підключена в `AndroidManifest`.
- [x] Додано anti-duplicate guard для запуску тривоги (service/controller/use case).
- [x] Stop-flow зроблено ідемпотентним (повторні stop виклики без побічних ефектів).
- [x] Додано stop-кнопку у `TopBar` (кнопка показується тільки коли alarm active).
- [x] Stop-кнопка у `TopBar` зупиняє тільки поточну тривогу (без вимкнення monitoring).
- [x] З `AlarmActivity` прибрано emergency-кнопку, залишено одну кнопку stop alarm.
- [x] У `AlarmActivity` додано вимкнення тривоги апаратними клавішами гучності (`Volume Up/Down`).
- [x] Прискорено показ `AlarmActivity`: пріоритезовано запуск UI, додано retry-відкриття activity та
  неблокуючий старт звуку.
- [x] `alarmDurationSeconds` синхронізовано з авто-зупинкою `AlarmService`.
- [x] `alarmVolumePercent` застосовується до `MediaPlayer` до старту звуку тривоги.
- [ ] `WAKE_LOCK` permission додано, але явне керування `PowerManager.WakeLock` в `AlarmService` ще
  не реалізовано.
- [ ] Повна інтеграція DND bypass потребує перевірки каналів/дозволів на runtime.

### Monitoring / Telegram listener

- [x] `TelegramListenerService` оголошений у `AndroidManifest`.
- [x] `onNotificationPosted` реалізований, базовий pipeline підключений.
- [x] Додано `MonitoringForegroundService` з ongoing notification.
- [x] `ToggleMonitoring` керує стартом/зупинкою monitoring service.
- [x] `BootReceiver` відновлює monitoring service після reboot, якщо monitoring був active.
- [x] Реалізовано watchdog (`WorkManager`) для періодичної перевірки.
- [x] Monitoring notification має кнопку `Deactivate` та зелений accent у шторці.
- [x] Stop із monitoring notification вимикає runtime, persisted state та watchdog.
- [x] Додано runtime/persisted синхронізацію моніторингу (self-heal при `MainActivity.onResume`).

### Domain / Data

- [x] `AnalyzeMessageUseCase` існує.
- [x] `KeywordMatcher`, `SettingsRepository`, `KeywordsRepository`, `LogRepository` реалізовані.
- [x] Room для журналу (`AlertEventDao`, entity, mappers) реалізований.
- [x] Додано use case обробки вхідного Telegram notification (фільтри + лог + alarm trigger).
- [x] Matching trigger-ключових слів переведено на `TriggerKeywordRule` з підтримкою `WORD` /
  `ALL_WORDS` / `PHRASE` замість простого `contains`.
- [x] Додано anti-retrigger cooldown для alarm trigger (default `5 хв`): повторний alarm trigger
  блокується до завершення cooldown; відлік працює в coroutine scope `MonitoringForegroundService`,
  а сам cooldown персиститься в `DataStore` (відновлення після restart / process death).
- [x] Для логів додано окремий статус `SKIPPED_COOLDOWN` (подія зафіксована, але alarm trigger
  пропущено через активний cooldown/вже активну тривогу).
- [x] Дедуплікація логу переведена на атомарний DB-рівень (`signature` + `UNIQUE` + `INSERT IGNORE`)
  без pre-check race condition.

## 9) Особливості та ризики

- Якість роботи сильно залежить від дозволів (`Notification Access`, battery optimizations,
  full-screen intent).
- OEM-прошивки (Xiaomi/Samsung/Huawei) можуть агресивно вбивати фон.
- Неправильно підібрані trigger/stop-слова можуть дати false positive/false negative.
- Потрібна обережна перевірка сумісності Android 13/14+ для notification/full-screen policy.

## 10) Живий TODO-план (roadmap)

> Цей список є основним джерелом правди для прогресу. Після кожної завершеної задачі оновлюємо
> статус тут.

### Milestone A — Core monitoring pipeline (найвищий пріоритет)

- [x] Реалізувати `TelegramListenerService.onNotificationPosted`:
    - [x] витяг `packageName`, `title`, `text`, `postTime`;
    - [x] пропуск порожніх/службових нотифікацій;
    - [x] фільтр за вибраними Telegram-пакетами.
- [x] Додати use case `ProcessIncomingNotificationUseCase`:
    - [x] завантаження keywords/stop-words/channels/settings;
    - [x] виклик логіки аналізу повідомлення;
    - [x] формування результату (ALARM/SKIPPED).
- [x] Інтегрувати запуск `TriggerAlarmUseCase` тільки на релевантному match.
- [x] Інтегрувати `AddLogEntryUseCase` для кожної обробленої події.

### Milestone B — Monitoring lifecycle / reliability

- [x] Додати monitoring foreground service з постійним low-importance notification.
- [x] Прив’язати `Home ToggleMonitoring` до старту/зупинки monitoring service.
- [x] Реалізувати `BootReceiver` (відновлення моніторингу після reboot, якщо було active).
- [x] Додати watchdog через `WorkManager` для самовідновлення monitor service.

### Milestone C — Home integration

- [x] Показувати `lastAlertEvent` із реального джерела (log flow).
- [x] Перенести `Test Alarm` у `SettingsScreen` (та прибрати з `HomeScreen`).
- [ ] Показати статус сервісу моніторингу (`isListenerServiceActive` / health check).

### Milestone D — Log model enhancement

- [ ] Розширити модель журналу під стани:
    - [ ] `ALARM_TRIGGERED`
    - [ ] `SKIPPED_NO_KEYWORD`
    - [ ] `SKIPPED_STOP_WORD`
    - [ ] `SKIPPED_CHANNEL_FILTER`
- [ ] Оновити Room schema + mapper + UI логу під нову модель.

### Milestone E — Settings/Alarm completion

- [x] Підв’язати `alarmDurationSeconds` до реальної зупинки `AlarmService`.
- [x] Додати налаштування `alarmVolumePercent` та застосування в `AlarmService`.
- [ ] Підв’язати `isVibrationEnabled` до поведінки `AlarmService`.
- [ ] Додати короткий `WakeLock` в `AlarmService` (safe acquire/release) для надійного старту
  тривоги в deep sleep.
- [ ] Додати preview/play тест звуку тривоги з Settings.

### Milestone F — Permissions hardening

- [x] Повний чек усіх required/recommended permissions.
- [x] Динамічна логіка для API 33+/34+ (POST_NOTIFICATIONS, full-screen intent).
- [x] Авто-рефреш permission статусів на `PermissionsScreen` при `ON_RESUME`.
- [x] Узгоджені переходи в системні налаштування для Notifications / Full-screen / Battery / DND.
- [ ] Vendor-specific автозапуск гайд (Xiaomi/Samsung/Huawei).

### Milestone G — QA / release readiness

- [ ] Інструментальні/інтеграційні перевірки основного пайплайна.
- [ ] Ручний тест-план для нічного сценарію.
- [ ] Полірування UX станів помилок/підказок.
- [ ] Підготовка release checklist.

## 11) Правило оновлення цього README

Після кожної суттєвої зміни:

1. Оновити секцію `Поточний стан реалізації (as-is)`.
2. Оновити чекбокси в `Живий TODO-план`.
3. Додати короткий запис у форматі:
    - `YYYY-MM-DD — що зроблено, що залишилось`.

## 12) Change log (короткий)

- `2026-03-13` — README перетворено у повну специфікацію проєкту + єдиний TODO-roadmap для подальшої
  роботи.
- `2026-03-13` — реалізовано базовий Telegram notification pipeline (`onNotificationPosted` +
  process use case + логування + alarm trigger).
- `2026-03-13` — реалізовано `MonitoringForegroundService`, прив’язку до toggle, `BootReceiver` та
  `WorkManager` watchdog.
- `2026-03-13` — реалізовано список подій на `LogScreen` і notification-діалог на `Home` при новому
  спрацюванні.
- `2026-03-13` — реалізовано повноцінний `PermissionsScreen` (UI + дії) та відкриття з
  іконки в `TopBar` з badge.
- `2026-03-15` — стабілізовано alarm/monitoring flows (anti-duplicate guard, idempotent stop,
  emergency stop, runtime/persisted sync), оновлено permissions UX та перенесено `Test Alarm` у
  `Settings`.
- `2026-03-15` — впроваджено атомарну дедуплікацію логів на рівні Room (`signature + UNIQUE +
  INSERT IGNORE`), прискорено показ `AlarmActivity`, додано scroll behavior top bar на `Logs`,
  haptic для ключових дій та заміну snackbar без черги.
- `2026-03-16` — додано режими trigger-правил (`WORD`/`ALL_WORDS`/`PHRASE`) у domain + `Keywords`
  UI,
  оновлено matching вхідних Telegram-сповіщень на правила, покращено UX чіпсів/полів вводу (copy,
  remove через `X`, trailing clear icon з tooltip).
- `2026-03-16` — на `Home` додано deep-link поведінку для `Last alert`: перехід у `Logs` з
  автоскролом до відповідного запису за `id` (fallback на звичайний список, якщо запис видалено).
- `2026-03-16` — додано захист від повторного alarm trigger через cooldown (`5 хв` за
  замовчуванням):
  відлік працює тільки в життєвому циклі monitoring service, стан cooldown показується на `Home`,
  значення підготовлено до винесення в `Settings`.
- `2026-03-16` — cooldown винесено в персистентний стан (`DataStore`) з відновленням після restart /
  process death, а в лог-модель додано статус `SKIPPED_COOLDOWN` для явного позначення пропусків
  через
  cooldown.
- `2026-03-19` — stop у `TopBar` змінено на локальну зупинку лише поточної тривоги; додано stop
  тривоги через `Volume Up/Down` в `AlarmActivity`; у `Settings` додано `Alarm duration` slider
  (5..300 с, крок 30, live preview) і `Alarm volume` slider (0..100%, крок 10); у `AlarmService`
  гучність застосовується до `MediaPlayer` до старту відтворення.

## 13) Узгоджені продукт-рішення (зафіксовано)

- Логування: зберігаємо **всі** перехоплені Telegram-повідомлення (включно з пропущеними).
- Channel filter: первинна реалізація через `notification title` в режимі **exact
  match + `ignoreCase`** (без `contains`).
- Monitoring default state: при першому запуску — `OFF`.
- Alarm duration mode: використовуємо числове значення в секундах (`Int`) з кроком слайдера в UI.

## 14) UI/UX специфікація (фільтрована, актуальна)

Ця секція фіксує тільки те, що важливо для реалізації і перевірки. Детальні дизайнерські
експерименти/мокапні варіації не є блокерами для core-функціоналу.

### 14.1 Visual direction

- Тема за замовчуванням: **Dark only** (light theme поки не робимо).
- Стиль: утилітарний, стриманий, без декоративного шуму.
- Ключові кольори:
    - background: `#0D1117`
    - surface: `#161B22`
    - surfaceVariant: `#21262D`
    - primary: `#2EA043`
    - error/alarm: `#F85149`
    - warning: `#D29922`
    - onBackground: `#E6EDF3`
    - secondary text: `#8B949E`
    - outline: `#30363D`

### 14.2 Typography baseline

- TopAppBar title: `20sp Medium`
- Section title: `16sp Medium`
- Body: `14sp Regular`
- Secondary/hint: `12sp Regular`
- Big status text: `24sp Bold`
- Chip text: `13sp Medium`
- Log time: `12sp Mono` (`RobotoMono`)

### 14.3 Navigation

- Bottom bar: `Home`, `Keywords`, `Log`, `Settings`.
- `Permissions` відкривається окремо через іконку в `TopAppBar`.
- На permissions-іконці показується badge, якщо є ненадані критичні дозволи.

### 14.4 Screens — implementation contract

- **Home**
    - Великий статус моніторингу (щит + текст + toggle/button).
    - Картка ключових слів (включно з empty-state).
    - Картка останнього спрацювання (включно з empty-state).

- **Keywords**
    - Trigger rules (режими `WORD` / `ALL_WORDS` / `PHRASE`, додавання/видалення, tooltip).
    - Stop words (додавання/видалення, tooltip, візуально відмінні chips).
    - Telegram channel filter (toggle + список каналів, якщо увімкнено).

- **Log**
    - Список перехоплених подій (включно з пропущеними).
    - Стани елементів: alarm / skipped.
    - Очищення через confirm-діалог.
    - Empty-state.

- **Settings**
    - Кнопка `Test Alarm` (запуск/стоп тестової тривоги).
    - Якщо monitoring `ACTIVE`, тест тривоги блокується зі snackbar-підказкою та переходом на
      `Home`.
    - Розклад роботи (enable + start/end time).
    - Налаштування тривоги:
        - duration — `Slider` (5..300 с, крок 30) з live-оновленням значення під час перетягування;
        - volume — `Slider` (0..100%, крок 10);
        - vibration;
        - alarm sound preview.
    - Джерела сповіщень (список Telegram-клієнтів).
    - Ліміт розміру журналу.

- **Permissions**
    - Повний перелік критичних/рекомендованих дозволів.
    - Кнопки переходу в системні налаштування.
    - Статуси permission автооновлюються при поверненні на екран (`ON_RESUME`).

### 14.5 Alarm UX

- `AlarmActivity`: full-screen поверх lock screen.
- Контент: сирена, заголовок `ТРИВОГА`, одна кнопка вимкнення тривоги.
- `AlarmActivity`: `Volume Up/Down` теж вимикають поточну тривогу.
- Окреме alarm-сповіщення: `HIGH`, `CATEGORY_ALARM`, full-screen intent, action `Вимкнути тривогу`,
  red accent, fallback-відкриття `AlarmActivity`.

### 14.6 Monitoring notification UX

- Коли моніторинг активний: ongoing foreground notification (`LOW`, без звуку, не свайпається).
- Tap по notification відкриває додаток.
- У notification є кнопка `Вимкнути`, яка зупиняє monitoring runtime + persisted state.
- Notification має зелений accent для індикації активного стану.

### 14.7 Notes for implementation

- Для channel filter використовуємо `title exact match + ignoreCase` як базову стратегію.
- Якщо в реальних тестах стабільність недостатня, додаємо fallback-режим як окрему, підтверджену
  зміну.
- `Alarm duration` моделюємо числовим значенням у секундах (`Int`) з обмеженням діапазону та кроком
  у UI.
