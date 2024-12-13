package com.greasy.fighters.statistic;

import java.util.HashMap;

import com.greasy.fighters.coffee.machine.Coffee;
import com.greasy.fighters.coffee.machine.ControlPanel;

public class Statistics {
    private HashMap<String, Integer> dailyStatistic = new HashMap<>();
    private ControlPanel controlPanel;

    public Statistics(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    public void loadDailyStatistic() {
        dailyStatistic = controlPanel.getDataHandler().loadStatistic();
    }

    public void addCoffeeToDailyStatistic(Coffee coffee) {
        dailyStatistic.put(coffee.getName(), dailyStatistic.getOrDefault(coffee.getName(), 0) + 1);
        controlPanel.getDataHandler().saveStatistics();
    }

    public HashMap<String, Integer> getDailyStatistic() {
        return dailyStatistic;
    }
}