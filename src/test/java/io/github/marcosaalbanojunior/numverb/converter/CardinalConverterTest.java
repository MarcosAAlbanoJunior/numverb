package io.github.marcosaalbanojunior.numverb.converter;

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

@DisplayName("CardinalConverter — pt-BR")
class CardinalConverterTest {

    private CardinalConverter converter;
    private ConversionContext context;

    @BeforeEach
    void setUp() {
        converter = new CardinalConverter(new LanguageProvider());
        context = new ConversionContext(Language.PT_BR, null);
    }

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "0,          zero",
        "1,          um",
        "2,          dois",
        "10,         dez",
        "11,         onze",
        "15,         quinze",
        "19,         dezenove",
        "20,         vinte",
        "21,         vinte e um",
        "99,         noventa e nove",
        "100,        cem",
        "101,        cento e um",
        "199,        cento e noventa e nove",
        "200,        duzentos",
        "999,        novecentos e noventa e nove",
        "1000,       mil",
        "1001,       mil e um",
        "1100,       mil cem",
        "1234,       mil duzentos e trinta e quatro",
        "2000,       dois mil",
        "9999,       nove mil novecentos e noventa e nove",
        "1000000,    um milhão",
        "2000000,    dois milhões",
        "1000000000, um bilhão",
    })
    @DisplayName("parametrized cardinal conversions")
    void cardinals(String input, String expected) {
        assertEquals(expected.trim(), converter.convert(new BigDecimal(input.trim()), context));
    }

    @Test
    @DisplayName("fractional part is ignored — 1.99 → 'um'")
    void fractionalPartIgnored() {
        assertEquals("um", converter.convert(new BigDecimal("1.99"), context));
    }

    @Test
    @DisplayName("quintilhão — number above Long.MAX_VALUE via BigDecimal")
    void quintilhao() {
        assertEquals("um quintilhão",
                converter.convert(new BigDecimal("1000000000000000000"), context));
    }

    @Test
    @DisplayName("setilhão — maximum supported value")
    void setilhao() {
        assertEquals("um setilhão",
                converter.convert(new BigDecimal("1000000000000000000000000"), context));
    }

    @Test
    @DisplayName("complex large number: 2_345_678_901")
    void complexLargeNumber() {
        // 901 >= 100 → no "e" between "mil" and "novecentos" (grammar rule: "e" only before parts < 100)
        assertEquals("dois bilhões trezentos e quarenta e cinco milhões seiscentos e setenta e oito mil novecentos e um",
                converter.convert(new BigDecimal("2345678901"), context));
    }
}
