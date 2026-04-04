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

@DisplayName("CurrencyConverter — en-US")
class EnUSCurrencyConverterTest {

    private CurrencyConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CurrencyConverter(new LanguageProvider());
    }

    // -------------------------------------------------------------------------
    // USD
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("USD — dollar / cent")
    class Usd {

        private ConversionContext ctx;

        @BeforeEach
        void setUp() {
            Currency usd = new Currency("USD", "dollar", "dollars", "cent", "cents",
                    "masculine", "masculine");
            ctx = new ConversionContext(Language.EN_US, usd);
        }

        @ParameterizedTest(name = "{0} → \"{1}\"")
        @CsvSource({
            "0.00,  zero dollars",
            "0.01,  one cent",
            "0.02,  two cents",
            "0.10,  ten cents",
            "0.50,  fifty cents",
            "0.99,  ninety-nine cents",
            "1.00,  one dollar",
            "1.01,  one dollar and one cent",
            "1.02,  one dollar and two cents",
            "1.50,  one dollar and fifty cents",
            "1.99,  one dollar and ninety-nine cents",
            "2.00,  two dollars",
            "10.00, ten dollars",
            "11.00, eleven dollars",
            "20.00, twenty dollars",
            "21.00, twenty-one dollars",
            "99.00, ninety-nine dollars",
            "100.00,  one hundred dollars",
            "101.00,  one hundred and one dollars",
            "200.00,  two hundred dollars",
            "999.00,  nine hundred and ninety-nine dollars",
            "1000.00, one thousand dollars",
            "1001.00, one thousand and one dollars",
            "1100.00, one thousand one hundred dollars",
            "1234.68, one thousand two hundred and thirty-four dollars and sixty-eight cents",
            "2000.00, two thousand dollars",
        })
        @DisplayName("parametrized USD conversions")
        void usdConversions(String input, String expected) {
            assertEquals(expected.trim(),
                    converter.convert(new BigDecimal(input.trim()), ctx));
        }

        @Test
        @DisplayName("one million dollars — no 'of' preposition")
        void oneMillion() {
            assertEquals("one million dollars",
                    converter.convert(new BigDecimal("1000000.00"), ctx));
        }

        @Test
        @DisplayName("two million dollars")
        void twoMillion() {
            assertEquals("two million dollars",
                    converter.convert(new BigDecimal("2000000.00"), ctx));
        }

        @Test
        @DisplayName("one million and one dollars (no 'of' — not a round million)")
        void millionPlusOne() {
            assertEquals("one million and one dollars",
                    converter.convert(new BigDecimal("1000001.00"), ctx));
        }

        @Test
        @DisplayName("one million two hundred thousand dollars")
        void millionWithThousands() {
            assertEquals("one million two hundred thousand dollars",
                    converter.convert(new BigDecimal("1200000.00"), ctx));
        }

        @Test
        @DisplayName("one million dollars and fifty cents — no 'of' + cents")
        void millionWithCents() {
            assertEquals("one million dollars and fifty cents",
                    converter.convert(new BigDecimal("1000000.50"), ctx));
        }

        @Test
        @DisplayName("one billion dollars")
        void oneBillion() {
            assertEquals("one billion dollars",
                    converter.convert(new BigDecimal("1000000000.00"), ctx));
        }

        @Test
        @DisplayName("rounding: 1.999 rounds to two dollars")
        void roundingUp() {
            assertEquals("two dollars",
                    converter.convert(new BigDecimal("1.999"), ctx));
        }

        @Test
        @DisplayName("rounding: 1.994 rounds to one dollar and ninety-nine cents")
        void roundingDown() {
            assertEquals("one dollar and ninety-nine cents",
                    converter.convert(new BigDecimal("1.994"), ctx));
        }
    }

    // -------------------------------------------------------------------------
    // EUR
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("EUR — euro / cent")
    class Eur {

        private ConversionContext ctx;

        @BeforeEach
        void setUp() {
            Currency eur = new Currency("EUR", "euro", "euros", "cent", "cents",
                    "masculine", "masculine");
            ctx = new ConversionContext(Language.EN_US, eur);
        }

        @Test
        @DisplayName("zero euros")
        void zeroEuros() {
            assertEquals("zero euros",
                    converter.convert(new BigDecimal("0.00"), ctx));
        }

        @Test
        @DisplayName("one euro — singular")
        void oneEuro() {
            assertEquals("one euro",
                    converter.convert(new BigDecimal("1.00"), ctx));
        }

        @Test
        @DisplayName("two euros — plural")
        void twoEuros() {
            assertEquals("two euros",
                    converter.convert(new BigDecimal("2.00"), ctx));
        }

        @Test
        @DisplayName("one euro and one cent")
        void oneEuroOneCent() {
            assertEquals("one euro and one cent",
                    converter.convert(new BigDecimal("1.01"), ctx));
        }

        @Test
        @DisplayName("one hundred euros")
        void oneHundredEuros() {
            assertEquals("one hundred euros",
                    converter.convert(new BigDecimal("100.00"), ctx));
        }
    }

    // -------------------------------------------------------------------------
    // GBP — penny/pence subunit
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GBP — pound / penny/pence")
    class Gbp {

        private ConversionContext ctx;

        @BeforeEach
        void setUp() {
            Currency gbp = new Currency("GBP", "pound", "pounds", "penny", "pence",
                    "masculine", "masculine");
            ctx = new ConversionContext(Language.EN_US, gbp);
        }

        @Test
        @DisplayName("zero pounds")
        void zeroPounds() {
            assertEquals("zero pounds",
                    converter.convert(new BigDecimal("0.00"), ctx));
        }

        @Test
        @DisplayName("one penny — singular subunit")
        void onePenny() {
            assertEquals("one penny",
                    converter.convert(new BigDecimal("0.01"), ctx));
        }

        @Test
        @DisplayName("two pence — plural subunit")
        void twoPence() {
            assertEquals("two pence",
                    converter.convert(new BigDecimal("0.02"), ctx));
        }

        @Test
        @DisplayName("fifty pence")
        void fiftyPence() {
            assertEquals("fifty pence",
                    converter.convert(new BigDecimal("0.50"), ctx));
        }

        @Test
        @DisplayName("one pound — singular")
        void onePound() {
            assertEquals("one pound",
                    converter.convert(new BigDecimal("1.00"), ctx));
        }

        @Test
        @DisplayName("two pounds — plural")
        void twoPounds() {
            assertEquals("two pounds",
                    converter.convert(new BigDecimal("2.00"), ctx));
        }

        @Test
        @DisplayName("one pound and one penny")
        void onePoundOnePenny() {
            assertEquals("one pound and one penny",
                    converter.convert(new BigDecimal("1.01"), ctx));
        }

        @Test
        @DisplayName("one pound and two pence")
        void onePoundTwoPence() {
            assertEquals("one pound and two pence",
                    converter.convert(new BigDecimal("1.02"), ctx));
        }

        @Test
        @DisplayName("twenty-one pounds")
        void twentyOnePounds() {
            assertEquals("twenty-one pounds",
                    converter.convert(new BigDecimal("21.00"), ctx));
        }
    }
}
