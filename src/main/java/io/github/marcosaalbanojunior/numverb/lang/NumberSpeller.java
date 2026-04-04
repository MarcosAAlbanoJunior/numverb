package io.github.marcosaalbanojunior.numverb.lang;

/**
 * Converts integers in the range [0, 999] to their word representation.
 *
 * <p>All vocabulary and grammar decisions (connectors, separators, gender forms)
 * are driven by the supplied {@link GrammarConfig} — this class contains no
 * language-specific logic.</p>
 *
 * <p>Package-private; used only by {@link LanguageRules}.</p>
 */
final class NumberSpeller {

    private final GrammarConfig cfg;

    NumberSpeller(GrammarConfig cfg) {
        this.cfg = cfg;
    }

    /**
     * Converts {@code n} in [0, 999] to words, returning an empty string for 0.
     * Used when building larger numbers where 0 contributes no words.
     *
     * @param n      the number; must be in [0, 999]
     * @param gender grammatical gender ({@code "masculine"} or {@code "feminine"})
     * @return the word representation, or {@code ""} if {@code n} is 0
     * @throws IllegalArgumentException if {@code n} is outside [0, 999]
     */
    String spellNonZero(long n, String gender) {
        if (n < 0 || n > 999) {
            throw new IllegalArgumentException("n must be in [0, 999]: " + n);
        }
        if (n == 0) return "";
        return doSpell(n, gender);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String doSpell(long n, String gender) {
        if (n < 20)  return unitWord((int) n, gender);
        if (n < 100) return spellTens(n, gender);
        return spellHundreds(n, gender);
    }

    private String spellTens(long n, String gender) {
        long tens  = (n / 10) * 10;
        long units = n % 10;
        String tensWord = cfg.vocab("ten." + tens);
        if (units == 0) return tensWord;
        return tensWord + cfg.tensSeparator + unitWord((int) units, gender);
    }

    private String spellHundreds(long n, String gender) {
        long hundreds  = (n / 100) * 100;
        long remainder = n % 100;

        String hundredsWord;
        if (n == 100) {
            // Exact 100 — some languages have a distinct form (e.g. "cem" in pt-BR).
            hundredsWord = cfg.vocab("hundred.100");
        } else if (hundreds == 100) {
            // 101–199 — combined form (e.g. "cento" in pt-BR, "one hundred" in en-US).
            hundredsWord = cfg.vocab("hundred.100.combined");
        } else {
            String femKey = "hundred." + hundreds + ".feminine";
            hundredsWord = ("feminine".equalsIgnoreCase(gender) && cfg.hasVocab(femKey))
                    ? cfg.vocab(femKey)
                    : cfg.vocab("hundred." + hundreds);
        }

        if (remainder == 0) return hundredsWord;
        return hundredsWord + " " + cfg.connector + " " + doSpell(remainder, gender);
    }

    private String unitWord(int n, String gender) {
        String femKey = "unit." + n + ".feminine";
        if ("feminine".equalsIgnoreCase(gender) && cfg.hasVocab(femKey)) {
            return cfg.vocab(femKey);
        }
        return cfg.vocab("unit." + n);
    }
}
