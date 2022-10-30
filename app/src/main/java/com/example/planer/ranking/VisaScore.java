package com.example.planer.ranking;

public enum VisaScore {
    VISA_FREE("visa free", 100, -1, -1),
    ON_ARRIVAL("visa on arrival", 95, -1, -1),
    LIMIT_181_360("visa free 181 to 360 days", 95, 181, 360),
    LIMIT_91_180("visa free 91 to 180 days", 85, 91, 180),
    LIMIT_7_90("visa free 7 to 90 days", 80, 7, 90),
    E_VISA("e-visa", 80, -1, -1),
    VISA_REQUIRED("visa required", 70, -1, -1),
    COVID_BAN("covid ban", 10, -1, -1),
    NO_ADMISSION("no admission", 0, -1, -1),
    SAME_COUNTRY("-1", 0, -1, -1);

    /**
     * Description for the visa requirement of a country.
     */
    public final String description;
    /**
     * Score for the visa requirement of a country.
     */
    public final int score;
    /**
     * The minimum value of maximum days of staying visa-free of a country.
     * Equals -1 if not applicable.
     */
    public final int lower_limit;
    /**
     * The maximum value of maximum days of staying visa-free of a country.
     * Equals -1 if not applicable.
     */
    public final int upper_limit;

    VisaScore(final String description, final int score, final int lower_limit, final int upper_limit) {
        this.description = description;
        this.score = score;
        this.lower_limit = lower_limit;
        this.upper_limit = upper_limit;
    }
}
