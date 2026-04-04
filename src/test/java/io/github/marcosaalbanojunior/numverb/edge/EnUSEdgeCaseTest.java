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

@DisplayName("Edge cases — en-US end-to-end API")
class EnUSEdgeCaseTest {

    // -------------------------------------------------------------------------
    // Cardinals via public API
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Cardinal — en-US")
    class Cardinals {

        @Test
        @DisplayName("zero")
        void zero() {
            assertEquals("zero", NumVerb.cardinal(0).language(Language.EN_US).toWords());
        }

        @Test
        @DisplayName("basic: 1234 → 'one thousand two hundred and thirty-four'")
        void basic() {
            assertEquals("one thousand two hundred and thirty-four",
                    NumVerb.cardinal(1234).language(Language.EN_US).toWords());
        }

        @Test
        @DisplayName("same result with language string and Language object")
        void languageObjectEqualsString() {
            assertEquals(
                    NumVerb.cardinal(1234).language("en-US").toWords(),
                    NumVerb.cardinal(1234).language(Language.EN_US).toWords());
        }

        @Test
        @DisplayName("BigDecimal overload — fractional part ignored")
        void bigDecimalFractionalIgnored() {
            assertEquals("two",
                    NumVerb.cardinal(new BigDecimal("2.99")).language(Language.EN_US).toWords());
        }

        @Test
        @DisplayName("BigInteger overload — quintillion")
        void bigIntegerQuintillion() {
            assertEquals("one quintillion",
                    NumVerb.cardinal(new BigInteger("1000000000000000000"))
                            .language(Language.EN_US).toWords());
        }

        @Test
        @DisplayName("septillion — maximum supported scale")
        void septillion() {
            assertEquals("one septillion",
                    NumVerb.cardinal(new BigDecimal("1000000000000000000000000"))
                            .language(Language.EN_US).toWords());
        }

        @Test
        @DisplayName("negative cardinal throws NumberOutOfRangeException")
        void negativeThrows() {
            assertThrows(NumberOutOfRangeException.class,
                    () -> NumVerb.cardinal(-1).language(Language.EN_US).toWords());
        }
    }

    // -------------------------------------------------------------------------
    // Currency via public API
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Currency — en-US")
    class CurrencyApi {

        @Test
        @DisplayName("zero dollars")
        void zeroDollars() {
            assertEquals("zero dollars",
                    NumVerb.currency("0.00").language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("one dollar")
        void oneDollar() {
            assertEquals("one dollar",
                    NumVerb.currency("1.00").language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("one cent")
        void oneCent() {
            assertEquals("one cent",
                    NumVerb.currency("0.01").language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("fifty cents")
        void fiftyCents() {
            assertEquals("fifty cents",
                    NumVerb.currency("0.50").language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("one thousand two hundred and thirty-four dollars and sixty-eight cents")
        void fullAmount() {
            assertEquals(
                "one thousand two hundred and thirty-four dollars and sixty-eight cents",
                NumVerb.currency("1234.68").language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("one million dollars — no 'of' preposition")
        void oneMillion() {
            assertEquals("one million dollars",
                    NumVerb.currency("1000000.00").language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("one euro — EUR via en-US")
        void oneEuro() {
            assertEquals("one euro",
                    NumVerb.currency("1.00").language(Language.EN_US).currency(Currencies.EUR).toWords());
        }

        @Test
        @DisplayName("one pound — GBP via en-US")
        void onePound() {
            assertEquals("one pound",
                    NumVerb.currency("1.00").language(Language.EN_US).currency(Currencies.GBP).toWords());
        }

        @Test
        @DisplayName("one pound and two pence — GBP subunit 'pence'")
        void onePoundTwoPence() {
            assertEquals("one pound and two pence",
                    NumVerb.currency("1.02").language(Language.EN_US).currency(Currencies.GBP).toWords());
        }

        @Test
        @DisplayName("BRL in en-US — 'one real'")
        void brlEnUS() {
            assertEquals("one real",
                    NumVerb.currency("1.00").language(Language.EN_US).currency(Currencies.BRL).toWords());
        }

        @Test
        @DisplayName("BRL in en-US plural — 'two reais'")
        void brlEnUSPlural() {
            assertEquals("two reais",
                    NumVerb.currency("2.00").language(Language.EN_US).currency(Currencies.BRL).toWords());
        }

        @Test
        @DisplayName("BRL in en-US with centavos — 'one real and fifty centavos'")
        void brlEnUSWithCentavos() {
            assertEquals("one real and fifty centavos",
                    NumVerb.currency("1.50").language(Language.EN_US).currency(Currencies.BRL).toWords());
        }
    }

    // -------------------------------------------------------------------------
    // Null / invalid inputs
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Null and invalid inputs")
    class NullAndInvalid {

        @Test
        @DisplayName("null BigDecimal to currency() throws NullPointerException")
        void nullBigDecimal() {
            assertThrows(NullPointerException.class,
                    () -> NumVerb.currency((BigDecimal) null));
        }

        @Test
        @DisplayName("null String to currency() throws NullPointerException")
        void nullString() {
            assertThrows(NullPointerException.class,
                    () -> NumVerb.currency((String) null));
        }

        @Test
        @DisplayName("invalid decimal string throws NumberFormatException")
        void invalidString() {
            assertThrows(NumberFormatException.class,
                    () -> NumVerb.currency("not-a-number")
                            .language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("null Language throws NullPointerException")
        void nullLanguage() {
            assertThrows(NullPointerException.class,
                    () -> NumVerb.currency("1.00").language((Language) null));
        }

        @Test
        @DisplayName("null BigDecimal to cardinal() throws NullPointerException")
        void nullCardinalBigDecimal() {
            assertThrows(NullPointerException.class,
                    () -> NumVerb.cardinal((BigDecimal) null));
        }
    }

    // -------------------------------------------------------------------------
    // Boundary values
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Boundary values")
    class Boundaries {

        @Test
        @DisplayName("negative currency throws NumberOutOfRangeException")
        void negativeCurrencyThrows() {
            assertThrows(NumberOutOfRangeException.class,
                    () -> NumVerb.currency("-1.00")
                            .language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("value exceeding max range throws NumberOutOfRangeException")
        void overRangeThrows() {
            assertThrows(NumberOutOfRangeException.class,
                    () -> NumVerb.currency("1000000000000000000000000000.00")
                            .language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("maximum supported value does not throw")
        void maxValueDoesNotThrow() {
            assertDoesNotThrow(() ->
                    NumVerb.currency("999999999999999999999999.99")
                            .language(Language.EN_US).currency(Currencies.USD).toWords());
        }

        @Test
        @DisplayName("cardinal exceeding max range throws NumberOutOfRangeException")
        void cardinalOverRangeThrows() {
            assertThrows(NumberOutOfRangeException.class,
                    () -> NumVerb.cardinal(new BigDecimal("1000000000000000000000000000"))
                            .language(Language.EN_US).toWords());
        }

        @Test
        @DisplayName("maximum supported cardinal does not throw")
        void maxCardinalDoesNotThrow() {
            assertDoesNotThrow(() ->
                    NumVerb.cardinal(new BigDecimal("999999999999999999999999"))
                            .language(Language.EN_US).toWords());
        }
    }

    // -------------------------------------------------------------------------
    // Unsupported language / currency
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Unsupported language and currency")
    class UnsupportedCombinations {

        @Test
        @DisplayName("unsupported language throws UnsupportedLanguageException")
        void unsupportedLanguage() {
            assertThrows(UnsupportedLanguageException.class,
                    () -> NumVerb.currency("1.00").language("xx-XX").toWords());
        }

        @Test
        @DisplayName("unknown currency throws UnsupportedCurrencyException")
        void unknownCurrency() {
            assertThrows(UnsupportedCurrencyException.class,
                    () -> NumVerb.currency("1.00")
                            .language(Language.EN_US).currency("JPY").toWords());
        }

        @Test
        @DisplayName("exception message for unknown currency includes both codes")
        void exceptionMessageContainsBothCodes() {
            UnsupportedCurrencyException ex = assertThrows(UnsupportedCurrencyException.class,
                    () -> NumVerb.currency("1.00").language(Language.EN_US).currency("JPY").toWords());
            assertTrue(ex.getMessage().contains("JPY"));
            assertTrue(ex.getMessage().contains("en-US"));
        }

        @Test
        @DisplayName("cardinal with unsupported language throws UnsupportedLanguageException")
        void cardinalUnsupportedLanguage() {
            assertThrows(UnsupportedLanguageException.class,
                    () -> NumVerb.cardinal(1).language("xx-XX").toWords());
        }

        @Test
        @DisplayName("UnsupportedLanguageException takes precedence over NumberOutOfRangeException")
        void languageValidationBeforeNegativeCheck() {
            // Even with an invalid value, language is checked first.
            assertThrows(UnsupportedLanguageException.class,
                    () -> NumVerb.cardinal(-1).language("xx-XX").toWords());
        }
    }

    // -------------------------------------------------------------------------
    // pt-BR still works (regression)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("pt-BR regression — existing behaviour unchanged")
    class PtBrRegression {

        @Test
        @DisplayName("default (pt-BR + BRL) still works")
        void defaultPtBr() {
            assertEquals(
                "mil duzentos e trinta e quatro reais e sessenta e oito centavos",
                NumVerb.currency(new BigDecimal("1234.68")).toWords());
        }

        @Test
        @DisplayName("'mil' has no 'um' prefix in pt-BR")
        void milNoUmPrefix() {
            assertEquals("mil", NumVerb.cardinal(1000).toWords());
        }

        @Test
        @DisplayName("'um milhão de reais' still uses 'de' preposition")
        void umMilhaoDeReais() {
            assertEquals("um milhão de reais",
                    NumVerb.currency("1000000.00").toWords());
        }

        @Test
        @DisplayName("pt-BR cardinal: 'dois bilhões' not 'dois bilhão'")
        void ptBrPluralScale() {
            assertEquals("dois bilhões",
                    NumVerb.cardinal(2_000_000_000L).toWords());
        }
    }
}
