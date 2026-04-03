package io.github.marcosaalbanojunior.numverb;

import io.github.marcosaalbanojunior.numverb.converter.CardinalConverter;
import io.github.marcosaalbanojunior.numverb.converter.CurrencyConverter;
import io.github.marcosaalbanojunior.numverb.currency.Currency;
import io.github.marcosaalbanojunior.numverb.currency.CurrencyProvider;
import io.github.marcosaalbanojunior.numverb.lang.Language;
import io.github.marcosaalbanojunior.numverb.lang.LanguageProvider;
import io.github.marcosaalbanojunior.numverb.model.ConversionContext;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Main entry point for the <strong>numverb</strong> library.
 *
 * <p>Provides a fluent API for converting numbers to their word representation.</p>
 *
 * <pre>{@code
 * // Basic usage — defaults to pt-BR + BRL
 * String result = NumVerb.currency(new BigDecimal("1234.68")).toWords();
 * // → "mil duzentos e trinta e quatro reais e sessenta e oito centavos"
 *
 * // With explicit language and currency
 * String result = NumVerb.currency(new BigDecimal("1234.68"))
 *     .language("pt-BR")
 *     .currency("USD")
 *     .toWords();
 *
 * // Cardinal number
 * String result = NumVerb.cardinal(1234).language("pt-BR").toWords();
 * // → "mil duzentos e trinta e quatro"
 * }</pre>
 *
 * <p>All public methods are thread-safe.</p>
 */
public final class NumVerb {

    private static final LanguageProvider LANGUAGE_PROVIDER = new LanguageProvider();
    private static final CurrencyProvider CURRENCY_PROVIDER = new CurrencyProvider();
    private static final CurrencyConverter CURRENCY_CONVERTER = new CurrencyConverter(LANGUAGE_PROVIDER);
    private static final CardinalConverter CARDINAL_CONVERTER = new CardinalConverter(LANGUAGE_PROVIDER);

    private NumVerb() {}

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
         * Sets the target language.
         *
         * @param code a BCP 47 language tag (e.g., {@code "pt-BR"})
         * @return this builder
         */
        public CurrencyBuilder language(String code) {
            this.languageCode = Objects.requireNonNull(code, "language code cannot be null");
            return this;
        }

        /**
         * Sets the target currency.
         *
         * @param code an ISO 4217 currency code (e.g., {@code "BRL"}, {@code "USD"}, {@code "EUR"})
         * @return this builder
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
         * Sets the target language.
         *
         * @param code a BCP 47 language tag (e.g., {@code "pt-BR"})
         * @return this builder
         */
        public CardinalBuilder language(String code) {
            this.languageCode = Objects.requireNonNull(code, "language code cannot be null");
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
