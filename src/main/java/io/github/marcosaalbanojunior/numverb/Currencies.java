package io.github.marcosaalbanojunior.numverb;

/**
 * Constants for the currency codes supported out-of-the-box by numverb.
 *
 * <p>Use these constants with {@link NumVerb.CurrencyBuilder#currency(String)} to avoid
 * typos and improve discoverability:</p>
 *
 * <pre>{@code
 * NumVerb.currency("1234.68")
 *     .language(Language.PT_BR)
 *     .currency(Currencies.USD)
 *     .toWords();
 * }</pre>
 *
 * <p>The values are plain ISO 4217 strings, so they are fully compatible with the
 * {@code String}-based builder method.</p>
 */
public final class Currencies {

    /** Brazilian Real. */
    public static final String BRL = "BRL";

    /** US Dollar. */
    public static final String USD = "USD";

    /** Euro. */
    public static final String EUR = "EUR";

    /** British Pound Sterling. */
    public static final String GBP = "GBP";

    private Currencies() {}
}
