package io.github.marcosaalbanojunior.numverb.exception;

/**
 * Thrown when a number falls outside the supported conversion range.
 */
public class NumberOutOfRangeException extends RuntimeException {

    /**
     * @param message description of the range violation
     */
    public NumberOutOfRangeException(String message) {
        super(message);
    }
}
