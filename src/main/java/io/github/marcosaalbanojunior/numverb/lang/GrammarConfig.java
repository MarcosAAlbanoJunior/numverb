package io.github.marcosaalbanojunior.numverb.lang;

import java.util.Properties;

/**
 * Immutable grammar configuration derived from a language {@code .properties} file.
 *
 * <p>Centralises all grammar decisions for a language so that
 * {@link LanguageRules} and {@link NumberSpeller} contain zero language-specific
 * {@code if} blocks. Adding a new language requires only a properties file.</p>
 *
 * <p>Recognised grammar properties:</p>
 * <ul>
 *   <li>{@code connector} — word joining adjacent number components
 *       (e.g. {@code "e"} for pt-BR, {@code "and"} for en-US)</li>
 *   <li>{@code tens.separator} — separator between a tens word and a units word,
 *       including surrounding whitespace if desired
 *       (absent → {@code " {connector} "}; en-US uses {@code "-"})</li>
 *   <li>{@code scale.mil.skip-one} — {@code true} when the count "1" should be
 *       omitted before the thousand-scale word ("mil", not "um mil"; pt-BR only)</li>
 *   <li>{@code scale.gender} — grammatical gender used when counting scale words</li>
 *   <li>{@code currency.preposition} — preposition before currency for round-million
 *       amounts (e.g. {@code "de"} in pt-BR; absent = plain space)</li>
 * </ul>
 */
final class GrammarConfig {

    private final Properties vocabulary;

    /** Word connecting two adjacent components ("e", "and"). */
    final String connector;

    /**
     * Full separator placed between a tens word and a units word, including any
     * surrounding whitespace. Examples: {@code " e "} (pt-BR), {@code "-"} (en-US).
     */
    final String tensSeparator;

    /**
     * Grammatical gender used when counting scale words (mil, milhões, etc.).
     * All scale words in pt-BR and en-US are masculine.
     */
    final String scaleGender;

    /**
     * Whether the count "1" should be omitted before the thousand-scale word.
     * {@code true} for pt-BR ("mil", not "um mil"), {@code false} for en-US
     * ("one thousand").
     */
    final boolean skipOneBeforeThousand;

    /**
     * Preposition placed between a round-million amount and the currency word.
     * {@code null} when the language does not use one (e.g. en-US).
     */
    final String currencyPreposition;

    GrammarConfig(Properties props) {
        this.vocabulary         = props;
        this.connector          = require(props, "connector");
        this.scaleGender        = require(props, "scale.gender");
        this.currencyPreposition = props.getProperty("currency.preposition");
        this.skipOneBeforeThousand = "true".equalsIgnoreCase(
                props.getProperty("scale.mil.skip-one", "false"));

        String sep = props.getProperty("tens.separator");
        this.tensSeparator = (sep != null) ? sep : " " + connector + " ";
    }

    /** Returns the value of a vocabulary property, throwing if absent. */
    String vocab(String key) {
        String value = vocabulary.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing language property: '" + key + "'");
        }
        return value;
    }

    /** Returns {@code true} if the vocabulary contains the given key. */
    boolean hasVocab(String key) {
        return vocabulary.containsKey(key);
    }

    private static String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing required language property: '" + key + "'");
        }
        return value;
    }
}
