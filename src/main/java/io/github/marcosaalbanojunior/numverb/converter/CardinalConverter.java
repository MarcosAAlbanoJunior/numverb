package io.github.marcosaalbanojunior.numverb.converter;

import io.github.marcosaalbanojunior.numverb.exception.NumberOutOfRangeException;
import io.github.marcosaalbanojunior.numverb.lang.LanguageProvider;
import io.github.marcosaalbanojunior.numverb.lang.LanguageRules;
import io.github.marcosaalbanojunior.numverb.model.ConversionContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Converts cardinal numbers to their word representation.
 *
 * <p>Only the integer part of the value is converted; fractional parts are ignored.</p>
 *
 * <p>This class is thread-safe.</p>
 */
public class CardinalConverter implements NumberToWordsConverter {

    /**
     * Maximum supported value: 999 setilhões + all lower scales filled with 999.
     * = 999_999_999_999_999_999_999_999_999 (27 nines, 27 digits)
     * Above this the count at the setilhão scale slot would exceed 999.
     */
    private static final BigInteger MAX_CARDINAL = new BigInteger("999999999999999999999999999");

    private final LanguageProvider languageProvider;

    /**
     * @param languageProvider the provider for language vocabulary; must not be null
     */
    public CardinalConverter(LanguageProvider languageProvider) {
        this.languageProvider = Objects.requireNonNull(languageProvider, "languageProvider cannot be null");
    }

    /**
     * Converts the integer part of {@code value} to words.
     *
     * @param value   the value to convert; must not be null
     * @param context the language context; must not be null
     * @return the cardinal number in words
     */
    @Override
    public String convert(BigDecimal value, ConversionContext context) {
        Objects.requireNonNull(value, "value cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        BigInteger intPart = value.toBigInteger();
        if (intPart.signum() < 0) {
            throw new NumberOutOfRangeException("Negative values are not supported: " + value);
        }
        if (intPart.compareTo(MAX_CARDINAL) > 0) {
            throw new NumberOutOfRangeException(
                    "Value exceeds the maximum supported range of " + MAX_CARDINAL + ": " + intPart);
        }
        LanguageRules rules = new LanguageRules(context.language().code(), languageProvider);
        return rules.convertToWords(intPart, "masculine");
    }
}
