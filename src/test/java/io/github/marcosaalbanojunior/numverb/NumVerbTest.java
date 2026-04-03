package io.github.marcosaalbanojunior.numverb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("NumVerb public API")
class NumVerbTest {

    @Test
    @DisplayName("currency() with default language and currency")
    void currencyDefault() {
        assertEquals(
                "mil duzentos e trinta e quatro reais e sessenta e oito centavos",
                NumVerb.currency(new BigDecimal("1234.68")).toWords()
        );
    }

    @Test
    @DisplayName("currency() — fifty cents")
    void fiftyCents() {
        assertEquals("cinquenta centavos", NumVerb.currency(new BigDecimal("0.50")).toWords());
    }

    @Test
    @DisplayName("currency() — one real")
    void oneReal() {
        assertEquals("um real", NumVerb.currency(new BigDecimal("1.00")).toWords());
    }

    @Test
    @DisplayName("currency() — one million reais")
    void oneMillion() {
        assertEquals(
                "um milhão de reais",
                NumVerb.currency(new BigDecimal("1000000.00")).toWords()
        );
    }

    @Test
    @DisplayName("currency() — explicit language and USD")
    void explicitLanguageAndCurrency() {
        String result = NumVerb.currency(new BigDecimal("1234.68"))
                .language("pt-BR")
                .currency("USD")
                .toWords();
        assertEquals(
                "mil duzentos e trinta e quatro dólares e sessenta e oito centavos",
                result
        );
    }

    @Test
    @DisplayName("cardinal() — basic")
    void cardinal() {
        assertEquals(
                "mil duzentos e trinta e quatro",
                NumVerb.cardinal(1234).language("pt-BR").toWords()
        );
    }

    @Test
    @DisplayName("cardinal() — zero")
    void cardinalZero() {
        assertEquals("zero", NumVerb.cardinal(0).toWords());
    }

    @Test
    @DisplayName("currency() — EUR")
    void eurCurrency() {
        assertEquals(
                "cem euros",
                NumVerb.currency(new BigDecimal("100.00")).currency("EUR").toWords()
        );
    }
}
