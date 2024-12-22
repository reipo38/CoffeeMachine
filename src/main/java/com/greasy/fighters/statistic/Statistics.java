package com.greasy.fighters.statistic;

import java.util.HashMap;

import com.greasy.fighters.coffee.machine.Coffee;

public class Statistics {
    private HashMap<String, Integer> dailyStatistic = new HashMap<>();

    public void setDailyStatistic(HashMap<String, Integer> dailyStatistic) {
        this.dailyStatistic = dailyStatistic;
    }

    public void addCoffeeToDailyStatistic(Coffee coffee) {
        dailyStatistic.put(coffee.getName(), dailyStatistic.getOrDefault(coffee.getName(), 0) + 1);
    }

    public HashMap<String, Integer> getDailyStatistic() {
        return dailyStatistic;
    }
}