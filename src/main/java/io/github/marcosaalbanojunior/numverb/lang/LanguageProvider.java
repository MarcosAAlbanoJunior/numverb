package io.github.marcosaalbanojunior.numverb.lang;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedLanguageException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and caches language property files from the classpath.
 *
 * <p>Language files are located at {@code /languages/{code}.properties} on the classpath
 * and are encoded in UTF-8.</p>
 *
 * <p>This class is thread-safe.</p>
 */
public class LanguageProvider {

    private static final String RESOURCE_PATH = "/languages/%s.properties";
    private final Map<String, Properties> cache = new ConcurrentHashMap<>();

    /**
     * Returns the properties for the given language code, loading from the classpath if needed.
     *
     * @param languageCode the BCP 47 language tag (e.g., {@code "pt-BR"})
     * @return the loaded {@link Properties}
     * @throws UnsupportedLanguageException if no property file exists for the language
     */
    public Properties getProperties(String languageCode) {
        return cache.computeIfAbsent(languageCode, this::load);
    }

    private Properties load(String code) {
        String path = String.format(RESOURCE_PATH, code);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new UnsupportedLanguageException(code);
            }
            Properties props = new Properties();
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            return props;
        } catch (IOException e) {
            throw new UnsupportedLanguageException(code, e);
        }
    }
}
