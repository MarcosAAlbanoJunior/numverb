# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
