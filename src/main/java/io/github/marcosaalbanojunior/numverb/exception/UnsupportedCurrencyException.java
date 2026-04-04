package io.github.marcosaalbanojunior.numverb.exception;

/**
 * Thrown when a requested currency is not supported by the library,
 * either because it is unknown or because it has not been configured
 * for the requested language.
 */
public class UnsupportedCurrencyException extends RuntimeException {

    /**
     * Use when the currency is unknown regardless of language.
     *
     * @param code the unsupported currency code
     */
    public UnsupportedCurrencyException(String code) {
        super("Unsupported currency: " + code);
    }

    /**
     * Use when the currency exists but is not configured for the given language.
     *
     * @param currencyCode the currency code that was requested
     * @param languageCode the language for which it is not available
     */
    public UnsupportedCurrencyException(String currencyCode, String languageCode) {
        super("Currency '" + currencyCode + "' is not supported for language '" + languageCode + "'");
    }

    /**
     * Use when loading fails due to an I/O error.
     *
     * @param code  the unsupported currency code
     * @param cause the underlying cause
     */
    public UnsupportedCurrencyException(String code, Throwable cause) {
        super("Unsupported currency: " + code, cause);
    }
}
