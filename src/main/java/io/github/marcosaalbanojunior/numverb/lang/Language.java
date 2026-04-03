package io.github.marcosaalbanojunior.numverb.lang;

import java.util.Objects;

/**
 * Represents a supported language/locale for number-to-words conversion.
 *
 * <p>Language codes follow the BCP 47 standard (e.g., {@code "pt-BR"}, {@code "en-US"}).</p>
 */
public record Language(String code) {

    /** Portuguese (Brazil). */
    public static final Language PT_BR = new Language("pt-BR");

    /**
     * @param code BCP 47 language tag; must not be null or blank
     */
    public Language {
        Objects.requireNonNull(code, "Language code cannot be null");
        if (code.isBlank()) {
            throw new IllegalArgumentException("Language code cannot be blank");
        }
    }
}
