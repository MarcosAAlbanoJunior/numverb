# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
