package io.github.marcosaalbanojunior.numverb.edge;

import io.github.marcosaalbanojunior.numverb.NumVerb;
import io.github.marcosaalbanojunior.numverb.exception.NumberOutOfRangeException;
import io.github.marcosaalbanojunior.numverb.exception.UnsupportedCurrencyException;
import io.github.marcosaalbanojunior.numverb.exception.UnsupportedLanguageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Edge cases")
class EdgeCaseTest {

    @Test
    @DisplayName("zero value")
    void zero() {
        assertEquals("zero reais", NumVerb.currency(new BigDecimal("0.00")).toWords());
    }

    @Test
    @DisplayName("negative value throws")
    void negativeThrows() {
        assertThrows(NumberOutOfRangeException.class,
                () -> NumVerb.currency(new BigDecimal("-1.00")).toWords());
    }

    @Test
    @DisplayName("value exceeding max range throws")
    void overRangeThrows() {
        BigDecimal overMax = new BigDecimal("1000000000000000000000000000.00");
        assertThrows(NumberOutOfRangeException.class,
                () -> NumVerb.currency(overMax).toWords());
    }

    @Test
    @DisplayName("unsupported language throws")
    void unsupportedLanguageThrows() {
        assertThrows(UnsupportedLanguageException.class,
                () -> NumVerb.currency(new BigDecimal("1.00")).language("xx-XX").toWords());
    }

    @Test
    @DisplayName("unsupported currency throws")
    void unsupportedCurrencyThrows() {
        assertThrows(UnsupportedCurrencyException.class,
                () -> NumVerb.currency(new BigDecimal("1.00")).currency("XYZ").toWords());
    }

    @Test
    @DisplayName("singular vs plural — 1 real vs 2 reais")
    void singularPlural() {
        assertEquals("um real", NumVerb.currency(new BigDecimal("1.00")).toWords());
        assertEquals("dois reais", NumVerb.currency(new BigDecimal("2.00")).toWords());
    }

    @Test
    @DisplayName("singular vs plural — 1 centavo vs 2 centavos")
    void singularPluralCents() {
        assertEquals("um centavo", NumVerb.currency(new BigDecimal("0.01")).toWords());
        assertEquals("dois centavos", NumVerb.currency(new BigDecimal("0.02")).toWords());
    }

    @Test
    @DisplayName("cem vs cento")
    void cemVsCento() {
        assertEquals("cem reais", NumVerb.currency(new BigDecimal("100.00")).toWords());
        assertEquals("cento e um reais", NumVerb.currency(new BigDecimal("101.00")).toWords());
    }

    @Test
    @DisplayName("um milhão de reais — preposition 'de'")
    void milhaoDeReais() {
        assertEquals("um milhão de reais", NumVerb.currency(new BigDecimal("1000000.00")).toWords());
    }

    @Test
    @DisplayName("dois bilhões de reais")
    void doisBilhoes() {
        assertEquals("dois bilhões de reais", NumVerb.currency(new BigDecimal("2000000000.00")).toWords());
    }

    @Test
    @DisplayName("large value — quatrilhão")
    void quatrilhao() {
        assertEquals(
                "um quatrilhão de reais",
                NumVerb.currency(new BigDecimal("1000000000000000.00")).toWords()
        );
    }

    @Test
    @DisplayName("null value throws NullPointerException")
    void nullValueThrows() {
        assertThrows(NullPointerException.class, () -> NumVerb.currency(null));
    }

    @Test
    @DisplayName("rounding: 1.999 rounds to 2.00")
    void rounding() {
        assertEquals("dois reais", NumVerb.currency(new BigDecimal("1.999")).toWords());
    }

    @Test
    @DisplayName("all supported currencies produce output")
    void allCurrencies() {
        assertDoesNotThrow(() -> {
            NumVerb.currency(new BigDecimal("1.00")).currency("BRL").toWords();
            NumVerb.currency(new BigDecimal("1.00")).currency("USD").toWords();
            NumVerb.currency(new BigDecimal("1.00")).currency("EUR").toWords();
        });
    }
}
