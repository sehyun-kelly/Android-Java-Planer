package com.example.planer.ranking;

public enum RecommendationLevel {
    STRONGLY_RECOMMENDED(85),
    RECOMMENDED(70),
    URGENCY_ONLY(40),
    NOT_RECOMMENDED(0);

    public final int lower_bound;

    public static RecommendationLevel findRecommendationLevel(double score) {
        for (RecommendationLevel recommendationLevel : values()) {
            if (recommendationLevel.lower_bound <= score) {
                return recommendationLevel;
            }
        }
        return NOT_RECOMMENDED;
    }

    RecommendationLevel(int lower_bound) {
        this.lower_bound = lower_bound;
    }
}
