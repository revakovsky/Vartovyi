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

- Моніторити сповіщення **тільки** з офіційного клієнта Telegram (`org.telegram.messenger`).
- Підтримувати:
    - trigger-слова (позитивний тригер);
    - stop-слова (скасування тривоги навіть при trigger-збігу);
    - опційний фільтр за каналами.
- Запускати тривогу через окремий `AlarmService` + `AlarmActivity` поверх lock screen.
- Показувати постійне foreground-сповіщення, коли моніторинг активний.
- Відновлювати роботу після reboot (`BOOT_COMPLETED`) та контролювати живучість (
  watchdog/WorkManager).
- Давати керування через екрани: `Home`, `Keywords`, `Logs`, `Settings`, `Permissions`.
- До першого доступу до основного UI показувати екран згоди з юридичними документами (privacy /
  terms)
  та зберігати прийняту версію документів локально.

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
- **Build:** Gradle Kotlin DSL + Version Catalog (`gradle/libs.versions.toml`), composite *
  *`build-logic`**
  з convention-плагінами `vartovyi.*`, type-safe **`projects.*`** для залежностей між модулями

## 4.1) Gradle, модулі та build-logic

**Навіщо:** єдине джерело версій, менше копіпасти в `build.gradle.kts`, узгоджені правила для AGP 9+
(без застарілого `compileSdkVersion`, якщо є заміна), чіткі межі `:app` / `:domain` / `:data`.

**Що є в репозиторії:**

| Модуль                   | Роль                                                                                                                                                                                                          |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `:app`                   | Android application: Compose UI, сервіси (`service/`), `receiver/`, Koin (`AppModule`, `ViewModelModule`), `startKoin` у `VartovyiApp`                                                                        |
| `:domain`                | JVM library (`vartovyi.jvm.library`): моделі, інтерфейси репозиторіїв, use cases, доменні контролери, `KeywordMatcher`, Koin `useCaseModule`; `group`/`version` з Version Catalog                             |
| `:data`                  | Android library (`vartovyi.android.library` + `vartovyi.android.room`): DataStore, Room, mappers, реалізації репозиторіїв, Koin `databaseModule` та `repositoryModule`; `namespace` у `data/build.gradle.kts` |
| `build-logic/convention` | Плагіни `vartovyi.android.*`, `vartovyi.jvm.library`, `vartovyi.android.room`; спільна конфігурація SDK/Compose/JVM                                                                                           |

**Плагіни в модулях** — через **`alias(libs.plugins.…)`** з `libs.versions.toml`. У **кореневому**
`build.gradle.kts`
є `apply false` лише для **стандартних** плагінів (AGP, Kotlin, KSP, Detekt тощо); **не** додавати
`apply false` для `vartovyi.*` — це конфліктує з `includeBuild("build-logic")` у Gradle.

**Залежності між модулями:** `implementation(projects.domain)`, `implementation(projects.data)` (
потрібно
`enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` у `settings.gradle.kts`).

Детальні правила, пріоритети та edge cases (compileSdk DSL, залежності між `:app` / `:domain` /
`:data`) — у **[CLAUDE.md](CLAUDE.md)** у розділі **Gradle, Version Catalog, and build-logic**.

У **`build-logic/convention/.../BuildTypes.kt`** для `:app` може бути задано `signingConfigs` (
release) і
підключення до `defaultConfig` (локальна збірка з тестовим keystore). Для CI / публічного
репозиторію
шлях і паролі варто виносити в secrets або локальні файли, які не комітяться.

## 5) Архітектура і шари

Напрямок залежностей:

`ViewModel -> UseCase -> Repository (interface) -> RepositoryImpl (data) -> DataStore/Room/Android components`

Ключові правила:

- `ViewModel` інжектить тільки `UseCase`.
- Усередині **одного** use case **не** викликаються інші use case; допускаються виклики *
  *інтерфейсів
  репозиторіїв** і доменних **контролерів** (`AlarmController`, `MonitoringController` для
  зупинки тривоги / синхронізації foreground-моніторингу). Спільна логіка sync —
  `MonitoringRuntimeSync.kt`
  (`syncMonitoringRuntimeWithSettings`), також використовується в
  `SyncMonitoringRuntimeUseCaseImpl`.
- Domain-шар — окремий Gradle-модуль `:domain` без Android framework API; для журналу з paging у
  контракті використовується `androidx.paging.PagingData` (залежність `paging-common` у `:domain`).
- Репозиторії та mappers ізольовані по шарах.
- Навігація в `NavGraph`, екрани не працюють напряму з `NavController`.

## 6) Поточна структура модулів/пакетів

**Gradle-модулі:** `:app`, `:domain`, `:data` (див. також §4.1).

**`:domain`** (корінь пакетів `com.revakovskyi.vartovyi.*`): `model/`, `repository/` (інтерфейси),
`usecase/`, `controllers/`, `constants/` (зокрема `LegalDocumentsContract`: версія документів та
canonical URL політики/умов), `utils/`, `di/UseCaseModule.kt`.

**`:data`** (пакет `com.revakovskyi.vartovyi.data.*`): `datastore/`, `db/`, `mappers/`,
`repository/` (реалізації), `di/DatabaseModule.kt`, `di/RepositoryModule.kt`. Реалізації та
інфраструктура Room/DataStore — `internal` у межах модуля, публічні лише Koin-модулі для підключення
з `:app`.

**`:app`** (`com.revakovskyi.vartovyi.*`):

- `ui/` — екрани, контракти, компоненти, тема, навігація; `ui/screen/legal/` — згода з документами;
  `ui/util/` — допоміжні речі (наприклад `CustomTabsHelper`, перевірка дозволів, scroll behavior top
  bar)
- `service/` — `TelegramListenerService`, `AlarmService`, monitoring
- `receiver/` — `BootReceiver`
- `di/` — `AppModule`, `ViewModelModule` (решта Koin-модулів — у `:domain` та `:data`)

## 7) Runtime-процес (цільова модель роботи)

0. Після холодного старту: якщо збережена версія прийнятих документів не збігається з поточною в
   `LegalDocumentsContract`, показується екран згоди; після підтвердження зберігається актуальна
   версія і відкривається основний UI. Відмова завершує процес (`finish()`).
1. Користувач вмикає моніторинг на `Home`.
2. Запускається monitoring foreground service (persistent notification).
3. `TelegramListenerService` отримує нові Telegram-сповіщення.
4. Повідомлення проходить фільтри:
    - моніторинг активний;
    - пакет сповіщення — `org.telegram.messenger`;
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
- [x] Для чіпсів у `Keywords` виправлено layout при довгому тексті: кнопка видалення `X` залишається
  видимою навіть якщо текст переноситься на 2 рядки.
- [x] Видалення чіпсів (`trigger` / `stop` / `channel`) переведено на confirm-діалог, щоб уникнути
  випадкового видалення одним тапом.
- [x] Для полів вводу в `Keywords` додано trailing `X` (очистка поля) та long-press tooltip.
- [x] На `Home` підключено live-оновлення `lastAlertEvent` із логу.
- [x] Клік по `Last alert` на `Home` відкриває `Logs` і прокручує до конкретного запису, якщо він ще
  існує в БД.
- [x] На `Home` додано індикатор cooldown повторного спрацювання тривоги (`mm:ss`) після останнього
  alarm trigger з формулюванням “наступна тривога не раніше ніж за …”.
- [x] `Test Alarm` перенесено в `SettingsScreen` і підключено до реального alarm-flow.
- [x] Якщо моніторинг активний, тест тривоги блокується зі snackbar-підказкою та action переходу на
  `Home`.
- [x] Для `TopAppBar` підключено `TopAppBarScrollBehavior` (`enterAlways`): на `Keywords`, `Logs` і
  `Settings` top bar ховається/з'являється під час скролу.
- [x] Snackbar queue вимкнено: новий snackbar скасовує попередній.
- [x] Додано haptic feedback для add/remove дій у `Keywords`, для `VartovyiSwitch` та toggle-кнопки
  моніторингу.
- [x] У `Settings` додано керування `alarmDurationSeconds` через `Slider` (5..300 с, крок 30) з live
  preview значення.
- [x] У `Settings` додано керування `alarmVolumePercent` через `Slider` (0..100%, крок 10).
- [x] На `Settings` додано loading-state (`LoadingOverlay`) під час первинного завантаження всіх
  необхідних налаштувань.
- [x] У `Settings` додано вибір мелодії тривоги через системний `RingtonePicker` з відображенням
  вибраної назви.
- [x] Після повернення з системного `RingtonePicker` на `Settings` активна секція accordion більше
  не
  згортається.
- [x] `Settings` згруповано в картки-секції: **Дані** (ліміт журналу 100/300/500/1000, пауза між
  тривогами з підказками в діалозі, заглушка експорту з snackbar), **Звук** (мелодія, тривалість,
  гучність), **Розклад роботи** (toggle + час + підказка в діалозі для секції).
- [x] `Settings` секції перетворено на accordion-контейнери: стартово згорнуті, анімоване
  розкриття/згортання, іконка `down` з анімацією, одночасно відкрита тільки одна секція.
- [x] `Settings` секції автоматично згортаються при покиданні екрана; виняток — сценарій запуску
  `Test Alarm` (overlay поверх `Settings` не скидає стан секцій).
- [x] `Settings`: остання секція-акордеон **Info** — текстові посилання Privacy Policy / Terms of
  Use
  (типографіка `bodyLink`, Chrome Custom Tabs; секція не згортається після переходу за посиланням,
  як для `RingtonePicker`). `LegalDocumentsSettingsSection`, URL з `LegalDocumentsContract`.
  Вибір мелодії: інтент/парсинг/назва через `AlarmSoundPickerHelper`.
- [x] Тривалість **cooldown** між тривогами налаштовується на екрані (наприклад 1/3/5/10 хв) і
  зберігається в `DataStore`; логіка тригера використовує актуальне значення.
- [x] `LogScreen`: стан відображення списку (loading / error / empty / content) та дані для
  підсвітки запису формуються у `LogViewModel`; UI лише синхронізує сигнали paging у `Action`.
- [x] Для помилки завантаження журналу — `LogErrorState` з retry та кнопкою на базі
  `VartovyiActionButton`.
- [x] Крокові слайдери тривалості/гучності тривоги використовують спільний `VartovyiSettingSlider` з
  опційним тактильним відгуком на дискретний крок.
- [x] Кнопки `SettingsTestAlarmButton`, `LogClearButton`, `KeywordsClearButton`, `StatusBlock`
  toggle і кнопка вибору мелодії уніфіковано через спільний компонент `VartovyiActionButton`.
- [x] **Корінь додатку (`MainActivity`):** анімований фон залежно від `MonitoringState` (окремі
  градієнти/розташування для неактивного та активного моніторингу, плавний перехід).
- [x] **Кореневий фон (`appRootBackground`):** у станах `INACTIVE` та `ACTIVE` центр градієнта
  анімовано "блукає" по всій площі екрана; параметри анімації винесено в константи.
- [x] **Home (активний моніторинг):** сигнальні кільця на весь `HomeContent`, центр анімації
  прив’язаний до центру іконки щита; пульс іконки `security_on`; коли моніторинг вимкнено — легкий
  пульс лише на кнопці «Активувати».
- [x] **`LoadingOverlay`:** спільний модуль `MonitoringActiveIconSignals.kt` (пульс іконки +
  кільця) для екранів завантаження.
- [x] **Legal consent (перед основним UI):** `LegalConsentScreen` + `LegalConsentViewModel` (MVI),
  гейт у `MainActivity`: `LoadingOverlay` → екран згоди (кнопки відкриття політики/умов у **Chrome
  Custom Tabs** через `CustomTabsHelper.openCustomChromeTab`) → основний контент з `NavGraph` після
  прийняття; мінімальна затримка перед показом форми згоди для стабільного UX; відмова завершує
  activity.
- [x] **Юридичний стан:** `LegalConsentRepository` + `LegalConsentDataStore` (окремий DataStore
  `accepted_legal_documents_version`), use cases `ObserveLegalConsentStateUseCase` /
  `AcceptCurrentLegalDocumentsUseCase`; поточна версія документів і URL політики/умов — у
  `LegalDocumentsContract` (`:domain`).
- [x] **`MainActivity`:** винесено логіку моніторингу/тривоги в `MainViewModel` + `MainUiContract`
  (shell більше не тримає всю логіку inline).
- [x] **Локалізація:** повний `values-ru/strings.xml` (паралельно до `values` та `values-uk`);
  рядки legal додано в усі три набори.
- [x] **Legal consent — disclaimer:** на `LegalConsentScreen` додано картку-попередження
  (`Surface(errorContainer)`) з підписом «⚠️ Important!» та текстом про те, що додаток не є
  заміною офіційних систем тривоги; картка знаходиться між основним описом і кнопками документів.

### Onboarding

- [x] `OnboardingScreen` розбито на 5 сторінок: Welcome, Telegram, Permissions, Keywords, Launch.
- [x] `OnboardingPageLayout` підтримує два типи візуального контенту через sealed interface
  `OnboardingVisual`: `VectorIcon` (Icon composable з tint) та `RasterImage` (Image composable);
  welcome-сторінка використовує `RasterImage(just_logo)`, решта — `VectorIcon`.
- [x] На сторінці Permissions іконка змінюється з `security_red` (error tint) на `security_green`
  (primary tint) коли всі критичні дозволи надані.
- [x] На сторінці Keywords під кнопкою «Відкрити ключові слова» є хінт `bodySmall` про можливість
  налаштувати ключові слова пізніше.
- [x] На сторінці Welcome додано приватна нотатка (`bodySmall`, `onSurfaceVariant`) про те, що всі
  дані зберігаються лише на пристрої та нікуди не надсилаються; нотатка підтягується через слот
  `actionContent` без змін у `OnboardingPageLayout`.
- [x] `OnboardingViewModel` використовує звичайний `koinViewModel()` (scope = NavBackStackEntry);
  при навігації з Settings → Onboarding створюється свіжий ViewModel з `currentPage = 0` і
  `canSkip` автоматично `true` якщо `isCompleted = true`.
- [x] **Onboarding guide в Settings:** секція **Info** містить третє посилання «Відкрити онбординг»
  (`settings_open_onboarding_guide`), що відкриває онбординг заново з першої сторінки;
  `SettingsUiContract.Action/Event.OpenOnboardingGuide` → `SettingsViewModel` →
  `onNavigateToOnboarding` → `navController.navigate(Routes.Onboarding)`.
- [x] `onClose` в NavGraph — уніфікований патерн: `navigateUp()` → якщо повернувся `false`
  (стартовий екран при першому запуску) → `navigate(Home)` з очищенням стека; `isFromSettings`
  detection більше не потрібен.

### Alarm

- [x] `AlarmService` реалізований (звук, вібрація, foreground notification, stop action).
- [x] `AlarmActivity` підключена в `AndroidManifest`.
- [x] Додано anti-duplicate guard для запуску тривоги (service/controller/use case).
- [x] Stop-flow зроблено ідемпотентним (повторні stop виклики без побічних ефектів).
- [x] Додано stop-кнопку у `TopBar` (кнопка показується тільки коли alarm active).
- [x] Stop-кнопка у `TopBar` зупиняє тільки поточну тривогу (без вимкнення monitoring).
- [x] З `AlarmActivity` прибрано emergency-кнопку, залишено одну кнопку stop alarm.
- [x] У `AlarmActivity` додано вимкнення тривоги апаратними клавішами гучності (`Volume Up/Down`).
- [x] Прискорено показ `AlarmActivity`: пріоритезовано запуск UI, додано retry-відкриття activity (
  крок **500 ms**, до **3** спроб `0..ALARM_ACTIVITY_OPEN_MAX_RETRIES` при `MAX_RETRIES = 2`) та
  неблокуючий старт звуку.
- [x] `alarmDurationSeconds` синхронізовано з авто-зупинкою `AlarmService`.
- [x] `alarmVolumePercent` застосовується до `MediaPlayer` до старту звуку тривоги.
- [x] `alarmSoundUri` (вибрана мелодія) зчитується в `AlarmService` перед стартом і використовується
  для відтворення (fallback на системний alarm/ringtone).
- [x] `AlarmActivity`: анімований фон (`AlarmScreenAnimatedBackground`) у тонах `error` /
  `background` — пульсуючий радіальний відтінок і кільця від **центру іконки тривоги**; та сама
  пульсація масштабу для іконки та кнопки вимкнення (синхронно).
- [x] `AlarmService`: `PowerManager.PARTIAL_WAKE_LOCK` — на старті циклу тривоги одразу
  `acquire` з timeout **30 с** (`INITIAL_WAKE_LOCK_TIMEOUT_MILLIS`), далі після читання тривалості
  lock перевизначається на **тривалість тривоги + 15 с** буферу (`WAKE_LOCK_BUFFER_MILLIS`); одне
  читання тривалості на цикл (разом з auto-stop), release у stop/destroy.
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
- [x] `KeywordMatcher`, інтерфейси `SettingsRepository`, `KeywordsRepository`, `LogRepository` у
  `:domain`; реалізації — у `:data`.
- [x] Room для журналу (`AlertEventDao`, entity, mappers) у модулі `:data`.
- [x] Додано use case обробки вхідного Telegram notification (фільтри + лог + alarm trigger).
- [x] Matching trigger-ключових слів переведено на `TriggerKeywordRule` з підтримкою `WORD` /
  `ALL_WORDS` / `PHRASE` замість простого `contains`.
- [x] Додано anti-retrigger cooldown для alarm trigger (дефолт **5 хв**, тривалість змінюється в
  `Settings`): повторний alarm trigger
  блокується до завершення cooldown; відлік працює в coroutine scope `MonitoringForegroundService`,
  а сам cooldown персиститься в `DataStore` (відновлення після restart / process death).
- [x] Для логів додано окремий статус `SKIPPED_COOLDOWN` (подія зафіксована, але alarm trigger
  пропущено через активний cooldown/вже активну тривогу).
- [x] Дедуплікація логу переведена на атомарний DB-рівень (`signature` + `UNIQUE` + `INSERT IGNORE`)
  без pre-check race condition.
- [x] `LegalConsentRepository` / `LegalConsentRepositoryImpl` + `LegalConsentDataStore` для згоди з
  юридичною версією документів (`CURRENT_LEGAL_DOCUMENTS_VERSION` у domain).
- [x] **Скидання до заводських (`Settings` → секція Дані):** червона кнопка, `VartovyiDialog` з
  поясненням; `ResetAppToFactoryDefaultsUseCase` — зупинка тривоги (`AlarmController`), очищення
  monitoring/keywords DataStore (`preferences.clear()`), очищення журналу (
  `LogRepository.clearLog()`),
  `syncMonitoringRuntimeWithSettings` для узгодження foreground-моніторингу; запис прийняття legal
  **не** скидається.
- [x] **Очистка списків Keywords:** нижня фіксована кнопка (layout як у `Log`: `weight` + кнопка),
  confirm-діалог; `ClearKeywordsScreenDataUseCase` — зупинка тривоги, `monitoring = false` +
  cooldown
  `0`, очищення keywords DataStore, sync моніторингу; інші налаштування (звук, розклад, ліміт
  журналу)
  не чіпаються; у `KeywordsUiContract.State` прапорець «чи є що чистити» обчислюється getter-ом від
  збережених списків і стану фільтра каналів.
- [x] **Backup Keywords (експорт / імпорт):** кнопки Export та Import у нижній частині
  `KeywordsScreen`; export — SAF `CreateDocument`, import — SAF `OpenDocument`; формат `JSON`
  (`KeywordsBackup`: `version`, `keywords`, `stopWords`, `telegramChannels`,
  `isTelegramChannelFilterEnabled`); `ExportKeywordsUseCase` повертає `ExportResult`
  (`Success(jsonContent)` / `Error`), `ImportKeywordsUseCase` повертає `ImportResult` (`Success` /
  `InvalidFormat` / `UnsupportedVersion`); усі IO-операції обгорнуті в `try/catch`; версія завжди
  записується у файл (`encodeDefaults = true`); поле `version` перевіряється при імпорті — файли
  майбутніх версій відхиляються з окремим повідомленням; кнопка Export заблокована (`enabled =
  false`) коли немає жодного ключового слова (`canExport` getter у `State`); уся логіка
  SAF-взаємодії інкапсульована у `KeywordsBackupHelper` (scoped до composition, без витоку пам'яті);
  результати повертаються через `Action` → ViewModel → `Event` → snackbar на `KeywordsScreen`;
  якщо при імпорті є наявні дані — показується `VartovyiDialog` із попередженням про перезапис
  (confirm = error color).
- [x] **`VartovyiActionButton` Outlined — автоматичний disabled стан:** кольори тексту, іконки та
  рамки при `enabled = false` обчислюються всередині компонента (`onSurfaceVariant`); колсайти
  передають тільки `enabled`, без `if (isEnabled)` умов зовні.
- [x] **ViewModel events: `Channel` замість `MutableSharedFlow`:** усі ViewModel (`HomeViewModel`,
  `KeywordsViewModel`, `LogViewModel`, `SettingsViewModel`, `PermissionsViewModel`,
  `LegalConsentViewModel`) перейшли з `MutableSharedFlow<Event>(extraBufferCapacity = 1)` на
  `Channel<Event>(Channel.BUFFERED)` + `receiveAsFlow()`; семантично точніше (point-to-point),
  природне буферування при lifecycle-переходах, `emit` замінено на `send`.

## 9) Особливості та ризики

- Якість роботи сильно залежить від дозволів (`Notification Access`, battery optimizations,
  full-screen intent).
- OEM-прошивки (Xiaomi/Samsung/Huawei) можуть агресивно вбивати фон.
- Неправильно підібрані trigger/stop-слова можуть дати false positive/false negative.
- Потрібна обережна перевірка сумісності Android 13/14+ для notification/full-screen policy.

## 10) Живий TODO-план (roadmap)

> Цей список є основним джерелом правди для прогресу. Після кожної завершеної задачі оновлюємо
> статус тут.
>
> Нижче — актуальний пріоритезований backlog під підготовку до релізу в Store.

### P0 — Release blocker (зробити перед публікацією)

- [x] Запустити повний прогін **release** збірки на краші (smoke + ключові user flows) — успішно
  протестовано; повторний обов'язковий прогін перед релізом не потрібен.
- [x] Додати `WakeLock` safe acquire/release в `AlarmService` для надійного старту тривоги в deep
  sleep (ранній lock **30 с**; основний timeout — тривалість тривоги + **15 с** буферу).
- [x] Підготувати і опублікувати `Privacy Policy` та `Terms of Use` (Google Sites, canonical HTTPS
  URL):
  - Privacy Policy: `https://sites.google.com/view/vartovyi-privacy-policy`
  - Terms of Use: `https://sites.google.com/view/vartovyi-terms-of-use`
- [x] Додати legal consent flow у застосунок: gate в `MainActivity` (loading → consent → main) +
  DataStore `accepted_legal_documents_version` (порівняння з `CURRENT_LEGAL_DOCUMENTS_VERSION`).
- [x] Додати посилання на `Privacy Policy` і `Terms of Use` в `Settings` (Chrome Custom Tabs,
  секція-акордеон **Info** внизу екрана; текст-посилання `bodyLink`).
- [x] Додати/розширити `values-ru/strings.xml` (повний набір рядків додатку, включно з legal).
- [x] Додати відображення версії додатку (`versionName`) внизу `Settings` (центр, приглушений текст,
  рядок produced by).
- [x] Оновити іконку додатку (launcher icon + adaptive icon + monochrome для Android 13+).
- [x] Скинути налаштування до заводських у `Settings` (секція **Дані**, confirm-діалог; див. §8
  as-is).
- [x] Додати Crashlytics + базову продуктову аналітику (мінімум ключових подій alarm/monitoring) —
  лише Crashlytics (автоматичний збір крашів); аналітика навмисно пропущена (privacy-чутливий
  застосунок, журнал подій вже є в Room).
- [x] Додати **імпорт та експорт ключових слів** (файл, сценарій backup/restore).
- [x] Додати базову in-app інструкцію користування і посилання на неї в `Settings` — **зроблено:**
  онбординг доступний повторно з секції **Info** → «Відкрити онбординг».
- [ ] Додати окрему повну інструкцію (web/markdown) і посилання на неї в `Settings`.
- [ ] Прогнати **ручне тестування**: різні **розміри екранів** і різні **вендори** (у тому числі
  реальні пристрої **Xiaomi / Samsung / Huawei**) — ключові сценарії, фоновий моніторинг, тривога;
  це **QA на девайсах**, не окремий матеріал-інструкція в застосунку чи в репозиторії.
- [ ] Підготувати release checklist (передпублікаційний чек).

### P1 — High priority (після P0, до масштабування функціоналу)

- [ ] Додати керування "за часами" у розширеному вигляді (кілька вікон/днів тижня/профілів) поверх
  поточного schedule.
- [ ] Додати екран `About/Support` (версія, політики, інструкція, контакти).
- [ ] Додати мінімальні non-fatal технічні події (діагностика причин фейлів alarm start/stop/sync).
- [ ] Додати інтеграційні тести критичного пайплайна (monitoring -> match -> alarm -> stop).
- [ ] Стабілізувати preview для `WordChip` у Compose (щоб прев'ю відкривалося без runtime-костилів і
  без деградації long-press поведінки в проді).

### P2 — Next wave / expansion

- [ ] Додати логін через Firebase і синхронізацію keywords між пристроями.
- [ ] Додати Wear OS companion (Pixel Watch/Galaxy Watch Wear OS): отримання сигналу тривоги на
  годиннику + кнопка stop + синхронний stop тривоги на телефоні.
- [ ] Додати feature flags для ризикових фіч (cloud sync, wearable sync) для контрольованого
  rollout.
- [ ] Додати multi-presets для `Keywords`: імпорт готових пресетів без ручного вводу, незалежне
  керування багатьма пресетами, одночасна активація кількох пресетів (`ON/OFF` по кожному),
  окреме збереження та експорт/імпорт для кожного пресету.

## 11) Правило оновлення цього README

Після кожної суттєвої зміни:

1. Оновити секцію `Поточний стан реалізації (as-is)`.
2. Оновити чекбокси в `Живий TODO-план`.
3. Додати короткий запис у форматі:
    - `YYYY-MM-DD — що зроблено, що залишилось`.

## 12) Change log (короткий)

- `2026-04-21` — **Onboarding overhaul + Settings guide link:**
  `OnboardingPageLayout` переведено на `OnboardingVisual` sealed interface (`VectorIcon` /
  `RasterImage`); permissions-сторінка отримала swap іконки red↔green залежно від стану дозволів;
  welcome-сторінка — privacy note у `actionContent`; keywords-сторінка — `bodySmall` хінт під
  кнопкою; тексти всіх 5 сторінок переписані для точності (EN/UK/RU).
  `OnboardingViewModel` переведено з `koinActivityViewModel()` на `koinViewModel()` (scope =
  NavBackStackEntry); `ShowManually` action видалено — свіжий ViewModel дає `currentPage = 0`
  автоматично; `canSkip` стає `true` з `observeCompleted` коли `isCompleted = true`.
  `onClose` у NavGraph: уніфікований `navigateUp()` + fallback до `navigate(Home)` замість
  `isFromSettings` branch.
  `LegalConsentScreen`: додано `Surface(errorContainer)` картку з disclaimer «⚠️ Important!».
  `SettingsUiContract` + `SettingsViewModel` + `LegalDocumentsSettingsSection` + `SettingsScreen`:
  третє посилання «Відкрити онбординг» у секції Info.
  Keywords import: `VartovyiDialog` з попередженням про перезапис якщо є наявні дані.
- `2026-04-04` — **Debug app name:** у debug-збірці назва додатку змінюється на `Vartovyi Debug` /
  `Вартовий Debug` (uk) через debug source set (`src/debug/res/values*/strings.xml`); release і
  маніфест не змінювались.
- `2026-04-03` — **P0 roadmap:** прибрано окремий OEM-only пункт; **ручне тестування** об’єднує
  різні розміри екранів і вендори (зокрема Xiaomi/Samsung/Huawei), QA, не in-app guide. У P0 додано
  **імпорт/експорт ключових слів**; відповідний пункт прибрано з P1. Раніше: vendor guide не
  планується; Crashlytics — наступний після інструкції (відкладено).
- `2026-04-03` — **Скидання даних:** `ResetAppToFactoryDefaultsUseCase` (Settings → Дані) та
  `ClearKeywordsScreenDataUseCase` (Keywords — нижня кнопка очищення списків); обидва сценарії
  зупиняють тривогу, вимикають моніторинг і синхронізують foreground-service; factory reset
  додатково
  очищує monitoring DataStore і журнал; use case не викликають інші use case — репозиторії +
  контролери, спільний `syncMonitoringRuntimeWithSettings` у `MonitoringRuntimeSync.kt`. README
  оновлено (as-is, §5, P0, §14.4).
- `2026-04-03` — **Legal consent:** гейт у `MainActivity` (loading, екран згоди з посиланнями на
  політику/умови в Custom Tabs, підтвердження зберігає версію в DataStore), `LegalConsentScreen` /
  `ViewModel`, репозиторій + use cases; константи URL і версії документів у
  `LegalDocumentsContract`;
  `CustomTabsHelper`; `MainViewModel` + `MainUiContract` для shell; повний `values-ru/strings.xml`;
  винесено допоміжні утиліти (`PermissionsChecker`, `TopBarScrollBehaviorHelper`). Залежність
  `androidx.browser:browser` для Custom Tabs (див. Version Catalog).
- `2026-04-03` — **AlarmService:** ранній `PARTIAL_WAKE_LOCK` на **30 с** у `onStartCommand`; після
  розрахунку тривалості lock перевизначається на `alarmDuration + 15 с` буфер; retry відкриття
  `AlarmActivity`: **500 ms**, **3** спроби. **Convention `BuildTypes`:** release `signingConfig` на
  `defaultConfig` для локальної збірки (креденшли не для публічного репо без заміни).
- `2026-03-31` — підготовлено та опубліковано юридичні сторінки в Google Sites:
  `Privacy Policy` (`https://sites.google.com/view/vartovyi-privacy-policy`) і `Terms of Use` (
  `https://sites.google.com/view/vartovyi-terms-of-use`); у P0 canonical legal URLs відмічено як
  виконані.
- `2026-03-30` — release signing перенесено з `app/build.gradle.kts` у `build-logic` convention
  (`AndroidApplicationConventionPlugin` + `application/BuildTypes.kt`); для `:data` додано
  `consumer-rules.pro` (фікс падіння `mergeReleaseConsumerProguardFiles`). `Settings`: при
  поверненні
  з `RingtonePicker` стан розкритої секції зберігається. `Keywords`: tooltip доповнено інформацією
  про case-insensitive matching; виправлено довгі чіпси (іконка видалення не зникає); додано
  confirm-діалог перед видаленням чіпсів. `AlarmService`: `WakeLock` з timeout
  `alarmDuration + 30 с` (замість фіксованого максимуму).
- `2026-03-26` — `Settings`: секції переведено в accordion (анімоване розкриття, іконка `down`,
  одночасно відкрита тільки одна секція), уніфіковано фон/контраст карток та заголовків; додано
  автозгортання секцій при покиданні `Settings` з винятком `Test Alarm` overlay. `MainActivity`:
  `appRootBackground` оновлено — "блукаючий" градієнт працює і для `INACTIVE`, і для `ACTIVE`;
  магічні числові параметри анімації винесено в константи. Сформовано пріоритезований backlog для
  релізу в Store (P0/P1/P2).
- `2026-03-25` — Додано composite `build-logic` з convention-плагінами `vartovyi.*`, розширено
  Version Catalog (`bundles`, метадані SDK/app), type-safe `projects.domain` / `projects.data`;
  інструкції з Gradle зібрано в `CLAUDE.md` (§ Gradle, Version Catalog, and build-logic). Узгоджено
  модулі з кодом: `:domain` (JVM) — use cases, інтерфейси репозиторіїв, `useCaseModule`; `:data`
  (Android) — DataStore, Room, реалізації репозиторіїв, `databaseModule` / `repositoryModule`; у
  `:data` реалізації позначені `internal` де можливо. README (§4.1, §5, §6, Domain/Data as-is)
  оновлено.
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
- `2026-03-19` — у `Settings` додано вибір мелодії тривоги через системний `RingtonePicker`
  (прослуховування + збереження + відображення назви), loading-state на первинне завантаження
  налаштувань і уніфіковано ключові кнопки через спільний `VartovyiActionButton`.
- `2026-03-20` — `Settings`: секції Дані/Звук/Розклад, ліміт журналу та налаштовуваний cooldown;
  фільтр вхідних сповіщень лише для `org.telegram.messenger`; прибрано зайві prefs (вібрація, список
  пакетів). `Log`: presentation state у ViewModel, `LogErrorState` + retry. Top bar `enterAlways` на
  `Keywords`; тактильний відгук на кроках слайдерів у `VartovyiSettingSlider`; оновлено README.
- `2026-03-21` — Поліровка UI: анімований кореневий фон `MainActivity` за станом моніторингу;
  на `Home` при активному моніторингу — кільця на весь контент з центром на іконці щита, пульс
  іконки, пульс кнопки «Активувати» лише у вимкненому стані; винесено спільну анімацію для
  `LoadingOverlay` у `MonitoringActiveIconSignals.kt`. `AlarmActivity`: анімований alarm-фон з
  центром
  на іконці, синхронний пульс іконки та кнопки вимкнення; узгоджено стиль викликів Compose з
  `CLAUDE.md` (порядок `modifier`, `LaunchedEffect` після `remember` де потрібно); README оновлено
  (as-is, UI-спека, changelog).

## 13) Узгоджені продукт-рішення (зафіксовано)

- Логування: зберігаємо **всі** перехоплені Telegram-повідомлення (включно з пропущеними).
- Channel filter: первинна реалізація через `notification title` в режимі **exact
  match + `ignoreCase`** (без `contains`).
- Monitoring default state: при першому запуску — `OFF`.
- Alarm duration mode: використовуємо числове значення в секундах (`Int`) з кроком слайдера в UI.
- Canonical legal URLs (Google Sites) — дублюються в коді як `PRIVACY_POLICY_URL` /
  `TERMS_OF_USE_URL` у [
  `LegalDocumentsContract`](domain/src/main/kotlin/com/revakovskyi/vartovyi/constants/LegalDocumentsContract.kt):
  - Privacy Policy: `https://sites.google.com/view/vartovyi-privacy-policy`
  - Terms of Use: `https://sites.google.com/view/vartovyi-terms-of-use`
- Юридичні сторінки: **тільки дельти до вже опублікованого тексту** —
  [`LEGAL_PATCH_ONLY.md`](LEGAL_PATCH_ONLY.md). Повні тексти (як архів) —
  [`LEGAL_COPY_PASTE_MASTER.md`](LEGAL_COPY_PASTE_MASTER.md); старі гайди —
  [`PRIVACY_POLICY_SITE_GUIDE.md`](PRIVACY_POLICY_SITE_GUIDE.md),
  [`TERMS_OF_USE_SITE_GUIDE.md`](TERMS_OF_USE_SITE_GUIDE.md).

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
    - Коли моніторинг **активний:** пульс іконки щита + «сигнальні» кільця по всьому контенту Home,
      центр кілець збігається з центром іконки; коли **вимкнено** — легкий пульс на кнопці
      «Активувати».
    - Картка ключових слів (включно з empty-state).
    - Картка останнього спрацювання (включно з empty-state).

- **Keywords**
    - Trigger rules (режими `WORD` / `ALL_WORDS` / `PHRASE`, додавання/видалення, tooltip).
    - Stop words (додавання/видалення, tooltip, візуально відмінні chips).
    - Telegram channel filter (toggle + список каналів, якщо увімкнено).
  - Знизу екрана — `KeywordsClearButton` (як `LogClearButton`: скрол з `weight(1f)` + фіксована
    кнопка), enabled коли є збережені дані для очищення; confirm-діалог; після підтвердження —
    `ClearKeywordsScreenDataUseCase` (тривога стоп, моніторинг вимкнено, keywords DataStore
    очищено).

- **Log**
    - Список перехоплених подій (включно з пропущеними).
    - Стани елементів: alarm / skipped.
    - Очищення через confirm-діалог.
    - Empty-state, loading, error + retry.
    - Відображення списку керується станом з `ViewModel` (див. as-is).

- **Settings**
    - Кнопка `Test Alarm` зверху контенту (запуск/стоп тестової тривоги).
    - Якщо monitoring `ACTIVE`, тест тривоги блокується зі snackbar-підказкою та переходом на
      `Home`.
    - Під час первинного завантаження налаштувань показується `LoadingOverlay`.
    - Секція **Дані**: ліміт розміру журналу (чіпи + діалог-підказка), пауза між тривогами (чіпи +
      діалог), кнопка експорту (поки заглушка + snackbar); червона кнопка **Скинути налаштування**
      (outlined error) з confirm-діалогом — повне скидання до заводських (
      `ResetAppToFactoryDefaultsUseCase`).
    - Секція **Звук**: мелодія (`RingtonePicker` + назва), тривалість і гучність через
      `VartovyiSettingSlider` (з тактильним кроком).
    - Секція **Розклад роботи**: toggle, час початку/кінця, підказка для секції в діалозі.
  - Остання секція **Info** (accordion): текстові посилання (`bodyLink`, primary) на Privacy Policy,
    Terms of Use (Custom Tabs; згортання секції не скидається при відкритті посилання) та
    «Відкрити онбординг» (повторний перегляд онбордингу з першої сторінки).
  - Під усім контентом: `versionName` і produced by (приглушений колір, по центру, окремо від
    секцій).
    - Збереження параметрів у `DataStore` (без окремого списку Telegram-пакетів і без окремого
      перемикача вібрації в налаштуваннях).

- **Permissions**
    - Повний перелік критичних/рекомендованих дозволів.
    - Кнопки переходу в системні налаштування.
    - Статуси permission автооновлюються при поверненні на екран (`ON_RESUME`).

### 14.5 Alarm UX

- `AlarmActivity`: full-screen поверх lock screen.
- Контент: сирена, заголовок `ТРИВОГА`, одна кнопка вимкнення тривоги.
- Фон екрану: анімація в кольорах тривоги (пульсуючий радіальний відтінок + кільця від центру
  іконки); іконка та кнопка вимкнення мають узгоджений пульс масштабу.
- `AlarmActivity`: `Volume Up/Down` теж вимикають поточну тривогу.
- Окреме alarm-сповіщення: `HIGH`, `CATEGORY_ALARM`, full-screen intent, action `Вимкнути тривогу`,
  red accent, fallback-відкриття `AlarmActivity`.

### 14.6 Monitoring notification UX

- Коли моніторинг активний: ongoing foreground notification (`LOW`, без звуку, не свайпається).
- Tap по notification відкриває додаток.
- У notification є кнопка `Вимкнути`, яка зупиняє monitoring runtime + persisted state.
- Notification має зелений accent для індикації активного стану.

### 14.7 Shell / global chrome

- Єдина `MainActivity`: спочатку гейт legal consent (`LoadingOverlay` → `LegalConsentScreen` або
  основний UI), далі під контентом навігації — анімований кореневий фон, що змінюється за
  `MonitoringState` (неактивний / активний моніторинг), без хардкоду кольорів поза темою; стан
  моніторингу/тривоги для shell — через `MainViewModel`.

### 14.8 Notes for implementation

- Для channel filter використовуємо `title exact match + ignoreCase` як базову стратегію.
- Якщо в реальних тестах стабільність недостатня, додаємо fallback-режим як окрему, підтверджену
  зміну.
- `Alarm duration` моделюємо числовим значенням у секундах (`Int`) з обмеженням діапазону та кроком
  у UI.
