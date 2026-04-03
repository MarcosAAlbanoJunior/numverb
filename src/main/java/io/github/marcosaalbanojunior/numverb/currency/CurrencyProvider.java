package io.github.marcosaalbanojunior.numverb.currency;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedCurrencyException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and caches currency definitions from classpath property files.
 *
 * <p>Currency files are located at {@code /currencies/{code}.properties} on the classpath
 * and are encoded in UTF-8.</p>
 *
 * <p>This class is thread-safe.</p>
 */
public class CurrencyProvider {

    private static final String RESOURCE_PATH = "/currencies/%s.properties";
    private final Map<String, Currency> cache = new ConcurrentHashMap<>();

    /**
     * Returns the {@link Currency} for the given ISO 4217 currency code.
     *
     * @param currencyCode ISO 4217 code (e.g., {@code "BRL"})
     * @return the loaded {@link Currency}
     * @throws UnsupportedCurrencyException if no property file exists for the currency
     */
    public Currency getCurrency(String currencyCode) {
        return cache.computeIfAbsent(currencyCode, this::load);
    }

    private Currency load(String code) {
        String path = String.format(RESOURCE_PATH, code);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new UnsupportedCurrencyException(code);
            }
            Properties props = new Properties();
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            return new Currency(
                    props.getProperty("currency.code"),
                    props.getProperty("currency.singular"),
                    props.getProperty("currency.plural"),
                    props.getProperty("currency.subunit.singular"),
                    props.getProperty("currency.subunit.plural"),
                    props.getProperty("currency.gender")
            );
        } catch (IOException e) {
            throw new UnsupportedCurrencyException(code, e);
        }
    }
}
