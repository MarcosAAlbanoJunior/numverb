# numverb

A Java library for converting numbers into words — supporting currencies and cardinals across multiple languages and locales.

```java
// Convenience string overload — no BigDecimal boilerplate, no floating-point surprises
NumVerb.currency("1234.68").toWords();
// → "mil duzentos e trinta e quatro reais e sessenta e oito centavos"

// Full range cardinal (up to setilhão, 10²⁴)
NumVerb.cardinal(new BigDecimal("1000000000000000000000000")).toWords();
// → "um setilhão"
```

## Features

- **Currency conversion** — monetary values to their full textual representation
- **Cardinal conversion** — whole numbers to words, up to setilhão (10²⁴)
- **Fluent API** — readable, chainable builder pattern
- **Multi-currency** — BRL, USD, EUR (ISO 4217 codes)
- **Type-safe constants** — `Language.PT_BR`, `Currencies.BRL`, `Currencies.USD`, `Currencies.EUR`
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
    <version>0.2.0</version>
</dependency>
```

Or with Gradle:

```groovy
implementation 'io.github.marcosaalbanojunior:numverb:0.2.0'
```

## Usage

### Currency conversion

```java
import io.github.marcosaalbanojunior.numverb.Currencies;
import io.github.marcosaalbanojunior.numverb.NumVerb;
import io.github.marcosaalbanojunior.numverb.lang.Language;

// Default: pt-BR + BRL
NumVerb.currency("1234.68").toWords();
// → "mil duzentos e trinta e quatro reais e sessenta e oito centavos"

// With typed constants — avoids magic strings and typos
NumVerb.currency("1234.68")
        .language(Language.PT_BR)
        .currency(Currencies.USD)
        .toWords();
// → "mil duzentos e trinta e quatro dólares e sessenta e oito centavos"

// EUR
NumVerb.currency("100.00").currency(Currencies.EUR).toWords();
// → "cem euros"

// Cents only
NumVerb.currency("0.50").toWords();
// → "cinquenta centavos"

// Round millions use "de" (pt-BR grammar)
NumVerb.currency("1000000.00").toWords();
// → "um milhão de reais"

// String overload prevents the common double-precision trap:
//   new BigDecimal(1.99)  →  1.9899999999999999911182...  ← wrong!
//   new BigDecimal("1.99")  →  1.99                       ← correct
//   NumVerb.currency("1.99")  →  1.99                     ← correct shorthand
```

### Cardinal conversion

```java
import io.github.marcosaalbanojunior.numverb.NumVerb;
import io.github.marcosaalbanojunior.numverb.lang.Language;

// long overload — convenient for everyday values
NumVerb.cardinal(1234).toWords();
// → "mil duzentos e trinta e quatro"

// BigDecimal overload — full range up to setilhão (10²⁴), beyond Long.MAX_VALUE
NumVerb.cardinal(new BigDecimal("1000000000000000000000000")).toWords();
// → "um setilhão"

// BigInteger overload
NumVerb.cardinal(new BigInteger("2000000000000000000000")).toWords();
// → "dois sextilhões"

// Typed language constant
NumVerb.cardinal(1_000_000).language(Language.PT_BR).toWords();
// → "um milhão"

// Zero
NumVerb.cardinal(0).toWords();
// → "zero"
```

## Supported languages

| Code    | Language            |
|---------|---------------------|
| `pt-BR` | Portuguese (Brazil) |

Use the `Language.PT_BR` constant instead of the raw string to get compile-time safety.

> Support for additional languages is planned. See [CONTRIBUTING.md](CONTRIBUTING.md) to add a new one.

## Supported currencies

| Code  | Currency       | Constant          |
|-------|----------------|-------------------|
| `BRL` | Brazilian Real | `Currencies.BRL`  |
| `USD` | US Dollar      | `Currencies.USD`  |
| `EUR` | Euro           | `Currencies.EUR`  |

## Error handling

All exceptions are unchecked (`RuntimeException`):

| Exception                      | When thrown                                                                          |
|--------------------------------|--------------------------------------------------------------------------------------|
| `UnsupportedLanguageException` | The requested language code has no `.properties` file or grammar implementation      |
| `UnsupportedCurrencyException` | The requested currency code has no `.properties` file                                |
| `NumberOutOfRangeException`    | The value is negative or exceeds the maximum supported range                         |
| `IllegalStateException`        | A currency `.properties` file exists but is missing a required property key          |
| `NullPointerException`         | `null` is passed to `currency()`, `cardinal()`, `language()`, or `currency(String)`  |
| `NumberFormatException`        | An invalid string is passed to `NumVerb.currency(String)`                            |

```java
try {
    NumVerb.currency("1.00").language("fr-FR").toWords();
} catch (UnsupportedLanguageException e) {
    // "Unsupported language: fr-FR"
}
```

## Building and testing

```bash
# Compile and run all tests
mvn test -Dgpg.skip=true

# Full verify (includes Javadoc and source JARs)
mvn verify -Dgpg.skip=true

# Generate Javadoc
mvn javadoc:javadoc
```

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
