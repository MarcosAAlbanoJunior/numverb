package io.github.marcosaalbanojunior.numverb.exception;

/**
 * Thrown when a requested language is not supported by the library.
 */
public class UnsupportedLanguageException extends RuntimeException {

    /**
     * @param code the unsupported language code
     */
    public UnsupportedLanguageException(String code) {
        super("Unsupported language: " + code);
    }

    /**
     * @param code  the unsupported language code
     * @param cause the underlying cause
     */
    public UnsupportedLanguageException(String code, Throwable cause) {
        super("Unsupported language: " + code, cause);
    }
}
