package com.greasy.fighters.statistic;
import java.util.HashMap;

import com.greasy.fighters.coffee.machine.Coffee;
import com.greasy.fighters.coffee.machine.ControlPanel;


public class Statistics {
    /*
     * По-добре класът да е статичен, защото не е нужно да има обект, неговите методи са помощни
     */

    private HashMap<String, Integer> dailyStatistic = new HashMap<>();
    private ControlPanel controlPanel;

    // * Всяко кафе ще се добавя, ако е поръчано
    // * Така се пълни с излишни работи
    // public static void addCoffeesToStatistic(ArrayList<Coffee> coffees) {
    //     for (Coffee coffee : coffees){
    //         dailyStatistic.put(coffee.getName(),0);
    //     }
    // }

    public Statistics(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        
        loadDailyStatistic();
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
