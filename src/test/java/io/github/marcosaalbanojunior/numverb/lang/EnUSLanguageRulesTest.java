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

@DisplayName("LanguageRules — en-US")
class EnUSLanguageRulesTest {

    private LanguageRules rules;

    @BeforeEach
    void setUp() {
        rules = new LanguageRules("en-US", new LanguageProvider());
    }

    // -------------------------------------------------------------------------
    // Units and teens (0–19)
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "0,  zero",
        "1,  one",
        "2,  two",
        "3,  three",
        "4,  four",
        "5,  five",
        "6,  six",
        "7,  seven",
        "8,  eight",
        "9,  nine",
        "10, ten",
        "11, eleven",
        "12, twelve",
        "13, thirteen",
        "14, fourteen",
        "15, fifteen",
        "16, sixteen",
        "17, seventeen",
        "18, eighteen",
        "19, nineteen",
    })
    @DisplayName("units and teens 0–19")
    void unitsAndTeens(String input, String expected) {
        assertEquals(expected.trim(),
                rules.convertToWords(new BigInteger(input.trim()), "masculine"));
    }

    // -------------------------------------------------------------------------
    // Tens (20–99) — hyphen separator
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "20, twenty",
        "21, twenty-one",
        "22, twenty-two",
        "30, thirty",
        "40, forty",
        "50, fifty",
        "60, sixty",
        "70, seventy",
        "80, eighty",
        "90, ninety",
        "99, ninety-nine",
    })
    @DisplayName("tens: hyphen between tens and units")
    void tens(String input, String expected) {
        assertEquals(expected.trim(),
                rules.convertToWords(new BigInteger(input.trim()), "masculine"));
    }

    // -------------------------------------------------------------------------
    // Hundreds (100–999) — "and" after hundreds
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "100, one hundred",
        "101, one hundred and one",
        "110, one hundred and ten",
        "111, one hundred and eleven",
        "121, one hundred and twenty-one",
        "199, one hundred and ninety-nine",
        "200, two hundred",
        "201, two hundred and one",
        "300, three hundred",
        "400, four hundred",
        "500, five hundred",
        "600, six hundred",
        "700, seven hundred",
        "800, eight hundred",
        "900, nine hundred",
        "999, nine hundred and ninety-nine",
    })
    @DisplayName("hundreds: 'and' between hundreds and remainder")
    void hundreds(String input, String expected) {
        assertEquals(expected.trim(),
                rules.convertToWords(new BigInteger(input.trim()), "masculine"));
    }

    // -------------------------------------------------------------------------
    // Thousands — "one thousand" (NOT just "thousand")
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "1000,  one thousand",
        "1001,  one thousand and one",
        "1010,  one thousand and ten",
        "1100,  one thousand one hundred",
        "1101,  one thousand one hundred and one",
        "1121,  one thousand one hundred and twenty-one",
        "2000,  two thousand",
        "2001,  two thousand and one",
        "9999,  nine thousand nine hundred and ninety-nine",
        "10000, ten thousand",
        "21000, twenty-one thousand",
        "99000, ninety-nine thousand",
        "100000,  one hundred thousand",
        "101000,  one hundred and one thousand",
        "999999,  nine hundred and ninety-nine thousand nine hundred and ninety-nine",
    })
    @DisplayName("thousands: 'one thousand' (count 1 is NOT omitted)")
    void thousands(String input, String expected) {
        assertEquals(expected.trim(),
                rules.convertToWords(new BigInteger(input.trim()), "masculine"));
    }

    // -------------------------------------------------------------------------
    // Millions, billions and beyond
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} → \"{1}\"")
    @CsvSource({
        "1000000,    one million",
        "2000000,    two million",
        "1000001,    one million and one",
        "1001000,    one million and one thousand",
        "1100000,    one million one hundred thousand",
        "1200000,    one million two hundred thousand",
        "1000000000, one billion",
        "2000000000, two billion",
    })
    @DisplayName("millions and billions")
    void millionsAndBillions(String input, String expected) {
        assertEquals(expected.trim(),
                rules.convertToWords(new BigInteger(input.trim()), "masculine"));
    }

    @Test
    @DisplayName("large composite: 2_345_678_901")
    void compositeNumber() {
        assertEquals(
            "two billion three hundred and forty-five million"
            + " six hundred and seventy-eight thousand nine hundred and one",
            rules.convertToWords(new BigInteger("2345678901"), "masculine"));
    }

    @Test
    @DisplayName("one septillion — maximum supported scale")
    void septillion() {
        assertEquals("one septillion",
                rules.convertToWords(new BigInteger("1000000000000000000000000"), "masculine"));
    }

    // -------------------------------------------------------------------------
    // No grammatical gender in English — all forms are identical
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("feminine gender produces same output as masculine")
    void feminineEqualsMasculine() {
        BigInteger[] cases = {
            BigInteger.ONE, BigInteger.TWO,
            BigInteger.valueOf(21), BigInteger.valueOf(200),
            BigInteger.valueOf(2_000)
        };
        for (BigInteger n : cases) {
            assertEquals(
                    rules.convertToWords(n, "masculine"),
                    rules.convertToWords(n, "feminine"),
                    "Expected no gender difference for " + n);
        }
    }

    // -------------------------------------------------------------------------
    // getCurrencyJoiner — no preposition in English
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getCurrencyJoiner — always plain space")
    class CurrencyJoiner {

        @Test
        @DisplayName("exact million → plain space (no 'of')")
        void millionNoPreposition() {
            assertEquals(" ", rules.getCurrencyJoiner(BigInteger.valueOf(1_000_000)));
        }

        @Test
        @DisplayName("exact billion → plain space")
        void billionNoPreposition() {
            assertEquals(" ", rules.getCurrencyJoiner(new BigInteger("1000000000")));
        }

        @Test
        @DisplayName("1001 → plain space")
        void smallNumber() {
            assertEquals(" ", rules.getCurrencyJoiner(BigInteger.valueOf(1_001)));
        }
    }

    // -------------------------------------------------------------------------
    // getConnector
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getConnector returns 'and'")
    void connector() {
        assertEquals("and", rules.getConnector());
    }

    // -------------------------------------------------------------------------
    // Error handling
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("negative number throws IllegalArgumentException")
    void negativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> rules.convertToWords(BigInteger.valueOf(-1), "masculine"));
    }

    @Test
    @DisplayName("unsupported language throws UnsupportedLanguageException")
    void unsupportedLanguage() {
        assertThrows(UnsupportedLanguageException.class,
                () -> new LanguageRules("xx-XX", new LanguageProvider()));
    }
}
