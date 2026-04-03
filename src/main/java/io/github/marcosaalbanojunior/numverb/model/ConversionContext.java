package io.github.marcosaalbanojunior.numverb.model;

import io.github.marcosaalbanojunior.numverb.currency.Currency;
import io.github.marcosaalbanojunior.numverb.lang.Language;

import java.util.Objects;

/**
 * Immutable context for a number-to-words conversion, holding the target language
 * and optional currency configuration.
 *
 * <p>For cardinal conversions, {@code currency} may be {@code null}.</p>
 */
public record ConversionContext(Language language, Currency currency) {

    /**
     * @param language the target language; must not be null
     * @param currency the target currency; may be null for cardinal conversions
     */
    public ConversionContext {
        Objects.requireNonNull(language, "language cannot be null");
    }
}
