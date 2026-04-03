package io.github.marcosaalbanojunior.numverb.converter;

import io.github.marcosaalbanojunior.numverb.currency.Currency;
import io.github.marcosaalbanojunior.numverb.lang.Language;
import io.github.marcosaalbanojunior.numverb.lang.LanguageProvider;
import io.github.marcosaalbanojunior.numverb.model.ConversionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CurrencyConverter — pt-BR")
class CurrencyConverterTest {

    private CurrencyConverter converter;
    private ConversionContext brlContext;

    @BeforeEach
    void setUp() {
        LanguageProvider lp = new LanguageProvider();
        converter = new CurrencyConverter(lp);
        Currency brl = new Currency("BRL", "real", "reais", "centavo", "centavos", "masculine", "masculine");
        brlContext = new ConversionContext(Language.PT_BR, brl);
    }

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "0.00,       zero reais",
        "0.01,       um centavo",
        "0.50,       cinquenta centavos",
        "0.99,       noventa e nove centavos",
        "1.00,       um real",
        "1.01,       um real e um centavo",
        "1.99,       um real e noventa e nove centavos",
        "2.00,       dois reais",
        "10.00,      dez reais",
        "11.00,      onze reais",
        "19.00,      dezenove reais",
        "20.00,      vinte reais",
        "21.00,      vinte e um reais",
        "99.00,      noventa e nove reais",
        "100.00,     cem reais",
        "101.00,     cento e um reais",
        "200.00,     duzentos reais",
        "999.00,     novecentos e noventa e nove reais",
        "1000.00,    mil reais",
        "1001.00,    mil e um reais",
        "1100.00,    mil cem reais",
        "1234.68,    mil duzentos e trinta e quatro reais e sessenta e oito centavos",
        "2000.00,    dois mil reais",
        "1000000.00, um milhão de reais",
        "2000000.00, dois milhões de reais",
        "1000001.00, um milhão e um reais",
        "1200000.00, um milhão duzentos mil reais",
    })
    @DisplayName("parametrized BRL conversions")
    void conversions(String input, String expected) {
        assertEquals(expected.trim(), converter.convert(new BigDecimal(input.trim()), brlContext));
    }

    @Test
    @DisplayName("one billion BRL")
    void oneBillion() {
        assertEquals("um bilhão de reais",
                converter.convert(new BigDecimal("1000000000.00"), brlContext));
    }

    @Test
    @DisplayName("one trillion BRL")
    void oneTrillion() {
        assertEquals("um trilhão de reais",
                converter.convert(new BigDecimal("1000000000000.00"), brlContext));
    }

    @Test
    @DisplayName("one quatrilhão BRL")
    void oneQuatrilhao() {
        assertEquals("um quatrilhão de reais",
                converter.convert(new BigDecimal("1000000000000000.00"), brlContext));
    }

    @Test
    @DisplayName("one sextilhão BRL — largest reachable denomination within MAX_VALUE")
    void oneSextilhao() {
        // setilhão = 10^24 > MAX_VALUE (999...999.99), so the largest full scale is sextilhão (10^21)
        assertEquals("um sextilhão de reais",
                converter.convert(new BigDecimal("1000000000000000000000.00"), brlContext));
    }

    // -------------------------------------------------------------------------
    // Rounding
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("1.999 rounds up to 2.00 reais")
    void roundingUp() {
        assertEquals("dois reais", converter.convert(new BigDecimal("1.999"), brlContext));
    }

    @Test
    @DisplayName("1.994 rounds down to 1.99 reais e noventa e nove centavos")
    void roundingDown() {
        assertEquals("um real e noventa e nove centavos",
                converter.convert(new BigDecimal("1.994"), brlContext));
    }

    // -------------------------------------------------------------------------
    // Feminine currency (subunit gender independent of main currency gender)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Feminine currency (hypothetical — validates gender isolation)")
    class FeminineGenderCurrency {

        private ConversionContext feminineContext;

        @BeforeEach
        void setUp() {
            LanguageProvider lp = new LanguageProvider();
            converter = new CurrencyConverter(lp);
            // "libra" is feminine in Portuguese; subunit "penny/pence" masculine here
            Currency libra = new Currency("GBP", "libra", "libras",
                    "centavo", "centavos", "feminine", "masculine");
            feminineContext = new ConversionContext(Language.PT_BR, libra);
        }

        @Test
        @DisplayName("1 libra — singular feminine unit")
        void oneLibra() {
            assertEquals("uma libra",
                    converter.convert(new BigDecimal("1.00"), feminineContext));
        }

        @Test
        @DisplayName("2 libras — plural feminine unit")
        void twoLibras() {
            assertEquals("duas libras",
                    converter.convert(new BigDecimal("2.00"), feminineContext));
        }

        @Test
        @DisplayName("21 libras — feminine")
        void twentyOneLibras() {
            assertEquals("vinte e uma libras",
                    converter.convert(new BigDecimal("21.00"), feminineContext));
        }

        @Test
        @DisplayName("200 libras — feminine hundreds")
        void twoHundredLibras() {
            assertEquals("duzentas libras",
                    converter.convert(new BigDecimal("200.00"), feminineContext));
        }

        @Test
        @DisplayName("201 libras — 'duzentas e uma libras'")
        void twoHundredOneLibras() {
            assertEquals("duzentas e uma libras",
                    converter.convert(new BigDecimal("201.00"), feminineContext));
        }

        @Test
        @DisplayName("2000 libras — scale count stays masculine: 'dois mil libras'")
        void twoThousandLibrasScaleCountMasculine() {
            assertEquals("dois mil libras",
                    converter.convert(new BigDecimal("2000.00"), feminineContext));
        }

        @Test
        @DisplayName("21000 libras — 'vinte e um mil libras' (scale count masculine)")
        void twentyOneThousandLibras() {
            assertEquals("vinte e um mil libras",
                    converter.convert(new BigDecimal("21000.00"), feminineContext));
        }

        @Test
        @DisplayName("2000000 libras — 'dois milhões de libras' (scale masculine + preposition)")
        void twoMillionLibras() {
            assertEquals("dois milhões de libras",
                    converter.convert(new BigDecimal("2000000.00"), feminineContext));
        }

        @Test
        @DisplayName("2001 libras — 'dois mil e uma libras' (scale masculine, remainder feminine)")
        void twoThousandOneLibras() {
            assertEquals("dois mil e uma libras",
                    converter.convert(new BigDecimal("2001.00"), feminineContext));
        }

        @Test
        @DisplayName("1 centavo — subunit gender is masculine")
        void oneCentavoMasculineSubunit() {
            assertEquals("um centavo",
                    converter.convert(new BigDecimal("0.01"), feminineContext));
        }

        @Test
        @DisplayName("2 centavos — subunit gender is masculine")
        void twoCentavosMasculineSubunit() {
            assertEquals("dois centavos",
                    converter.convert(new BigDecimal("0.02"), feminineContext));
        }
    }
}
