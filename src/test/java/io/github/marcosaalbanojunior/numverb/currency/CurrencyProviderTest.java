package io.github.marcosaalbanojunior.numverb.currency;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedCurrencyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CurrencyProvider")
class CurrencyProviderTest {

    private final CurrencyProvider provider = new CurrencyProvider();

    // -------------------------------------------------------------------------
    // pt-BR currencies
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "pt-BR/{0}")
    @CsvSource({
        "BRL, real,   reais,   centavo, centavos, masculine, masculine",
        "USD, dólar,  dólares, centavo, centavos, masculine, masculine",
        "EUR, euro,   euros,   centavo, centavos, masculine, masculine",
        "GBP, libra,  libras,  penny,   pence,    feminine,  masculine",
    })
    @DisplayName("loads all fields — pt-BR")
    void loadsPtBr(String code, String singular, String plural,
                   String subSingular, String subPlural,
                   String gender, String subGender) {
        Currency c = provider.getCurrency("pt-BR", code);
        assertAll(
                () -> assertEquals(code,        c.code()),
                () -> assertEquals(singular,    c.singular()),
                () -> assertEquals(plural,       c.plural()),
                () -> assertEquals(subSingular,  c.subunitSingular()),
                () -> assertEquals(subPlural,    c.subunitPlural()),
                () -> assertEquals(gender,       c.gender()),
                () -> assertEquals(subGender,    c.subunitGender())
        );
    }

    // -------------------------------------------------------------------------
    // en-US currencies
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "en-US/{0}")
    @CsvSource({
        "USD, dollar, dollars, cent,   cents,    masculine, masculine",
        "EUR, euro,   euros,   cent,   cents,    masculine, masculine",
        "GBP, pound,  pounds,  penny,  pence,    masculine, masculine",
        "BRL, real,   reals,   centavo, centavos, masculine, masculine",
    })
    @DisplayName("loads all fields — en-US")
    void loadsEnUs(String code, String singular, String plural,
                   String subSingular, String subPlural,
                   String gender, String subGender) {
        Currency c = provider.getCurrency("en-US", code);
        assertAll(
                () -> assertEquals(code,        c.code()),
                () -> assertEquals(singular,    c.singular()),
                () -> assertEquals(plural,       c.plural()),
                () -> assertEquals(subSingular,  c.subunitSingular()),
                () -> assertEquals(subPlural,    c.subunitPlural()),
                () -> assertEquals(gender,       c.gender()),
                () -> assertEquals(subGender,    c.subunitGender())
        );
    }

    // -------------------------------------------------------------------------
    // Error handling
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("throws UnsupportedCurrencyException for unknown code")
    void throwsForUnsupportedCode() {
        assertThrows(UnsupportedCurrencyException.class,
                () -> provider.getCurrency("pt-BR", "XYZ"));
    }

    @Test
    @DisplayName("throws UnsupportedCurrencyException for currency not configured for that language")
    void throwsWhenCurrencyNotAvailableForLanguage() {
        assertThrows(UnsupportedCurrencyException.class,
                () -> provider.getCurrency("pt-BR", "JPY"));
    }

    @Test
    @DisplayName("exception message contains both currency code and language code")
    void exceptionMessageContainsBothCodes() {
        UnsupportedCurrencyException ex = assertThrows(UnsupportedCurrencyException.class,
                () -> provider.getCurrency("en-US", "JPY"));
        assertAll(
                () -> assertTrue(ex.getMessage().contains("JPY"),    "message should contain currency code"),
                () -> assertTrue(ex.getMessage().contains("en-US"),  "message should contain language code")
        );
    }

    // -------------------------------------------------------------------------
    // Caching
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Cache behaviour")
    class CacheBehaviour {

        @Test
        @DisplayName("same instance on repeated calls for same language + code")
        void cachesSameInstance() {
            Currency first  = provider.getCurrency("pt-BR", "BRL");
            Currency second = provider.getCurrency("pt-BR", "BRL");
            assertSame(first, second);
        }

        @Test
        @DisplayName("pt-BR/USD and en-US/USD are cached independently")
        void separateCacheEntriesPerLanguage() {
            Currency ptBrUsd = provider.getCurrency("pt-BR", "USD");
            Currency enUsUsd = provider.getCurrency("en-US", "USD");
            assertNotSame(ptBrUsd, enUsUsd);
            assertEquals("dólar",  ptBrUsd.singular());
            assertEquals("dollar", enUsUsd.singular());
        }

        @Test
        @DisplayName("separate provider instances have independent caches")
        void separateInstancesHaveIndependentCaches() {
            Currency fromFirst  = new CurrencyProvider().getCurrency("pt-BR", "BRL");
            Currency fromSecond = new CurrencyProvider().getCurrency("pt-BR", "BRL");
            assertEquals(fromFirst, fromSecond);
            assertNotSame(fromFirst, fromSecond);
        }
    }
}
