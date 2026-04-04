# numverb

A Java library for converting numbers into words — supporting currencies and cardinals across multiple languages and locales.

```java
// Portuguese (Brazil) — default
NumVerb.currency("1234.68").toWords();
// → "mil duzentos e trinta e quatro reais e sessenta e oito centavos"

// English (United States)
NumVerb.currency("1234.68").language(Language.EN_US).currency(Currencies.USD).toWords();
// → "one thousand two hundred and thirty-four dollars and sixty-eight cents"
```

## Features

- **Currency conversion** — monetary values to their full textual representation
- **Cardinal conversion** — whole numbers to words, up to setilhão / septillion (10²⁴)
- **Multi-language** — `pt-BR` and `en-US`; adding a new language requires only a `.properties` file
- **Multi-currency** — BRL, USD, EUR, GBP; currency names are language-specific (`dólar` vs `dollar`)
- **Fluent API** — readable, chainable builder pattern
- **Type-safe constants** — `Language.PT_BR`, `Language.EN_US`, `Currencies.BRL`, `Currencies.USD`, `Currencies.EUR`, `Currencies.GBP`
- **Grammatical gender** — feminine forms applied correctly (`uma libra`, `duzentas libras`)
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
    <version>0.3.0</version>
</dependency>
```

Or with Gradle:

```groovy
implementation 'io.github.marcosaalbanojunior:numverb:0.3.0'
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

// English + USD
NumVerb.currency("1234.68").language(Language.EN_US).currency(Currencies.USD).toWords();
// → "one thousand two hundred and thirty-four dollars and sixty-eight cents"

// English + GBP — penny/pence subunit
NumVerb.currency("1.02").language(Language.EN_US).currency(Currencies.GBP).toWords();
// → "one pound and two pence"

// Portuguese + GBP — feminine gender applied automatically
NumVerb.currency("2.00").language(Language.PT_BR).currency(Currencies.GBP).toWords();
// → "duas libras"

// Round millions use "de" in pt-BR, plain space in en-US
NumVerb.currency("1000000.00").toWords();
// → "um milhão de reais"
NumVerb.currency("1000000.00").language(Language.EN_US).currency(Currencies.USD).toWords();
// → "one million dollars"

// Cents only
NumVerb.currency("0.50").toWords();
// → "cinquenta centavos"

// String overload prevents the common double-precision trap:
//   new BigDecimal(1.99)  →  1.9899999999999999911182...  ← wrong!
//   NumVerb.currency("1.99")  →  1.99                     ← correct
```

### Cardinal conversion

```java
import io.github.marcosaalbanojunior.numverb.NumVerb;
import io.github.marcosaalbanojunior.numverb.lang.Language;

// Portuguese (default)
NumVerb.cardinal(1234).toWords();
// → "mil duzentos e trinta e quatro"

// English
NumVerb.cardinal(1234).language(Language.EN_US).toWords();
// → "one thousand two hundred and thirty-four"

// BigDecimal overload — full range up to setilhão / septillion (10²⁴)
NumVerb.cardinal(new BigDecimal("1000000000000000000000000")).toWords();
// → "um setilhão"
NumVerb.cardinal(new BigDecimal("1000000000000000000000000")).language(Language.EN_US).toWords();
// → "one septillion"

// BigInteger overload
NumVerb.cardinal(new BigInteger("2000000000000000000000")).toWords();
// → "dois sextilhões"

// Zero
NumVerb.cardinal(0).toWords();
// → "zero"
```

## Supported languages

| Code    | Constant          | Language              |
|---------|-------------------|-----------------------|
| `pt-BR` | `Language.PT_BR`  | Portuguese (Brazil)   |
| `en-US` | `Language.EN_US`  | English (United States) |

Use the typed constants to get compile-time safety and IDE auto-complete.

> Adding a new language requires only a `.properties` file under `src/main/resources/languages/`
> and registering the code in `LanguageRules.SUPPORTED`. No Java classes need to change.
> See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## Supported currencies

Currency names are language-specific. The same ISO 4217 code (`USD`, `GBP`, etc.) resolves
to the correct name for the requested language automatically.

| Code  | pt-BR                        | en-US                      | Constant         |
|-------|------------------------------|----------------------------|------------------|
| `BRL` | real / reais                 | real / reals               | `Currencies.BRL` |
| `USD` | dólar / dólares              | dollar / dollars           | `Currencies.USD` |
| `EUR` | euro / euros                 | euro / euros               | `Currencies.EUR` |
| `GBP` | libra / libras *(feminine)*  | pound / pounds             | `Currencies.GBP` |

All subunits use `centavo`/`centavos` for pt-BR; `cent`/`cents` for en-US (except GBP,
which uses `penny`/`pence` in both languages).

## Error handling

All exceptions are unchecked (`RuntimeException`):

| Exception                      | When thrown                                                                                        |
|--------------------------------|----------------------------------------------------------------------------------------------------|
| `UnsupportedLanguageException` | The requested language code is not supported                                                       |
| `UnsupportedCurrencyException` | The currency is not configured for the requested language — message includes both codes            |
| `NumberOutOfRangeException`    | The value is negative or exceeds the maximum supported range (currency and cardinal)               |
| `IllegalStateException`        | A `.properties` file exists but is missing a required key                                          |
| `NullPointerException`         | `null` is passed to `currency()`, `cardinal()`, `language()`, or `currency(String)`               |
| `NumberFormatException`        | An invalid string is passed to `NumVerb.currency(String)`                                          |

```java
// Language is always validated first, regardless of the value
try {
    NumVerb.currency("1.00").language("fr-FR").toWords();
} catch (UnsupportedLanguageException e) {
    // "Unsupported language: fr-FR"
}

// Clear message when a currency is not configured for the requested language
try {
    NumVerb.currency("1.00").language("pt-BR").currency("JPY").toWords();
} catch (UnsupportedCurrencyException e) {
    // "Currency 'JPY' is not supported for language 'pt-BR'"
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
