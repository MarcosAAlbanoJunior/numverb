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
        "BRL, real,  reais,   centavo, centavos, masculine, masculine",
        "USD, dólar, dólares, centavo, centavos, masculine, masculine",
        "EUR, euro,  euros,   centavo, centavos, masculine, masculine",
    })
    @DisplayName("loads all fields of supported currencies")
    void loadsSupported(String code, String singular, String plural,
                        String subSingular, String subPlural,
                        String gender, String subunitGender) {
        Currency c = provider.getCurrency(code);
        assertAll(
                () -> assertEquals(code,         c.code()),
                () -> assertEquals(singular,     c.singular()),
                () -> assertEquals(plural,        c.plural()),
                () -> assertEquals(subSingular,   c.subunitSingular()),
                () -> assertEquals(subPlural,     c.subunitPlural()),
                () -> assertEquals(gender,        c.gender()),
                () -> assertEquals(subunitGender, c.subunitGender())
        );
    }

    @Test
    @DisplayName("throws UnsupportedCurrencyException for unknown code")
    void throwsForUnsupported() {
        assertThrows(UnsupportedCurrencyException.class, () -> provider.getCurrency("XYZ"));
    }

    @Test
    @DisplayName("exception message contains the unsupported code")
    void exceptionMessageContainsCode() {
        UnsupportedCurrencyException ex = assertThrows(UnsupportedCurrencyException.class,
                () -> provider.getCurrency("GBP"));
        assertTrue(ex.getMessage().contains("GBP"));
    }

    @Test
    @DisplayName("caches currency — same instance on repeated calls")
    void cachesCurrency() {
        Currency first  = provider.getCurrency("BRL");
        Currency second = provider.getCurrency("BRL");
        assertSame(first, second);
    }

    @Test
    @DisplayName("caches are independent across different instances")
    void separateInstancesHaveSeparateCaches() {
        Currency fromFirst  = new CurrencyProvider().getCurrency("BRL");
        Currency fromSecond = new CurrencyProvider().getCurrency("BRL");
        // same value, but different CurrencyProvider instances = different cache entries
        assertEquals(fromFirst, fromSecond);
        assertNotSame(fromFirst, fromSecond);
    }
}
