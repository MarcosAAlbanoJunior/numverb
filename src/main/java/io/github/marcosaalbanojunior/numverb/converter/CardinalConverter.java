package io.github.marcosaalbanojunior.numverb.converter;

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
        LanguageRules rules = new LanguageRules(context.language().code(), languageProvider);
        return rules.convertToWords(intPart, "masculine");
    }
}
