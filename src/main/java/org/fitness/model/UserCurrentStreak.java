package org.fitness.model;

import lombok.Data;

@Data
public class UserCurrentStreak {

    private String name;
    private int currentStreak;
    private Period period = Period.WEEKS;
}

enum Period {
    DAYS,
    WEEKS,
    MONTHS
}
