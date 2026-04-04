# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.1] - 2026-04-04

### Fixed
- **`CardinalConverter` missing upper-bound check** — cardinal values exceeding the maximum
  supported range (999 setilhões / 999 septillion) previously escaped with an internal
  `IllegalArgumentException` or `ArithmeticException`. They now throw
  `NumberOutOfRangeException`, consistent with `CurrencyConverter`.
- **BRL plural in en-US corrected from `reals` to `reais`** — English financial media
  (Bloomberg, Reuters, BCB) consistently uses the Portuguese plural "reais"; "reals" is
  an anglicisation that is not in common use.
- **Dead code removed in `LanguageRules.joinParts`** — an unreachable
  `if (parts.isEmpty()) return ""` guard was present; removed as `joinParts` is only
  ever called when the number is non-zero.

### Tests
- Added explicit output assertions for **pt-BR + USD** (singular, plural, cents, round
  million with "de" preposition) — previously only a no-throw check existed.
- Added explicit output assertions for **pt-BR + EUR** (singular, plural, with cent,
  round million with "de" preposition) — previously only a no-throw check existed.
- Added cardinal over-range and at-max boundary tests for both `pt-BR` and `en-US`.

## [0.3.0] - 2026-04-04

### Added
- **English (United States) — `en-US` language support** via `Language.EN_US`.
  - Cardinal numbers with hyphen between tens and units: "twenty-one", "ninety-nine".
  - "and" connector after hundreds and before small scale remainders:
    "one hundred and twenty-one", "one thousand and one".
  - Thousand scale always includes its count: "one thousand" (not just "thousand" as in pt-BR).
  - No "of" preposition before currency: "one million dollars" (not "one million of dollars").
  - Scale words do not pluralise when used as cardinal adjectives: "two million", "three billion".
- **`Language.EN_US`** typed constant, consistent with existing `Language.PT_BR`.
- **`Currencies.GBP`** constant — British Pound Sterling.
  - `pt-BR`: libra / libras (feminine gender), subunit penny / pence.
  - `en-US`: pound / pounds, subunit penny / pence.
- **All four currencies now available for both languages** — `BRL`, `USD`, `EUR`, `GBP`
  are fully supported for both `pt-BR` and `en-US`. Currency names are resolved
  automatically per language from the same ISO 4217 code:

  | Code  | pt-BR                       | en-US           |
  |-------|-----------------------------|-----------------|
  | `BRL` | real / reais                | real / reals    |
  | `USD` | dólar / dólares             | dollar / dollars|
  | `EUR` | euro / euros                | euro / euros    |
  | `GBP` | libra / libras *(feminine)* | pound / pounds  |

- **Language-specific currency files** — currency names are stored under
  `/currencies/{languageCode}/{currencyCode}.properties`. The same ISO 4217 code
  resolves to the correct name and gender for the active language automatically.

### Changed
- **Architecture refactor — `LanguageRules` split into three focused classes**
  (no public API changes):
  - `GrammarConfig` (new) — immutable value-object built from the `.properties` file;
    centralises all grammar decisions so that `LanguageRules` and `NumberSpeller`
    contain zero language-specific `if` blocks.
  - `NumberSpeller` (new) — converts integers in [0, 999] to words; entirely
    properties-driven, reusable across any language.
  - `LanguageRules` — now a thin orchestrator (~135 lines). **Adding a new language
    requires only a `.properties` file; no Java changes needed.**
- **`CurrencyProvider.getCurrency`** now accepts `(languageCode, currencyCode)`.
  Currency name files live under `/currencies/{languageCode}/` subdirectories.
- Two new grammar properties, configurable per language:
  - `scale.mil.skip-one` — `true` to omit the "1" prefix before the thousand word
    (pt-BR: `"mil"` not `"um mil"`; en-US: `false`).
  - `tens.separator` — separator placed between a tens word and a units word,
    including any surrounding whitespace (absent → `" {connector} "`; en-US: `"-"`).

### Fixed
- **Negative cardinal returned an empty string silently** — `NumVerb.cardinal(-1).toWords()`
  previously returned `""`. It now throws `NumberOutOfRangeException`, consistent with
  `CurrencyConverter`.
- **`UnsupportedLanguageException` now always takes precedence over other exceptions** —
  previously `cardinal(-1).language("xx-XX")` threw `NumberOutOfRangeException` because
  the value was checked before the language. Language is now validated first in both
  `CurrencyBuilder` and `CardinalBuilder`.
- **`UnsupportedCurrencyException` message now identifies both codes** — previously
  "Unsupported currency: GBP" gave no hint about the language context. Message is now
  "Currency 'GBP' is not supported for language 'pt-BR'".
- **`CurrencyConverter` now fails fast on null currency** — passing a `ConversionContext`
  with `currency = null` to `CurrencyConverter` now throws `NullPointerException`
  immediately at the top of `convert()` instead of deep inside `buildIntegerPart`.
- **Dead method `NumberSpeller.spell` removed** — the method was defined but never called.
  Its bounds validation is now in `spellNonZero`, which throws `IllegalArgumentException`
  for values outside [0, 999] instead of the confusing "Missing language property" error
  that the previous code would have produced.

## [0.2.0] - 2026-04-03

### Added
- `NumVerb.currency(String value)` overload — pass the amount as a String to avoid
  floating-point precision issues that arise from `new BigDecimal(double)`.
- `NumVerb.cardinal(BigDecimal value)` overload — converts cardinal numbers beyond
  `Long.MAX_VALUE` (up to setilhão, 10²⁴).
- `NumVerb.cardinal(BigInteger value)` overload — same range, direct `BigInteger` input.
- `CurrencyBuilder.language(Language language)` and
  `CardinalBuilder.language(Language language)` overloads — use the typed
  `Language.PT_BR` constant instead of a raw string.
- `Currencies` constants class with `BRL`, `USD`, `EUR` fields for type-safe currency
  selection without magic strings.
- `currency.subunit.gender` property in all currency `.properties` files — the subunit
  (e.g., centavo) now has an independent grammatical gender, separate from the main
  currency unit.
- `scale.gender` property in language `.properties` files — controls the gender used
  when counting scale words (mil, milhões, bilhões, etc.), making it explicit and
  configurable per language.
- `currency.preposition` property in language `.properties` files — the preposition
  used between a round-million amount and the currency word (e.g., `de` for pt-BR).

### Fixed
- **Gender propagation bug in scale counts**: The gender of the currency (e.g.,
  `feminine`) was incorrectly applied when spelling out scale word counts. This
  produced wrong forms like "duas mil libras" or "duzentas milhões". Scale counts
  now always use the gender defined by `scale.gender` in the language file (masculine
  in pt-BR), while the passed-in gender is correctly applied only to the final
  remainder (< 1000).
- **Language-specific "de" preposition was hardcoded in `CurrencyConverter`**: The
  logic for "um milhão *de* reais" was inside the language-agnostic converter class,
  which would have broken for non-Portuguese languages. It is now delegated to
  `LanguageRules.getCurrencyJoiner()`, controlled by the `currency.preposition`
  property. Languages without this property default to a plain space.
- **`CurrencyProvider` silent failure on malformed property files**: A `.properties`
  file that existed but was missing a required key (e.g., `currency.singular`) would
  cause an opaque `NullPointerException` deep in the record constructor. The provider
  now validates all required keys upfront and throws an `IllegalStateException` with a
  clear message naming the missing property and the file.

## [0.1.0] - 2026-04-03

> First public release — published to Maven Central.

### Added
- Initial release
- Currency conversion to words (BigDecimal → String)
- Portuguese (Brazil) — `pt-BR` language support
- BRL, USD, EUR currency support
- Properties-based language and currency configuration
- Fluent API: `NumVerb.currency(...).language(...).currency(...).toWords()`
- Cardinal number conversion via `NumVerb.cardinal(...).language(...).toWords()`
- Support for numbers up to setilhão (10²⁴)
- Comprehensive unit tests with JUnit 5
- Full Javadoc documentation
