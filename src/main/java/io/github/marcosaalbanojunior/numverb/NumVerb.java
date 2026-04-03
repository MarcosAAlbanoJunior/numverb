package io.github.marcosaalbanojunior.numverb;

import io.github.marcosaalbanojunior.numverb.converter.CardinalConverter;
import io.github.marcosaalbanojunior.numverb.converter.CurrencyConverter;
import io.github.marcosaalbanojunior.numverb.currency.Currency;
import io.github.marcosaalbanojunior.numverb.currency.CurrencyProvider;
import io.github.marcosaalbanojunior.numverb.lang.Language;
import io.github.marcosaalbanojunior.numverb.lang.LanguageProvider;
import io.github.marcosaalbanojunior.numverb.model.ConversionContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Main entry point for the <strong>numverb</strong> library.
 *
 * <p>Provides a fluent API for converting numbers to their word representation.</p>
 *
 * <pre>{@code
 * // Default: pt-BR + BRL
 * String result = NumVerb.currency(new BigDecimal("1234.68")).toWords();
 * // → "mil duzentos e trinta e quatro reais e sessenta e oito centavos"
 *
 * // Convenience: pass the amount as a String to avoid floating-point surprises
 * String result = NumVerb.currency("1234.68").toWords();
 *
 * // Explicit language and currency
 * String result = NumVerb.currency("1234.68")
 *     .language(Language.PT_BR)
 *     .currency(Currencies.USD)
 *     .toWords();
 *
 * // Cardinal number — long overload
 * String result = NumVerb.cardinal(1234).language("pt-BR").toWords();
 * // → "mil duzentos e trinta e quatro"
 *
 * // Cardinal number — full range (up to setilhão) via BigDecimal
 * String result = NumVerb.cardinal(new BigDecimal("1000000000000000000000000")).toWords();
 * // → "um setilhão"
 * }</pre>
 *
 * <p>All public methods are thread-safe.</p>
 *
 * @see Currencies
 * @see Language
 */
public final class NumVerb {

    private static final LanguageProvider LANGUAGE_PROVIDER = new LanguageProvider();
    private static final CurrencyProvider CURRENCY_PROVIDER = new CurrencyProvider();
    private static final CurrencyConverter CURRENCY_CONVERTER = new CurrencyConverter(LANGUAGE_PROVIDER);
    private static final CardinalConverter CARDINAL_CONVERTER = new CardinalConverter(LANGUAGE_PROVIDER);

    private NumVerb() {}

    // -------------------------------------------------------------------------
    // Currency factory methods
    // -------------------------------------------------------------------------

    /**
     * Starts a currency conversion for the given monetary value.
     *
     * <p>Defaults: language {@code "pt-BR"}, currency {@code "BRL"}.</p>
     *
     * @param value the monetary value; must not be null
     * @return a builder to configure and execute the conversion
     */
    public static CurrencyBuilder currency(BigDecimal value) {
        Objects.requireNonNull(value, "value cannot be null");
        return new CurrencyBuilder(value);
    }

    /**
     * Starts a currency conversion for the given monetary value expressed as a String.
     *
     * <p>This overload avoids floating-point precision issues that arise from
     * {@link BigDecimal#BigDecimal(double)}.
     * Prefer this over passing a {@code double} literal.</p>
     *
     * <p>Defaults: language {@code "pt-BR"}, currency {@code "BRL"}.</p>
     *
     * @param value the monetary value as a decimal string (e.g., {@code "1234.68"}); must not be null
     * @return a builder to configure and execute the conversion
     * @throws NumberFormatException if {@code value} is not a valid decimal string
     */
    public static CurrencyBuilder currency(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        return new CurrencyBuilder(new BigDecimal(value));
    }

    // -------------------------------------------------------------------------
    // Cardinal factory methods
    // -------------------------------------------------------------------------

    /**
     * Starts a cardinal number conversion for the given value.
     *
     * <p>Default: language {@code "pt-BR"}.</p>
     *
     * @param value the number to convert
     * @return a builder to configure and execute the conversion
     */
    public static CardinalBuilder cardinal(long value) {
        return new CardinalBuilder(BigDecimal.valueOf(value));
    }

    /**
     * Starts a cardinal number conversion for the given value.
     *
     * <p>Use this overload to handle numbers larger than {@link Long#MAX_VALUE}
     * (e.g., quintilhões, setilhões).
     * Only the integer part of the value is converted; any fractional part is ignored.</p>
     *
     * <p>Default: language {@code "pt-BR"}.</p>
     *
     * @param value the number to convert; must not be null
     * @return a builder to configure and execute the conversion
     */
    public static CardinalBuilder cardinal(BigDecimal value) {
        Objects.requireNonNull(value, "value cannot be null");
        return new CardinalBuilder(value);
    }

    /**
     * Starts a cardinal number conversion for the given value.
     *
     * <p>Use this overload to handle numbers larger than {@link Long#MAX_VALUE}
     * (e.g., quintilhões, setilhões).</p>
     *
     * <p>Default: language {@code "pt-BR"}.</p>
     *
     * @param value the number to convert; must not be null
     * @return a builder to configure and execute the conversion
     */
    public static CardinalBuilder cardinal(BigInteger value) {
        Objects.requireNonNull(value, "value cannot be null");
        return new CardinalBuilder(new BigDecimal(value));
    }

    // -------------------------------------------------------------------------
    // Fluent builders
    // -------------------------------------------------------------------------

    /**
     * Fluent builder for currency conversions.
     */
    public static final class CurrencyBuilder {

        private final BigDecimal value;
        private String languageCode = "pt-BR";
        private String currencyCode = "BRL";

        private CurrencyBuilder(BigDecimal value) {
            this.value = value;
        }

        /**
         * Sets the target language by code.
         *
         * @param code a BCP 47 language tag (e.g., {@code "pt-BR"})
         * @return this builder
         */
        public CurrencyBuilder language(String code) {
            this.languageCode = Objects.requireNonNull(code, "language code cannot be null");
            return this;
        }

        /**
         * Sets the target language.
         *
         * @param language the target language; must not be null
         * @return this builder
         * @see Language#PT_BR
         */
        public CurrencyBuilder language(Language language) {
            this.languageCode = Objects.requireNonNull(language, "language cannot be null").code();
            return this;
        }

        /**
         * Sets the target currency by ISO 4217 code.
         *
         * @param code an ISO 4217 currency code (e.g., {@code "BRL"}, {@code "USD"}, {@code "EUR"})
         * @return this builder
         * @see Currencies
         */
        public CurrencyBuilder currency(String code) {
            this.currencyCode = Objects.requireNonNull(code, "currency code cannot be null");
            return this;
        }

        /**
         * Performs the conversion and returns the result.
         *
         * @return the monetary value expressed in words
         */
        public String toWords() {
            Language language = new Language(languageCode);
            Currency currency = CURRENCY_PROVIDER.getCurrency(currencyCode);
            ConversionContext context = new ConversionContext(language, currency);
            return CURRENCY_CONVERTER.convert(value, context);
        }
    }

    /**
     * Fluent builder for cardinal number conversions.
     */
    public static final class CardinalBuilder {

        private final BigDecimal value;
        private String languageCode = "pt-BR";

        private CardinalBuilder(BigDecimal value) {
            this.value = value;
        }

        /**
         * Sets the target language by code.
         *
         * @param code a BCP 47 language tag (e.g., {@code "pt-BR"})
         * @return this builder
         */
        public CardinalBuilder language(String code) {
            this.languageCode = Objects.requireNonNull(code, "language code cannot be null");
            return this;
        }

        /**
         * Sets the target language.
         *
         * @param language the target language; must not be null
         * @return this builder
         * @see Language#PT_BR
         */
        public CardinalBuilder language(Language language) {
            this.languageCode = Objects.requireNonNull(language, "language cannot be null").code();
            return this;
        }

        /**
         * Performs the conversion and returns the result.
         *
         * @return the cardinal number expressed in words
         */
        public String toWords() {
            Language language = new Language(languageCode);
            ConversionContext context = new ConversionContext(language, null);
            return CARDINAL_CONVERTER.convert(value, context);
        }
    }
}
