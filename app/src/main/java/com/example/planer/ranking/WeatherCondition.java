package com.example.planer.ranking;

import com.google.android.gms.common.util.ArrayUtils;

public enum WeatherCondition {
    THUNDERSTORM(new int[]{}, 10, -1),      //2xx
    DRIZZLE(new int[]{300, 301}, 70, 80),   //3xx
    RAIN(new int[]{500, 501}, 70, 80),      //5xx
    SNOW(new int[]{600}, 60, 70),           //6xx
    ATMOSPHERE(new int[]{741}, 30, 70),     //7xx
    CLOUD(new int[]{800}, 90, 100);         //8xx

    public final int[] exceptions;
    public final int score;
    public final int exceptionScore;

    public static int findScoreByWeatherCode(int code) {
        if (200 <= code && code < 300) {
            return THUNDERSTORM.score;
        } else if (300 <= code && code < 400) {
            return (ArrayUtils.contains(DRIZZLE.exceptions, code) ? DRIZZLE.exceptionScore : DRIZZLE.score);
        } else if (500 <= code && code < 600) {
            return (ArrayUtils.contains(RAIN.exceptions, code) ? RAIN.exceptionScore : RAIN.score);
        } else if (600 <= code && code < 700) {
            return (ArrayUtils.contains(SNOW.exceptions, code) ? SNOW.exceptionScore : DRIZZLE.score);
        } else if (700 <= code && code < 800) {
            return (ArrayUtils.contains(ATMOSPHERE.exceptions, code) ? ATMOSPHERE.exceptionScore : ATMOSPHERE.score);
        } else if (800 <= code && code < 900) {
            return (ArrayUtils.contains(CLOUD.exceptions, code) ? CLOUD.exceptionScore : CLOUD.score);
        }
        return 0;
    }

    WeatherCondition(int[] exceptions, int score, int exceptionScore) {
        this.exceptions = exceptions;
        this.score = score;
        this.exceptionScore = exceptionScore;
    }
}
