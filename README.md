# numverb

A Java library for converting numbers into words — supporting currencies and cardinals across multiple languages and locales.

```java
NumVerb.currency(new BigDecimal("1234.68")).toWords();
// → "mil duzentos e trinta e quatro reais e sessenta e oito centavos"

NumVerb.cardinal(1234).language("pt-BR").toWords();
// → "mil duzentos e trinta e quatro"
```

## Features

- **Currency conversion** — monetary values (`BigDecimal`) to their full textual representation
- **Cardinal conversion** — whole numbers (`long`) to words
- **Fluent API** — readable, chainable builder pattern
- **Multi-currency** — BRL, USD, EUR (ISO 4217 codes)
- **Extensible** — languages and currencies are driven by `.properties` files
- **Thread-safe** — all public methods share stateless converters
- **Zero dependencies** — no runtime dependencies beyond the JDK

## Requirements

- Java 17+
- Maven 3.6+ (to build from source)

## Installation

Add the dependency to your Maven project:

```xml
<dependency>
    <groupId>io.github.marcosaalbanojunior</groupId>
    <artifactId>numverb</artifactId>
    <version>0.1.0</version>
</dependency>
```

Or with Gradle:

```groovy
implementation 'io.github.marcosaalbanojunior:numverb:0.1.0'
```

## Usage

### Currency conversion

```java
import io.github.marcosaalbanojunior.numverb.NumVerb;

import java.math.BigDecimal;

// Default: pt-BR + BRL
String result = NumVerb.currency(new BigDecimal("1234.68")).toWords();
// → "mil duzentos e trinta e quatro reais e sessenta e oito centavos"

        // Explicit language and currency
        String result = NumVerb.currency(new BigDecimal("1234.68"))
                .language("pt-BR")
                .currency("USD")
                .toWords();
// → "mil duzentos e trinta e quatro dólares e sessenta e oito centavos"

        // EUR
        String result = NumVerb.currency(new BigDecimal("100.00"))
                .currency("EUR")
                .toWords();
// → "cem euros"

        // Cents only
        String result = NumVerb.currency(new BigDecimal("0.50")).toWords();
// → "cinquenta centavos"
```

### Cardinal conversion

```java
// Default: pt-BR
String result = NumVerb.cardinal(1234).toWords();
// → "mil duzentos e trinta e quatro"

// Explicit language
String result = NumVerb.cardinal(1_000_000).language("pt-BR").toWords();
// → "um milhão"

// Zero
String result = NumVerb.cardinal(0).toWords();
// → "zero"
```

## Supported languages

| Code    | Language           |
|---------|--------------------|
| `pt-BR` | Portuguese (Brazil)|

> Support for additional languages is planned. See [CONTRIBUTING.md](CONTRIBUTING.md) to add a new one.

## Supported currencies

| Code  | Currency        |
|-------|-----------------|
| `BRL` | Brazilian Real  |
| `USD` | US Dollar       |
| `EUR` | Euro            |

## Error handling

All exceptions are unchecked (`RuntimeException`):

| Exception                    | When thrown                                               |
|------------------------------|-----------------------------------------------------------|
| `UnsupportedLanguageException` | The requested language code has no `.properties` file   |
| `UnsupportedCurrencyException` | The requested currency code has no `.properties` file   |
| `NumberOutOfRangeException`    | The value exceeds the supported range (up to 10²⁴)      |
| `NullPointerException`         | `null` is passed to `currency()`, `language()`, or `currency(String)` |

```java
try {
    NumVerb.currency(new BigDecimal("1.00")).language("fr-FR").toWords();
} catch (UnsupportedLanguageException e) {
    // "Unsupported language: fr-FR"
}
```

## Building and testing

```bash
# Compile and run all tests
mvn verify

# Generate Javadoc
mvn javadoc:javadoc
```

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

## License

This project is licensed under the [MIT License](LICENSE).