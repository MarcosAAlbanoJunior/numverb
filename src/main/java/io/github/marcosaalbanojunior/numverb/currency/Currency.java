package io.github.marcosaalbanojunior.numverb.currency;

import java.util.Objects;

/**
 * Immutable representation of a currency, including singular/plural forms for
 * both the main unit and the subunit.
 */
public record Currency(
        String code,
        String singular,
        String plural,
        String subunitSingular,
        String subunitPlural,
        String gender
) {

    /**
     * Compact constructor that validates all fields are non-null.
     */
    public Currency {
        Objects.requireNonNull(code, "code cannot be null");
        Objects.requireNonNull(singular, "singular cannot be null");
        Objects.requireNonNull(plural, "plural cannot be null");
        Objects.requireNonNull(subunitSingular, "subunitSingular cannot be null");
        Objects.requireNonNull(subunitPlural, "subunitPlural cannot be null");
        Objects.requireNonNull(gender, "gender cannot be null");
    }
}
