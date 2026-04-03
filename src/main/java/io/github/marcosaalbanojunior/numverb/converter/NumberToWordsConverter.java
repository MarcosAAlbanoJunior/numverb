package io.github.marcosaalbanojunior.numverb.converter;

import io.github.marcosaalbanojunior.numverb.model.ConversionContext;

import java.math.BigDecimal;

/**
 * Contract for converting a numeric value to its word representation.
 */
public interface NumberToWordsConverter {

    /**
     * Converts the given value to words according to the provided context.
     *
     * @param value   the value to convert; must not be null
     * @param context the language and currency context; must not be null
     * @return the word representation
     */
    String convert(BigDecimal value, ConversionContext context);
}
