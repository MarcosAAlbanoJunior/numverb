package io.github.marcosaalbanojunior.numverb.lang;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedLanguageException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Orchestrates number-to-words conversion for a given language.
 *
 * <p>All language-specific behaviour (connectors, separators, scale words, gender forms)
 * is driven by the language's {@code .properties} file via {@link GrammarConfig} and
 * {@link NumberSpeller}. This class contains <em>no</em> language-specific {@code if}
 * blocks; adding a new language requires only a properties file.</p>
 *
 * <p>Currently supported languages: {@code pt-BR}, {@code en-US}.</p>
 *
 * <p>Thread-safe once constructed.</p>
 */
public class LanguageRules {

    private static final Set<String> SUPPORTED = Set.of("pt-BR", "en-US");

    private static final BigInteger MILLION = BigInteger.valueOf(1_000_000L);

    private static final BigInteger[] SCALE_VALUES = {
        new BigInteger("1000000000000000000000000"), // setilhão  / septillion  (10^24)
        new BigInteger("1000000000000000000000"),    // sextilhão / sextillion  (10^21)
        new BigInteger("1000000000000000000"),       // quintilhão/ quintillion (10^18)
        new BigInteger("1000000000000000"),          // quatrilhão/ quadrillion (10^15)
        new BigInteger("1000000000000"),             // trilhão   / trillion    (10^12)
        new BigInteger("1000000000"),                // bilhão    / billion     (10^9)
        new BigInteger("1000000"),                   // milhão    / million     (10^6)
        new BigInteger("1000"),                      // mil       / thousand    (10^3)
    };

    /** Internal property keys for scale words (language-agnostic). */
    private static final String[] SCALE_KEYS = {
        "setilhao", "sextilhao", "quintilhao", "quatrilhao",
        "trilhao", "bilhao", "milhao", "mil"
    };

    private final GrammarConfig cfg;
    private final NumberSpeller speller;

    /**
     * Validates that {@code languageCode} is supported, throwing before any other
     * resource is loaded. Useful to enforce language-first error ordering.
     *
     * @throws UnsupportedLanguageException if the language is not supported
     */
    public static void requireSupported(String languageCode) {
        if (!SUPPORTED.contains(languageCode)) {
            throw new UnsupportedLanguageException(languageCode);
        }
    }

    /**
     * @param languageCode BCP 47 language tag (e.g. {@code "pt-BR"}, {@code "en-US"})
     * @param provider     provider used to load vocabulary
     * @throws UnsupportedLanguageException if the language is not supported
     */
    public LanguageRules(String languageCode, LanguageProvider provider) {
        requireSupported(languageCode);
        this.cfg     = new GrammarConfig(provider.getProperties(languageCode));
        this.speller = new NumberSpeller(cfg);
    }

    /**
     * Converts a non-negative integer to its word representation.
     *
     * <p>The {@code gender} parameter applies only to the final remainder (&lt; 1000).
     * Scale word counts (thousands, millions, etc.) always use the gender defined
     * by {@code scale.gender} in the properties file.</p>
     *
     * @param number the number to convert; must be &ge; 0
     * @param gender grammatical gender ({@code "masculine"} or {@code "feminine"})
     * @return the number expressed as words
     * @throws IllegalArgumentException if {@code number} is negative
     */
    public String convertToWords(BigInteger number, String gender) {
        if (number.signum() < 0) {
            throw new IllegalArgumentException("number must be >= 0: " + number);
        }
        if (number.equals(BigInteger.ZERO)) {
            return cfg.vocab("unit.0");
        }

        List<ScalePart> parts = new ArrayList<>();
        BigInteger remaining = number;

        for (int i = 0; i < SCALE_VALUES.length; i++) {
            BigInteger scaleValue = SCALE_VALUES[i];
            if (remaining.compareTo(scaleValue) >= 0) {
                BigInteger count = remaining.divide(scaleValue);
                remaining = remaining.remainder(scaleValue);
                parts.add(new ScalePart(count, buildScaleWords(count, SCALE_KEYS[i])));
            }
        }

        if (remaining.signum() > 0) {
            parts.add(new ScalePart(remaining,
                    speller.spellNonZero(remaining.longValueExact(), gender)));
        }

        return joinParts(parts);
    }

    /** Returns the connector word (e.g. {@code "e"}, {@code "and"}). */
    public String getConnector() {
        return cfg.connector;
    }

    /**
     * Returns the separator to place between the number words and the currency word.
     *
     * <p>Languages that define {@code currency.preposition} (e.g. pt-BR with {@code "de"})
     * use it for round-million values: "um milhão <b>de</b> reais".
     * All other cases — and languages without the property — return a plain space.</p>
     */
    public String getCurrencyJoiner(BigInteger intPart) {
        if (cfg.currencyPreposition == null) return " ";
        if (intPart.compareTo(MILLION) >= 0
                && intPart.remainder(MILLION).equals(BigInteger.ZERO)) {
            return " " + cfg.currencyPreposition + " ";
        }
        return " ";
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String buildScaleWords(BigInteger count, String scaleKey) {
        if ("mil".equals(scaleKey)) {
            String countWords = (count.equals(BigInteger.ONE) && cfg.skipOneBeforeThousand)
                    ? ""
                    : speller.spellNonZero(count.longValueExact(), cfg.scaleGender);
            String scaleWord = cfg.vocab("scale.mil");
            return countWords.isEmpty() ? scaleWord : countWords + " " + scaleWord;
        }
        String countWords = speller.spellNonZero(count.longValueExact(), cfg.scaleGender);
        boolean plural    = count.compareTo(BigInteger.ONE) > 0;
        String  scaleWord = plural
                ? cfg.vocab("scale." + scaleKey + ".plural")
                : cfg.vocab("scale." + scaleKey + ".singular");
        return countWords + " " + scaleWord;
    }

    /**
     * Joins scale parts using language connector rules:
     * <ul>
     *   <li>Use connector (e.g. "e", "and") before a part whose count is &lt; 100.</li>
     *   <li>Use a plain space before a part whose count is &ge; 100.</li>
     * </ul>
     */
    private String joinParts(List<ScalePart> parts) {
        if (parts.size() == 1) return parts.get(0).words();

        StringBuilder sb = new StringBuilder(parts.get(0).words());
        for (int i = 1; i < parts.size(); i++) {
            ScalePart part = parts.get(i);
            if (part.count().compareTo(BigInteger.valueOf(100)) < 0) {
                sb.append(" ").append(cfg.connector).append(" ");
            } else {
                sb.append(" ");
            }
            sb.append(part.words());
        }
        return sb.toString();
    }

    private record ScalePart(BigInteger count, String words) {}
}
