# Contributing to numverb

Thank you for your interest in contributing!

## Adding a new language

1. Create `src/main/resources/languages/{code}.properties` following the structure of `pt-BR.properties`
2. Register the language code in `LanguageRules.SUPPORTED`
3. Add grammar-specific handling in `LanguageRules` if the language has unique rules
4. Add tests in `src/test/`

## Adding a new currency

1. Create `src/main/resources/currencies/{CODE}.properties` following the structure of `BRL.properties`
2. No Java code changes needed — the library will pick it up automatically

## Running tests

```bash
mvn clean test
```

## Code style

- Java 17
- No external dependencies beyond JUnit 5 for tests
- All public methods must have Javadoc
- No warnings, no unused imports
