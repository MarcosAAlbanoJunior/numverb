package io.github.marcosaalbanojunior.numverb.converter;

import io.github.marcosaalbanojunior.numverb.lang.Language;
import io.github.marcosaalbanojunior.numverb.lang.LanguageProvider;
import io.github.marcosaalbanojunior.numverb.model.ConversionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        "20,         vinte",
        "21,         vinte e um",
        "100,        cem",
        "101,        cento e um",
        "1000,       mil",
        "1001,       mil e um",
        "1234,       mil duzentos e trinta e quatro",
        "1000000,    um milhão",
        "2000000,    dois milhões",
    })
    @DisplayName("parametrized cardinal conversions")
    void cardinals(String input, String expected) {
        assertEquals(expected.trim(), converter.convert(new BigDecimal(input.trim()), context));
    }
}
