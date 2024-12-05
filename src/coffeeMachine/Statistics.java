package coffeeMachine;
import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Statistics {
    private HashMap<String, Integer> dailyStatistic;
    private  Date date;

    public Statistics(Date date) {
        this.date = date;
    }

    protected String getDateMonth(){
        return date.getDate() + " : " + (date.getMonth()+1);
    }

    protected void addCoffeesToStatistic(ArrayList<Coffee> coffees){
        for (Coffee coffee : coffees){
            dailyStatistic.put(coffee.getName(),0);
        }
    }

    protected void addToDailyStatistic(Coffee coffee){
        if (dailyStatistic.containsKey(coffee.getName()))dailyStatistic.replace(coffee.getName(), dailyStatistic.get(coffee.getName())+1);
    }


}
