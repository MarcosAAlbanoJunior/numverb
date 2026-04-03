package io.github.marcosaalbanojunior.numverb;

import io.github.marcosaalbanojunior.numverb.lang.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("NumVerb public API")
class NumVerbTest {

    // -------------------------------------------------------------------------
    // currency(BigDecimal) — original API
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("currency(BigDecimal) — defaults to pt-BR + BRL")
    void currencyDefault() {
        assertEquals(
                "mil duzentos e trinta e quatro reais e sessenta e oito centavos",
                NumVerb.currency(new BigDecimal("1234.68")).toWords());
    }

    @Test
    @DisplayName("currency(BigDecimal) — fifty cents")
    void fiftyCents() {
        assertEquals("cinquenta centavos",
                NumVerb.currency(new BigDecimal("0.50")).toWords());
    }

    @Test
    @DisplayName("currency(BigDecimal) — one real")
    void oneReal() {
        assertEquals("um real",
                NumVerb.currency(new BigDecimal("1.00")).toWords());
    }

    @Test
    @DisplayName("currency(BigDecimal) — one million reais")
    void oneMillion() {
        assertEquals("um milhão de reais",
                NumVerb.currency(new BigDecimal("1000000.00")).toWords());
    }

    @Test
    @DisplayName("currency(BigDecimal) — explicit language string + USD")
    void explicitLanguageStringAndUSD() {
        assertEquals(
                "mil duzentos e trinta e quatro dólares e sessenta e oito centavos",
                NumVerb.currency(new BigDecimal("1234.68"))
                        .language("pt-BR")
                        .currency("USD")
                        .toWords());
    }

    @Test
    @DisplayName("currency(BigDecimal) — EUR")
    void eurCurrency() {
        assertEquals("cem euros",
                NumVerb.currency(new BigDecimal("100.00")).currency("EUR").toWords());
    }

    // -------------------------------------------------------------------------
    // currency(String) — convenience overload
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("currency(String) overload")
    class CurrencyString {

        @Test
        @DisplayName("produces the same result as currency(BigDecimal)")
        void sameResultAsBigDecimal() {
            assertEquals(
                    NumVerb.currency(new BigDecimal("1234.68")).toWords(),
                    NumVerb.currency("1234.68").toWords());
        }

        @Test
        @DisplayName("works with language + currency chaining")
        void chainingWorks() {
            assertEquals(
                    "cinquenta dólares",
                    NumVerb.currency("50.00").language("pt-BR").currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("null throws NullPointerException")
        void nullThrows() {
            assertThrows(NullPointerException.class, () -> NumVerb.currency((String) null));
        }

        @Test
        @DisplayName("invalid decimal string throws NumberFormatException")
        void invalidStringThrows() {
            assertThrows(NumberFormatException.class, () -> NumVerb.currency("not-a-number").toWords());
        }
    }

    // -------------------------------------------------------------------------
    // cardinal(long) — original API
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("cardinal(long) — basic")
    void cardinal() {
        assertEquals("mil duzentos e trinta e quatro",
                NumVerb.cardinal(1234).language("pt-BR").toWords());
    }

    @Test
    @DisplayName("cardinal(long) — zero")
    void cardinalZero() {
        assertEquals("zero", NumVerb.cardinal(0).toWords());
    }

    @Test
    @DisplayName("cardinal(long) — Long.MAX_VALUE")
    void cardinalLongMaxValue() {
        // Long.MAX_VALUE = 9_223_372_036_854_775_807
        // Grammar notes:
        //   "trilhões e trinta e seis bilhões" — "e" because 36 < 100
        //   "setenta e cinco mil oitocentos e sete" — no "e" before "oitocentos" because 807 >= 100
        assertEquals("nove quintilhões duzentos e vinte e três quatrilhões trezentos e setenta e dois trilhões e trinta e seis bilhões oitocentos e cinquenta e quatro milhões setecentos e setenta e cinco mil oitocentos e sete",
                NumVerb.cardinal(Long.MAX_VALUE).toWords());
    }

    // -------------------------------------------------------------------------
    // cardinal(BigDecimal) — new overload for full range
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("cardinal(BigDecimal) overload")
    class CardinalBigDecimal {

        @Test
        @DisplayName("same result as cardinal(long) for small values")
        void sameAsLong() {
            assertEquals(
                    NumVerb.cardinal(1234L).toWords(),
                    NumVerb.cardinal(new BigDecimal("1234")).toWords());
        }

        @Test
        @DisplayName("fractional part is silently ignored")
        void fractionalIgnored() {
            assertEquals("um", NumVerb.cardinal(new BigDecimal("1.99")).toWords());
        }

        @Test
        @DisplayName("setilhão — above Long.MAX_VALUE")
        void setilhao() {
            assertEquals("um setilhão",
                    NumVerb.cardinal(new BigDecimal("1000000000000000000000000")).toWords());
        }

        @Test
        @DisplayName("null throws NullPointerException")
        void nullThrows() {
            assertThrows(NullPointerException.class, () -> NumVerb.cardinal((BigDecimal) null));
        }
    }

    // -------------------------------------------------------------------------
    // cardinal(BigInteger) — new overload
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("cardinal(BigInteger) overload")
    class CardinalBigInteger {

        @Test
        @DisplayName("same result as cardinal(long) for small values")
        void sameAsLong() {
            assertEquals(
                    NumVerb.cardinal(999L).toWords(),
                    NumVerb.cardinal(BigInteger.valueOf(999)).toWords());
        }

        @Test
        @DisplayName("quintilhão via BigInteger")
        void quintilhao() {
            assertEquals("um quintilhão",
                    NumVerb.cardinal(new BigInteger("1000000000000000000")).toWords());
        }

        @Test
        @DisplayName("null throws NullPointerException")
        void nullThrows() {
            assertThrows(NullPointerException.class, () -> NumVerb.cardinal((BigInteger) null));
        }
    }

    // -------------------------------------------------------------------------
    // language(Language) — new overload on both builders
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("language(Language) overload")
    class LanguageObject {

        @Test
        @DisplayName("currency builder accepts Language.PT_BR")
        void currencyBuilderAcceptsLanguageObject() {
            assertEquals(
                    NumVerb.currency("100.00").language("pt-BR").toWords(),
                    NumVerb.currency("100.00").language(Language.PT_BR).toWords());
        }

        @Test
        @DisplayName("cardinal builder accepts Language.PT_BR")
        void cardinalBuilderAcceptsLanguageObject() {
            assertEquals(
                    NumVerb.cardinal(100L).language("pt-BR").toWords(),
                    NumVerb.cardinal(100L).language(Language.PT_BR).toWords());
        }

        @Test
        @DisplayName("null Language throws NullPointerException")
        void nullLanguageThrows() {
            assertThrows(NullPointerException.class,
                    () -> NumVerb.currency("1.00").language((Language) null).toWords());
        }
    }

    // -------------------------------------------------------------------------
    // Currencies constants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Currencies constants")
    class CurrenciesConstants {

        @Test
        @DisplayName("Currencies.BRL produces same result as literal \"BRL\"")
        void brlConstant() {
            assertEquals(
                    NumVerb.currency("1.00").currency("BRL").toWords(),
                    NumVerb.currency("1.00").currency(Currencies.BRL).toWords());
        }

        @Test
        @DisplayName("Currencies.USD")
        void usdConstant() {
            assertEquals("um dólar",
                    NumVerb.currency("1.00").currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("Currencies.EUR")
        void eurConstant() {
            assertEquals("um euro",
                    NumVerb.currency("1.00").currency(Currencies.EUR).toWords());
        }
    }
}
