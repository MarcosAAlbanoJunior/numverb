package io.github.marcosaalbanojunior.numverb.lang;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedLanguageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LanguageProvider")
class LanguageProviderTest {

    private final LanguageProvider provider = new LanguageProvider();

    @Test
    @DisplayName("loads pt-BR properties")
    void loadsPtBr() {
        Properties props = provider.getProperties("pt-BR");
        assertNotNull(props);
        assertEquals("um", props.getProperty("unit.1"));
        assertEquals("mil", props.getProperty("scale.mil"));
    }

    @Test
    @DisplayName("caches properties (same instance)")
    void cachesProperties() {
        Properties first = provider.getProperties("pt-BR");
        Properties second = provider.getProperties("pt-BR");
        assertSame(first, second);
    }

    @Test
    @DisplayName("throws for unsupported language")
    void throwsForUnsupported() {
        assertThrows(UnsupportedLanguageException.class, () -> provider.getProperties("xx-XX"));
    }
}
