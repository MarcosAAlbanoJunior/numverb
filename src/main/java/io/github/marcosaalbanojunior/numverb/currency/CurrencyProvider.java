package io.github.marcosaalbanojunior.numverb.currency;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedCurrencyException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and caches language-specific currency definitions from classpath property files.
 *
 * <p>Currency files are located at
 * {@code /currencies/{languageCode}/{currencyCode}.properties} on the classpath
 * and are encoded in UTF-8. Currency names are language-specific — "dólar" for pt-BR
 * and "dollar" for en-US live in separate files under their respective language
 * subdirectory.</p>
 *
 * <p>This class is thread-safe.</p>
 */
public class CurrencyProvider {

    private static final String RESOURCE_PATH = "/currencies/%s/%s.properties";

    private static final List<String> REQUIRED_KEYS = List.of(
            "currency.code",
            "currency.singular",
            "currency.plural",
            "currency.subunit.singular",
            "currency.subunit.plural",
            "currency.gender",
            "currency.subunit.gender"
    );

    private final Map<String, Currency> cache = new ConcurrentHashMap<>();

    /**
     * Returns the {@link Currency} for the given language and ISO 4217 currency code.
     *
     * @param languageCode BCP 47 language tag (e.g. {@code "pt-BR"}, {@code "en-US"})
     * @param currencyCode ISO 4217 code (e.g. {@code "BRL"}, {@code "USD"})
     * @return the loaded {@link Currency}
     * @throws UnsupportedCurrencyException if no property file exists for the combination
     * @throws IllegalStateException if the property file is missing a required key
     */
    public Currency getCurrency(String languageCode, String currencyCode) {
        String key = languageCode + ":" + currencyCode;
        return cache.computeIfAbsent(key, k -> load(languageCode, currencyCode));
    }

    private Currency load(String languageCode, String currencyCode) {
        String path = String.format(RESOURCE_PATH, languageCode, currencyCode);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new UnsupportedCurrencyException(currencyCode, languageCode);
            }
            Properties props = new Properties();
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));

            for (String key : REQUIRED_KEYS) {
                if (!props.containsKey(key)) {
                    throw new IllegalStateException(
                            "Currency file " + languageCode + "/" + currencyCode
                            + ".properties is missing required property: '" + key + "'");
                }
            }

            return new Currency(
                    props.getProperty("currency.code"),
                    props.getProperty("currency.singular"),
                    props.getProperty("currency.plural"),
                    props.getProperty("currency.subunit.singular"),
                    props.getProperty("currency.subunit.plural"),
                    props.getProperty("currency.gender"),
                    props.getProperty("currency.subunit.gender")
            );
        } catch (IOException e) {
            throw new UnsupportedCurrencyException(currencyCode, e);
        }
    }
}
