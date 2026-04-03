package io.github.marcosaalbanojunior.numverb.exception;

/**
 * Thrown when a requested currency is not supported by the library.
 */
public class UnsupportedCurrencyException extends RuntimeException {

    /**
     * @param code the unsupported currency code
     */
    public UnsupportedCurrencyException(String code) {
        super("Unsupported currency: " + code);
    }

    /**
     * @param code  the unsupported currency code
     * @param cause the underlying cause
     */
    public UnsupportedCurrencyException(String code, Throwable cause) {
        super("Unsupported currency: " + code, cause);
    }
}
