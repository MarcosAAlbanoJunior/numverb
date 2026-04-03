package io.github.marcosaalbanojunior.numverb.lang;

import io.github.marcosaalbanojunior.numverb.exception.UnsupportedLanguageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LanguageRules — pt-BR")
class LanguageRulesTest {

    private LanguageRules rules;

    @BeforeEach
    void setUp() {
        rules = new LanguageRules("pt-BR", new LanguageProvider());
    }

    // -------------------------------------------------------------------------
    // Basic masculine conversions
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "0,          zero",
        "1,          um",
        "2,          dois",
        "10,         dez",
        "11,         onze",
        "20,         vinte",
        "21,         vinte e um",
        "99,         noventa e nove",
        "100,        cem",
        "101,        cento e um",
        "200,        duzentos",
        "999,        novecentos e noventa e nove",
        "1000,       mil",
        "1001,       mil e um",
        "1100,       mil cem",
        "1200,       mil duzentos",
        "2000,       dois mil",
        "1000000,    um milhão",
        "2000000,    dois milhões",
        "1000000000, um bilhão",
    })
    @DisplayName("masculine conversions")
    void masculine(String input, String expected) {
        assertEquals(expected.trim(),
                rules.convertToWords(new BigInteger(input.trim()), "masculine"));
    }

    // -------------------------------------------------------------------------
    // Feminine gender — only units 1-2 and hundreds change
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Feminine gender")
    class FeminineGender {

        @Test
        @DisplayName("unit 1 → uma")
        void unit1() {
            assertEquals("uma", rules.convertToWords(BigInteger.ONE, "feminine"));
        }

        @Test
        @DisplayName("unit 2 → duas")
        void unit2() {
            assertEquals("duas", rules.convertToWords(BigInteger.TWO, "feminine"));
        }

        @Test
        @DisplayName("21 feminine → vinte e uma")
        void twentyOne() {
            assertEquals("vinte e uma",
                    rules.convertToWords(BigInteger.valueOf(21), "feminine"));
        }

        @Test
        @DisplayName("22 feminine → vinte e duas")
        void twentyTwo() {
            assertEquals("vinte e duas",
                    rules.convertToWords(BigInteger.valueOf(22), "feminine"));
        }

        @Test
        @DisplayName("200 feminine → duzentas")
        void twoHundred() {
            assertEquals("duzentas",
                    rules.convertToWords(BigInteger.valueOf(200), "feminine"));
        }

        @Test
        @DisplayName("201 feminine → duzentas e uma")
        void twoHundredOne() {
            assertEquals("duzentas e uma",
                    rules.convertToWords(BigInteger.valueOf(201), "feminine"));
        }

        @Test
        @DisplayName("221 feminine → duzentas e vinte e uma")
        void twoHundredTwentyOne() {
            assertEquals("duzentas e vinte e uma",
                    rules.convertToWords(BigInteger.valueOf(221), "feminine"));
        }

        // -----------------------------------------------------------------------
        // Scale counts must stay MASCULINE regardless of the outer gender.
        // In Portuguese "mil", "milhão", "bilhão" etc. are masculine words —
        // "duas mil" and "duzentas milhões" are grammatically wrong.
        // -----------------------------------------------------------------------

        @Test
        @DisplayName("2000 feminine → 'dois mil' (scale count stays masculine)")
        void twoThousandFeminineScaleCountMasculine() {
            assertEquals("dois mil",
                    rules.convertToWords(BigInteger.valueOf(2_000), "feminine"));
        }

        @Test
        @DisplayName("21000 feminine → 'vinte e um mil' (scale count stays masculine)")
        void twentyOneThousandFeminineScaleCountMasculine() {
            assertEquals("vinte e um mil",
                    rules.convertToWords(BigInteger.valueOf(21_000), "feminine"));
        }

        @Test
        @DisplayName("22000 feminine → 'vinte e dois mil' (scale count stays masculine)")
        void twentyTwoThousandFeminineScaleCountMasculine() {
            assertEquals("vinte e dois mil",
                    rules.convertToWords(BigInteger.valueOf(22_000), "feminine"));
        }

        @Test
        @DisplayName("2000000 feminine → 'dois milhões' (scale count stays masculine)")
        void twoMillionFeminineScaleCountMasculine() {
            assertEquals("dois milhões",
                    rules.convertToWords(BigInteger.valueOf(2_000_000), "feminine"));
        }

        @Test
        @DisplayName("200000000 feminine → 'duzentos milhões' (hundreds in scale count stay masculine)")
        void twoHundredMillionFeminineScaleCountMasculine() {
            assertEquals("duzentos milhões",
                    rules.convertToWords(BigInteger.valueOf(200_000_000), "feminine"));
        }

        @Test
        @DisplayName("2000000001 feminine → 'dois bilhões e uma' (scale masculine, remainder feminine)")
        void twoBillionAndOneFeminine() {
            assertEquals("dois bilhões e uma",
                    rules.convertToWords(new BigInteger("2000000001"), "feminine"));
        }

        @Test
        @DisplayName("1001 feminine — remainder 1 is feminine, thousand count stays masculine")
        void oneThousandAndOneFeminine() {
            assertEquals("mil e uma",
                    rules.convertToWords(BigInteger.valueOf(1_001), "feminine"));
        }

        @Test
        @DisplayName("2001 feminine — 'dois mil e uma' (scale masculine, remainder feminine)")
        void twoThousandAndOneFeminine() {
            assertEquals("dois mil e uma",
                    rules.convertToWords(BigInteger.valueOf(2_001), "feminine"));
        }
    }

    // -------------------------------------------------------------------------
    // getCurrencyJoiner
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getCurrencyJoiner")
    class CurrencyJoiner {

        @Test
        @DisplayName("exact million → ' de '")
        void exactMillion() {
            assertEquals(" de ", rules.getCurrencyJoiner(BigInteger.valueOf(1_000_000)));
        }

        @Test
        @DisplayName("exact two millions → ' de '")
        void exactTwoMillions() {
            assertEquals(" de ", rules.getCurrencyJoiner(BigInteger.valueOf(2_000_000)));
        }

        @Test
        @DisplayName("exact billion → ' de '")
        void exactBillion() {
            assertEquals(" de ", rules.getCurrencyJoiner(new BigInteger("1000000000")));
        }

        @Test
        @DisplayName("exact trillion → ' de '")
        void exactTrillion() {
            assertEquals(" de ", rules.getCurrencyJoiner(new BigInteger("1000000000000")));
        }

        @Test
        @DisplayName("million + 1 → ' '")
        void millionPlusOne() {
            assertEquals(" ", rules.getCurrencyJoiner(BigInteger.valueOf(1_000_001)));
        }

        @Test
        @DisplayName("1234 → ' '")
        void plain() {
            assertEquals(" ", rules.getCurrencyJoiner(BigInteger.valueOf(1_234)));
        }

        @Test
        @DisplayName("1 → ' '")
        void one() {
            assertEquals(" ", rules.getCurrencyJoiner(BigInteger.ONE));
        }

        @Test
        @DisplayName("1_200_000 (million + thousands) → ' '")
        void millionWithThousands() {
            assertEquals(" ", rules.getCurrencyJoiner(BigInteger.valueOf(1_200_000)));
        }
    }

    // -------------------------------------------------------------------------
    // Connector
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getConnector returns 'e'")
    void connector() {
        assertEquals("e", rules.getConnector());
    }

    // -------------------------------------------------------------------------
    // Unsupported language
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("unsupported language throws UnsupportedLanguageException")
    void unsupportedLanguage() {
        assertThrows(UnsupportedLanguageException.class,
                () -> new LanguageRules("en-US", new LanguageProvider()));
    }
}
