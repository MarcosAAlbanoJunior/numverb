package io.github.marcosaalbanojunior.numverb.edge;

import io.github.marcosaalbanojunior.numverb.Currencies;
import io.github.marcosaalbanojunior.numverb.NumVerb;
import io.github.marcosaalbanojunior.numverb.exception.NumberOutOfRangeException;
import io.github.marcosaalbanojunior.numverb.exception.UnsupportedCurrencyException;
import io.github.marcosaalbanojunior.numverb.exception.UnsupportedLanguageException;
import io.github.marcosaalbanojunior.numverb.lang.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Edge cases")
class EdgeCaseTest {

    // -------------------------------------------------------------------------
    // Zero
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("zero currency value")
    void zeroCurrency() {
        assertEquals("zero reais", NumVerb.currency(new BigDecimal("0.00")).toWords());
    }

    @Test
    @DisplayName("zero cardinal")
    void zeroCardinal() {
        assertEquals("zero", NumVerb.cardinal(0).toWords());
    }

    // -------------------------------------------------------------------------
    // Boundaries
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("negative value throws NumberOutOfRangeException")
    void negativeThrows() {
        assertThrows(NumberOutOfRangeException.class,
                () -> NumVerb.currency(new BigDecimal("-1.00")).toWords());
    }

    @Test
    @DisplayName("value exceeding max range throws NumberOutOfRangeException")
    void overRangeThrows() {
        assertThrows(NumberOutOfRangeException.class,
                () -> NumVerb.currency(new BigDecimal("1000000000000000000000000000.00")).toWords());
    }

    @Test
    @DisplayName("maximum supported value does not throw")
    void maxValueDoesNotThrow() {
        assertDoesNotThrow(() ->
                NumVerb.currency(new BigDecimal("999999999999999999999999.99")).toWords());
    }

    // -------------------------------------------------------------------------
    // Null inputs
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("null BigDecimal to currency() throws NullPointerException")
    void nullBigDecimalThrows() {
        assertThrows(NullPointerException.class, () -> NumVerb.currency((BigDecimal) null));
    }

    @Test
    @DisplayName("null String to currency() throws NullPointerException")
    void nullStringThrows() {
        assertThrows(NullPointerException.class, () -> NumVerb.currency((String) null));
    }

    @Test
    @DisplayName("null BigDecimal to cardinal() throws NullPointerException")
    void nullBigDecimalCardinalThrows() {
        assertThrows(NullPointerException.class, () -> NumVerb.cardinal((BigDecimal) null));
    }

    @Test
    @DisplayName("null BigInteger to cardinal() throws NullPointerException")
    void nullBigIntegerCardinalThrows() {
        assertThrows(NullPointerException.class, () -> NumVerb.cardinal((BigInteger) null));
    }

    // -------------------------------------------------------------------------
    // Unsupported language / currency
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("unsupported language throws UnsupportedLanguageException")
    void unsupportedLanguageThrows() {
        assertThrows(UnsupportedLanguageException.class,
                () -> NumVerb.currency(new BigDecimal("1.00")).language("xx-XX").toWords());
    }

    @Test
    @DisplayName("unsupported currency throws UnsupportedCurrencyException")
    void unsupportedCurrencyThrows() {
        assertThrows(UnsupportedCurrencyException.class,
                () -> NumVerb.currency(new BigDecimal("1.00")).currency("XYZ").toWords());
    }

    // -------------------------------------------------------------------------
    // Singular vs plural
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("singular vs plural — 1 real vs 2 reais")
    void singularPluralReal() {
        assertEquals("um real",    NumVerb.currency(new BigDecimal("1.00")).toWords());
        assertEquals("dois reais", NumVerb.currency(new BigDecimal("2.00")).toWords());
    }

    @Test
    @DisplayName("singular vs plural — 1 centavo vs 2 centavos")
    void singularPluralCentavo() {
        assertEquals("um centavo",    NumVerb.currency(new BigDecimal("0.01")).toWords());
        assertEquals("dois centavos", NumVerb.currency(new BigDecimal("0.02")).toWords());
    }

    @Test
    @DisplayName("singular vs plural — 1 dólar vs 2 dólares")
    void singularPluralDolar() {
        assertEquals("um dólar",    NumVerb.currency("1.00").currency(Currencies.USD).toWords());
        assertEquals("dois dólares", NumVerb.currency("2.00").currency(Currencies.USD).toWords());
    }

    // -------------------------------------------------------------------------
    // "cem" vs "cento"
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("100 → 'cem reais'")
    void cem() {
        assertEquals("cem reais", NumVerb.currency(new BigDecimal("100.00")).toWords());
    }

    @Test
    @DisplayName("101 → 'cento e um reais'")
    void cento() {
        assertEquals("cento e um reais", NumVerb.currency(new BigDecimal("101.00")).toWords());
    }

    // -------------------------------------------------------------------------
    // "de" preposition (round millions)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("1_000_000 → 'um milhão de reais'")
    void umMilhaoDeReais() {
        assertEquals("um milhão de reais",
                NumVerb.currency(new BigDecimal("1000000.00")).toWords());
    }

    @Test
    @DisplayName("2_000_000_000 → 'dois bilhões de reais'")
    void doisBilhoesDeReais() {
        assertEquals("dois bilhões de reais",
                NumVerb.currency(new BigDecimal("2000000000.00")).toWords());
    }

    @Test
    @DisplayName("1_000_000_000_000 → 'um trilhão de reais'")
    void umTrilhaoDeReais() {
        assertEquals("um trilhão de reais",
                NumVerb.currency(new BigDecimal("1000000000000.00")).toWords());
    }

    @Test
    @DisplayName("1_200_000 (million + thousands) → no 'de'")
    void millionWithThousandsNoDe() {
        assertEquals("um milhão duzentos mil reais",
                NumVerb.currency(new BigDecimal("1200000.00")).toWords());
    }

    @Test
    @DisplayName("1_000_001 (million + units) → no 'de'")
    void millionWithUnitsNoDe() {
        assertEquals("um milhão e um reais",
                NumVerb.currency(new BigDecimal("1000001.00")).toWords());
    }

    // -------------------------------------------------------------------------
    // Rounding
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("1.999 rounds to 2.00 — dois reais")
    void roundingUpToUnit() {
        assertEquals("dois reais", NumVerb.currency(new BigDecimal("1.999")).toWords());
    }

    @Test
    @DisplayName("0.995 rounds to 1.00 — um real")
    void roundingCentsToUnit() {
        assertEquals("um real", NumVerb.currency(new BigDecimal("0.995")).toWords());
    }

    @Test
    @DisplayName("0.994 rounds to 0.99 — noventa e nove centavos")
    void roundingCentsDown() {
        assertEquals("noventa e nove centavos",
                NumVerb.currency(new BigDecimal("0.994")).toWords());
    }

    // -------------------------------------------------------------------------
    // All supported currencies
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("all supported currencies produce output without throwing")
    void allCurrencies() {
        assertDoesNotThrow(() -> {
            NumVerb.currency("1.00").currency(Currencies.BRL).toWords();
            NumVerb.currency("1.00").currency(Currencies.USD).toWords();
            NumVerb.currency("1.00").currency(Currencies.EUR).toWords();
        });
    }

    // -------------------------------------------------------------------------
    // Large cardinals via new overloads
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Large cardinals via BigDecimal / BigInteger overloads")
    class LargeCardinals {

        @Test
        @DisplayName("setilhão via BigDecimal")
        void setilhaoBigDecimal() {
            assertEquals("um setilhão",
                    NumVerb.cardinal(new BigDecimal("1000000000000000000000000")).toWords());
        }

        @Test
        @DisplayName("setilhão via BigInteger")
        void setilhaoBigInteger() {
            assertEquals("um setilhão",
                    NumVerb.cardinal(new BigInteger("1000000000000000000000000")).toWords());
        }

        @Test
        @DisplayName("cardinal(BigDecimal) fractional part is ignored")
        void bigDecimalFractionalIgnored() {
            assertEquals("dois",
                    NumVerb.cardinal(new BigDecimal("2.99")).toWords());
        }
    }

    // -------------------------------------------------------------------------
    // Language object overload
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("language(Language.PT_BR) produces same result as language(\"pt-BR\")")
    void languageObjectEqualsString() {
        assertEquals(
                NumVerb.currency("1234.00").language("pt-BR").toWords(),
                NumVerb.currency("1234.00").language(Language.PT_BR).toWords());
    }
}
