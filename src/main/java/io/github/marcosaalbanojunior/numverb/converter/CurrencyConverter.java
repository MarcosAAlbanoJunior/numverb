package io.github.marcosaalbanojunior.numverb.converter;

import io.github.marcosaalbanojunior.numverb.currency.Currency;
import io.github.marcosaalbanojunior.numverb.exception.NumberOutOfRangeException;
import io.github.marcosaalbanojunior.numverb.lang.LanguageProvider;
import io.github.marcosaalbanojunior.numverb.lang.LanguageRules;
import io.github.marcosaalbanojunior.numverb.model.ConversionContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Converts monetary values ({@link BigDecimal}) to their word representation.
 *
 * <p>Supports values from {@code 0.00} up to {@code 999,999,999,999,999,999,999,999.99}
 * (up to setilhões). Negative values are not supported.</p>
 *
 * <p>This class is thread-safe.</p>
 */
public class CurrencyConverter implements NumberToWordsConverter {

    /** Maximum supported value (setilhões range). */
    private static final BigDecimal MAX_VALUE =
            new BigDecimal("999999999999999999999999.99");

    private final LanguageProvider languageProvider;

    /**
     * @param languageProvider the provider for language vocabulary; must not be null
     */
    public CurrencyConverter(LanguageProvider languageProvider) {
        this.languageProvider = Objects.requireNonNull(languageProvider, "languageProvider cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * @throws NumberOutOfRangeException if value is negative or exceeds the maximum range
     */
    @Override
    public String convert(BigDecimal value, ConversionContext context) {
        Objects.requireNonNull(value, "value cannot be null");
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(context.currency(), "CurrencyConverter requires a non-null currency in the context");

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NumberOutOfRangeException("Negative values are not supported: " + value);
        }
        if (value.compareTo(MAX_VALUE) > 0) {
            throw new NumberOutOfRangeException(
                    "Value exceeds the maximum supported range of " + MAX_VALUE + ": " + value);
        }

        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
        BigInteger intPart = scaled.toBigInteger();
        int centPart = scaled.remainder(BigDecimal.ONE)
                             .multiply(BigDecimal.valueOf(100))
                             .setScale(0, RoundingMode.HALF_UP)
                             .intValueExact();

        Currency currency = context.currency();
        LanguageRules rules = new LanguageRules(context.language().code(), languageProvider);

        if (intPart.equals(BigInteger.ZERO) && centPart == 0) {
            return "zero " + currency.plural();
        }

        if (intPart.equals(BigInteger.ZERO)) {
            return buildCentPart(centPart, currency, rules);
        }

        if (centPart == 0) {
            return buildIntegerPart(intPart, currency, rules);
        }

        return buildIntegerPart(intPart, currency, rules)
                + " " + rules.getConnector() + " "
                + buildCentPart(centPart, currency, rules);
    }

    private String buildIntegerPart(BigInteger intPart, Currency currency, LanguageRules rules) {
        String numberWords = rules.convertToWords(intPart, currency.gender());
        String currencyWord = intPart.equals(BigInteger.ONE) ? currency.singular() : currency.plural();
        return numberWords + rules.getCurrencyJoiner(intPart) + currencyWord;
    }

    private String buildCentPart(int centPart, Currency currency, LanguageRules rules) {
        String numberWords = rules.convertToWords(BigInteger.valueOf(centPart), currency.subunitGender());
        String currencyWord = centPart == 1 ? currency.subunitSingular() : currency.subunitPlural();
        return numberWords + " " + currencyWord;
    }
}
