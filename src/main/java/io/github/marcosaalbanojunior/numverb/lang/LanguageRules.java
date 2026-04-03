package io.github.marcosaalbanojunior.numverb.lang;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedLanguageException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Provides language-specific grammar rules and vocabulary for number-to-words conversion.
 *
 * <p>Vocabulary is loaded from {@code .properties} files via {@link LanguageProvider};
 * grammar logic is encoded per supported language.</p>
 *
 * <p>Currently supported languages: {@code pt-BR}.</p>
 *
 * <p>This class is thread-safe once constructed.</p>
 */
public class LanguageRules {

    private static final Set<String> SUPPORTED = Set.of("pt-BR");

    private static final BigInteger MILLION = BigInteger.valueOf(1_000_000L);

    private static final BigInteger[] SCALE_VALUES = {
        new BigInteger("1000000000000000000000000"), // setilhão  (10^24)
        new BigInteger("1000000000000000000000"),    // sextilhão (10^21)
        new BigInteger("1000000000000000000"),       // quintilhão (10^18)
        new BigInteger("1000000000000000"),          // quatrilhão (10^15)
        new BigInteger("1000000000000"),             // trilhão   (10^12)
        new BigInteger("1000000000"),                // bilhão    (10^9)
        new BigInteger("1000000"),                   // milhão    (10^6)
        new BigInteger("1000"),                      // mil       (10^3)
    };

    private static final String[] SCALE_KEYS = {
        "setilhao", "sextilhao", "quintilhao", "quatrilhao",
        "trilhao", "bilhao", "milhao", "mil"
    };

    private final String languageCode;
    private final Properties props;

    /**
     * Creates a new {@code LanguageRules} for the given language code.
     *
     * @param languageCode the BCP 47 language tag (e.g., {@code "pt-BR"})
     * @param provider     the provider used to load vocabulary
     * @throws UnsupportedLanguageException if the language grammar is not implemented
     */
    public LanguageRules(String languageCode, LanguageProvider provider) {
        if (!SUPPORTED.contains(languageCode)) {
            throw new UnsupportedLanguageException(languageCode);
        }
        this.languageCode = languageCode;
        this.props = provider.getProperties(languageCode);
    }

    /**
     * Converts a non-negative integer to its word representation.
     *
     * <p>The {@code gender} parameter applies only to the final remainder (&lt; 1000).
     * Scale word counts (thousands, millions, etc.) always use the gender defined by
     * {@code scale.gender} in the language properties, since scale words have their
     * own grammatical gender independent of the noun being counted.</p>
     *
     * @param number the number to convert; must be &ge; 0
     * @param gender grammatical gender of the primary unit ({@code "masculine"} or {@code "feminine"})
     * @return the number expressed as words
     */
    public String convertToWords(BigInteger number, String gender) {
        if (number.equals(BigInteger.ZERO)) {
            return prop("unit.0");
        }

        // Gender used when counting scale words (e.g. "dois" in "dois milhões").
        // Scale words have their own grammatical gender, independent of the currency gender.
        String scaleGender = prop("scale.gender");

        List<ScalePart> parts = new ArrayList<>();
        BigInteger remaining = number;

        for (int i = 0; i < SCALE_VALUES.length; i++) {
            BigInteger scaleValue = SCALE_VALUES[i];
            String scaleKey = SCALE_KEYS[i];

            if (remaining.compareTo(scaleValue) >= 0) {
                BigInteger count = remaining.divide(scaleValue);
                remaining = remaining.remainder(scaleValue);

                String words;
                if ("mil".equals(scaleKey)) {
                    // "mil" is not prefixed with "um" when count == 1
                    String countWords = count.equals(BigInteger.ONE)
                            ? ""
                            : convertUpTo999(count.longValueExact(), scaleGender);
                    String scaleWord = prop("scale.mil");
                    words = countWords.isEmpty() ? scaleWord : countWords + " " + scaleWord;
                } else {
                    String countWords = convertUpTo999(count.longValueExact(), scaleGender);
                    boolean plural = count.compareTo(BigInteger.ONE) > 0;
                    String scaleWord = plural
                            ? prop("scale." + scaleKey + ".plural")
                            : prop("scale." + scaleKey + ".singular");
                    words = countWords + " " + scaleWord;
                }

                parts.add(new ScalePart(count, words));
            }
        }

        // The remaining portion (< 1000) uses the caller-supplied gender,
        // since this is where the currency/noun gender actually applies.
        if (remaining.compareTo(BigInteger.ZERO) > 0) {
            String words = convertUpTo999(remaining.longValueExact(), gender);
            parts.add(new ScalePart(remaining, words));
        }

        return joinParts(parts);
    }

    /**
     * Returns the word used to connect number components (e.g., {@code "e"} in pt-BR).
     *
     * @return the connector word
     */
    public String getConnector() {
        return prop("connector");
    }

    /**
     * Returns the separator string to place between the number words and the currency word.
     *
     * <p>In pt-BR, round-million values use a preposition:
     * {@code "um milhão <b>de</b> reais"}.
     * All other values use a plain space.</p>
     *
     * <p>The preposition is read from the {@code currency.preposition} property.
     * Languages without this property always return a plain space.</p>
     *
     * @param intPart the integer (non-cents) portion of the monetary amount
     * @return {@code " de "} for round-million values in pt-BR; {@code " "} otherwise
     */
    public String getCurrencyJoiner(BigInteger intPart) {
        if (!props.containsKey("currency.preposition")) {
            return " ";
        }
        if (intPart.compareTo(MILLION) >= 0 && intPart.remainder(MILLION).equals(BigInteger.ZERO)) {
            return " " + prop("currency.preposition") + " ";
        }
        return " ";
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Converts a number in the range 1–999 to words. Returns empty string for 0.
     */
    private String convertUpTo999(long n, String gender) {
        if (n == 0) return "";
        if (n < 20) return getUnitWord((int) n, gender);

        if (n < 100) {
            long tens = (n / 10) * 10;
            long units = n % 10;
            String tensWord = prop("ten." + tens);
            if (units == 0) return tensWord;
            return tensWord + " " + prop("connector") + " " + getUnitWord((int) units, gender);
        }

        // 100–999
        long hundreds = (n / 100) * 100;
        long remainder = n % 100;

        String hundredsWord;
        if (n == 100) {
            hundredsWord = prop("hundred.100");
        } else if (hundreds == 100) {
            hundredsWord = prop("hundred.100.combined");
        } else {
            String femKey = "hundred." + hundreds + ".feminine";
            hundredsWord = ("feminine".equalsIgnoreCase(gender) && props.containsKey(femKey))
                    ? prop(femKey)
                    : prop("hundred." + hundreds);
        }

        if (remainder == 0) return hundredsWord;
        return hundredsWord + " " + prop("connector") + " " + convertUpTo999(remainder, gender);
    }

    private String getUnitWord(int n, String gender) {
        String femKey = "unit." + n + ".feminine";
        if ("feminine".equalsIgnoreCase(gender) && props.containsKey(femKey)) {
            return prop(femKey);
        }
        return prop("unit." + n);
    }

    /**
     * Joins scale parts with appropriate connectors.
     *
     * <p>Rules (pt-BR):
     * <ul>
     *   <li>Use {@code " e "} before a part whose count is &lt; 100</li>
     *   <li>Use {@code " "} (space) before a part whose count is &ge; 100</li>
     * </ul>
     */
    private String joinParts(List<ScalePart> parts) {
        if (parts.isEmpty()) return "";
        if (parts.size() == 1) return parts.get(0).words();

        StringBuilder sb = new StringBuilder(parts.get(0).words());
        for (int i = 1; i < parts.size(); i++) {
            ScalePart part = parts.get(i);
            if (part.count().compareTo(BigInteger.valueOf(100)) < 0) {
                sb.append(" ").append(prop("connector")).append(" ");
            } else {
                sb.append(" ");
            }
            sb.append(part.words());
        }
        return sb.toString();
    }

    private String prop(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalStateException(
                    "Missing property '" + key + "' for language: " + languageCode);
        }
        return value;
    }

    private record ScalePart(BigInteger count, String words) {}
}