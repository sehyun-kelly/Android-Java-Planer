package com.example.planer.ranking;

public enum CovidRestriction {
    NORMAL("Take normal security precautions", 100),
    CAUTION("Exercise a high degree of caution", 60),
    AVOIDED("Avoid non-essential travel", 30),
    NO_TRAVEL("Avoid all travel", 0);

    /**
     * Description for the covid restriction of a country.
     */
    public final String description;
    /**
     * Score for the covid restriction of a country.
     */
    public final int score;

    public static int findScoreByDescription(String descriptionGiven) {
        for (CovidRestriction covidRestriction : values()) {
            if (descriptionGiven.contains(covidRestriction.description)) {
                return covidRestriction.score;
            }
        }
        return 0;
    }

    CovidRestriction(final String description, final int score) {
        this.description = description;
        this.score = score;
    }
}
