# Unit Tests Plan — Trigger Words Input Field

## Production Code Map

### Domain Layer

#### Models

- `domain/.../model/TriggerKeywordRule.kt`
    - `normalizedSignature()` — для дедублікації
    - `matches(text)` — WORD / ALL_WORDS / PHRASE
- `domain/.../model/TriggerKeywordRuleType.kt` — enum: `WORD`, `ALL_WORDS`, `PHRASE`
- `domain/.../model/KeywordsBackup.kt` — для export/import

#### Result types

- `domain/.../result/KeywordSanitizationResult.kt`
    - `Empty`, `MultiLineDetected`, `TermTooShort`,
      `Sanitized(effectiveType, storageValue, normalizedRawInput)`
- `domain/.../result/RestoreDefaultKeywordsResult.kt`
    - `Added(count)`, `NothingAdded`

#### Constants

- `domain/.../constants/KeywordsLimits.kt`
    - `MAX_TOTAL_KEYWORDS = 200`
    - `MIN_TERM_LENGTH = 2`
    - `MAX_TOKENS_FOR_ALL_WORDS_PROMOTION = 4`
- `domain/.../constants/KeywordRuleFormat.kt` — сепаратори, regex-патерни
- `domain/.../constants/DefaultKeywordsSeed.kt` — список дефолтних слів

#### Utils

- `domain/.../utils/InputNormalization.kt`
    - `String.normalizeApostrophes()` — замінює варіанти апострофів на `ʼ`
    - `String.normalizeUnicode()` — NFKC нормалізація
- `domain/.../utils/TriggerKeywordRuleParser.kt`
    - `parseTriggerKeywordRuleFromStorage(storageValue)` — парсить формат сховища в
      `TriggerKeywordRule`

#### Repository Interface

- `domain/.../repository/KeywordsRepository.kt`
    - Flows: `keywords`, `stopWords`, `telegramChannels`, `isTelegramChannelFilterEnabled`
    - Functions: `addKeyword`, `removeKeyword`, `addStopWord`, `removeStopWord`,
      `addTelegramChannel`,
      `removeTelegramChannel`, `setTelegramChannelFilterEnabled`, `seedDefaultKeywordsIfNeeded`,
      `restoreDefaultKeywords`, `clearAllKeywordsPreferences`

#### Use Cases

- `SanitizeKeywordInputUseCase` / `SanitizeKeywordInputUseCaseImpl` — **головна логіка валідації**
- `AddKeywordUseCase` / `RemoveKeywordUseCase`
- `AddStopWordUseCase` / `RemoveStopWordUseCase`
- `AddTelegramChannelUseCase` / `RemoveTelegramChannelUseCase`
- `ToggleTelegramChannelFilterUseCase`
- `ObserveKeywordsUseCase` / `ObserveStopWordsUseCase` / `ObserveTelegramChannelsUseCase` /
  `ObserveTelegramChannelFilterEnabledUseCase`
- `RestoreDefaultKeywordsUseCase`
- `SeedDefaultKeywordsUseCase`
- `ExportKeywordsUseCase` / `ImportKeywordsUseCase`
- `ClearKeywordsScreenDataUseCase`

### Data Layer

#### Repository Implementation

- `data/.../repository/KeywordsRepositoryImpl.kt`
    - Blank/trim-guard перед делегуванням до DataStore
    - `restoreDefaultKeywords()` → `mergeKeywords(DEFAULT_KEYWORDS_SEED)` →
      `RestoreDefaultKeywordsResult`

#### DataStore

- `data/.../datastore/KeywordsDataStore.kt`
    - `addKeywordIfMissing` — ігнорує дублікати
    - `removeKeyword` — видаляє зі списку
    - `mergeKeywords` — повертає кількість доданих
    - `seedDefaultKeywordsIfNeeded` — виконується лише один раз (прапорець `KEYWORDS_SEEDED`)
    - Серіалізація через `kotlinx.serialization.Json`

### App Layer (UI)

#### UiContract

- `app/.../keywords/KeywordsUiContract.kt`
    - `State`: `inputKeyword`, `selectedTriggerKeywordRuleType`, `keywords`, `duplicateWord`,
      `pendingRemoval`, ліміти
    - `Action`: `UpdateKeywordInput`, `SelectTriggerKeywordRuleType`, `AddKeyword`,
      `RemoveKeyword`, ...
    - `Event`: `KeywordAdded`, `KeywordNormalized`, `KeywordMultiLineNotAllowed`,
      `KeywordTermTooShort`,
      `KeywordsMaxReached`, `KeywordRemoved`, ...

#### ViewModel

- `app/.../keywords/KeywordsViewModel.kt`
    - `addKeyword()`: викликає `sanitizeKeywordInputUseCase`, потім перевіряє дублікат і ліміт 200,
      потім `addKeywordUseCase`
    - `addStopWord()`: trim + довжина >= 2 + case-insensitive дублікат
    - `addTelegramChannel()`: аналогічно до `addStopWord()`

---

## Test Plan

### ✅ 1. `SanitizeKeywordInputUseCase` (priority: HIGH — pure logic, no deps)

| Group                         | Test cases                                                                            |
|-------------------------------|---------------------------------------------------------------------------------------|
| **Empty input**               | `""` → `Empty`; `"   "` → `Empty`; invisible chars only → `Empty`                     |
| **Multiline**                 | `"abc\ndef"` → `MultiLineDetected`; `"abc\r\ndef"` → `MultiLineDetected`              |
| **Short term**                | single char → `TermTooShort`; exactly 2 chars → `Sanitized` (boundary)                |
| **Unicode normalization**     | invisible chars removed before empty check; apostrophe variants normalized            |
| **WORD type**                 | single word → `Sanitized(effectiveType=WORD, storageValue="word")`                    |
| **Auto-promote to ALL_WORDS** | 2 tokens, `selectedType=WORD` → `effectiveType=ALL_WORDS`, `storageValue="a+b"`       |
| **Auto-promote to ALL_WORDS** | 4 tokens (boundary), `selectedType=WORD` → `effectiveType=ALL_WORDS`                  |
| **Auto-promote to PHRASE**    | 5+ tokens, `selectedType=WORD` → `effectiveType=PHRASE`, `storageValue='"a b c d e"'` |
| **Explicit PHRASE type**      | 2 words, `selectedType=PHRASE` → `effectiveType=PHRASE`                               |
| **Balanced outer quotes**     | `'"abc def"'` → detects PHRASE intent, strips quotes, `storageValue='"abc def"'`      |
| **Unbalanced quotes**         | `'"abc def'` (no closing) → NOT treated as phrase, treated as WORD/ALL_WORDS          |
| **Phrase sanitization**       | leading/trailing non-alphanum stripped; internal whitespace collapsed                 |
| **ALL_WORDS type selected**   | 2 tokens, `selectedType=ALL_WORDS` → stays `ALL_WORDS`                                |
| **normalizedRawInput**        | equals `trimmed` input (before type resolution), not the `storageValue`               |
| **Apostrophe normalization**  | `"don't"` with curly apostrophe → normalized to standard variant                      |

### 🔲 2. `TriggerKeywordRule.normalizedSignature()` and `matches()`

| Test cases                                                  |
|-------------------------------------------------------------|
| Same content, different case → same `normalizedSignature()` |
| WORD: found as standalone word in text                      |
| WORD: not found as substring (partial match)                |
| ALL_WORDS: all words present in any order → matches         |
| ALL_WORDS: one word missing → no match                      |
| PHRASE: exact phrase present → matches                      |
| PHRASE: words present but not consecutive → no match        |

### 🔲 3. `parseTriggerKeywordRuleFromStorage()`

| Test cases                                       |
|--------------------------------------------------|
| Plain word → `WORD`, single term                 |
| `"a+b"` → `ALL_WORDS`, terms = ["a", "b"]        |
| `'"abc def"'` → `PHRASE`, terms = ["abc", "def"] |

### 🔲 4. `KeywordsViewModel.addKeyword()` (via `onAction(AddKeyword)`)

| Group              | Test cases                                                                                        |
|--------------------|---------------------------------------------------------------------------------------------------|
| **Empty input**    | `inputKeyword=""` → no `addKeywordUseCase` call, no event                                         |
| **Multiline**      | → `Event.KeywordMultiLineNotAllowed` emitted                                                      |
| **Short term**     | → `Event.KeywordTermTooShort(minLength=2)` emitted                                                |
| **Duplicate**      | → `state.duplicateWord` set, `inputKeyword` cleared, `addKeywordUseCase` NOT called               |
| **Max limit**      | 200 keywords in state → `Event.KeywordsMaxReached(max=200)`                                       |
| **Success**        | → `addKeywordUseCase` called with correct `storageValue`; `inputKeyword=""`; `Event.KeywordAdded` |
| **Normalized**     | sanitized differs from raw → additionally `Event.KeywordNormalized(displayValue)`                 |
| **Type selection** | `SelectTriggerKeywordRuleType(PHRASE)` → `state.selectedTriggerKeywordRuleType == PHRASE`         |

### 🔲 5. `KeywordsRepositoryImpl`

| Test cases                                                     |
|----------------------------------------------------------------|
| `addKeyword("")` → does NOT call DataStore                     |
| `addKeyword("  abc  ")` → calls DataStore with trimmed `"abc"` |
| `restoreDefaultKeywords()` with 0 added → `NothingAdded`       |
| `restoreDefaultKeywords()` with N added → `Added(count=N)`     |

---

## Tech Stack (Testing)

- **JUnit 5** (Jupiter) via `vartovyi.test.unit` convention plugin
- **MockK** — mocking
- **AssertK** — assertions
- **Turbine** — Flow testing
- **kotlinx-coroutines-test** — coroutine testing
