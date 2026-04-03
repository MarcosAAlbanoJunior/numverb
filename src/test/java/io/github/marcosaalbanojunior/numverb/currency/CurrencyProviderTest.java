package io.github.marcosaalbanojunior.numverb.currency;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedCurrencyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CurrencyProvider")
class CurrencyProviderTest {

    private final CurrencyProvider provider = new CurrencyProvider();

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "BRL, real,  reais,   centavo, centavos",
        "USD, dólar, dólares, centavo, centavos",
        "EUR, euro,  euros,   centavo, centavos",
    })
    @DisplayName("loads supported currencies")
    void loadsSupported(String code, String singular, String plural, String subSingular, String subPlural) {
        Currency c = provider.getCurrency(code);
        assertEquals(code, c.code());
        assertEquals(singular, c.singular());
        assertEquals(plural, c.plural());
        assertEquals(subSingular, c.subunitSingular());
        assertEquals(subPlural, c.subunitPlural());
        assertEquals("masculine", c.gender());
    }

    @Test
    @DisplayName("throws for unsupported currency")
    void throwsForUnsupported() {
        assertThrows(UnsupportedCurrencyException.class, () -> provider.getCurrency("XYZ"));
    }

    @Test
    @DisplayName("caches currency (same instance)")
    void cachesCurrency() {
        Currency first = provider.getCurrency("BRL");
        Currency second = provider.getCurrency("BRL");
        assertSame(first, second);
    }
}
