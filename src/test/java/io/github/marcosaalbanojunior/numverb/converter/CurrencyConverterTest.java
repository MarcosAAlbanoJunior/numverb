package io.github.marcosaalbanojunior.numverb.converter;

import io.github.marcosaalbanojunior.numverb.currency.Currency;
import io.github.marcosaalbanojunior.numverb.lang.Language;
import io.github.marcosaalbanojunior.numverb.lang.LanguageProvider;
import io.github.marcosaalbanojunior.numverb.model.ConversionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CurrencyConverter — pt-BR / BRL")
class CurrencyConverterTest {

    private CurrencyConverter converter;
    private ConversionContext brlContext;

    @BeforeEach
    void setUp() {
        LanguageProvider lp = new LanguageProvider();
        converter = new CurrencyConverter(lp);
        Currency brl = new Currency("BRL", "real", "reais", "centavo", "centavos", "masculine");
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
    @DisplayName("parametrized conversions")
    void conversions(String input, String expected) {
        assertEquals(expected.trim(), converter.convert(new BigDecimal(input.trim()), brlContext));
    }

    @Test
    @DisplayName("one billion BRL")
    void oneBillion() {
        assertEquals(
                "um bilhão de reais",
                converter.convert(new BigDecimal("1000000000.00"), brlContext)
        );
    }

    @Test
    @DisplayName("one trillion BRL")
    void oneTrillion() {
        assertEquals(
                "um trilhão de reais",
                converter.convert(new BigDecimal("1000000000000.00"), brlContext)
        );
    }
}
