package statistic; // По-добре да е в отделен пакет
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import coffee.machine.Coffee;
import data.handler.DataHandler;


public class Statistics {
    /*
     * По-добре класът да е статичен, защото не е нужно да има обект, неговите методи са помощни
     */

    private static HashMap<String, Integer> dailyStatistic = new HashMap<>();

    // * Всяко кафе ще се добавя, ако е поръчано
    // * Така се пълни с излишни работи
    // public static void addCoffeesToStatistic(ArrayList<Coffee> coffees) {
    //     for (Coffee coffee : coffees){
    //         dailyStatistic.put(coffee.getName(),0);
    //     }
    // }

    public static void loadDailyStatistic() {
        dailyStatistic = DataHandler.loadStatistic();
    }

    public static void addCoffeeToDailyStatistic(Coffee coffee) {
        dailyStatistic.put(coffee.getName(), dailyStatistic.getOrDefault(coffee.getName(), 0) + 1);
        DataHandler.saveStatistics();
    }

    public static HashMap<String, Integer> getDailyStatistic() {
        return dailyStatistic;
    }
}
