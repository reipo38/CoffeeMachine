package data.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import coffee.machine.Coffee;
import statistic.Statistics;

public class DataHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Path consumablesPath = Paths.get("data/consumables.json");
    private static final Path coffeesPath = Paths.get("data/coffees.json");

    public static void saveStatistics() {
        Path statisticsPath = Paths.get("data/statistics/" + getDateMonthYear() + ".json");
        HashMap<String, Integer> statistics = Statistics.getDailyStatistic();
        try {
            objectMapper.writeValue(new File(statisticsPath.toString()), statistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String,Object> loadConsumables() {
        try {
            HashMap<String, Object> consumablesHashMap = objectMapper.readValue(new File(consumablesPath.toString()), HashMap.class);
            return consumablesHashMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDateMonthYear() {
        LocalDateTime now = LocalDateTime.now();
        return now.getMonthValue() + "." + now.getDayOfMonth() + "." + now.getYear();
    }

    public static ArrayList<Coffee> loadCoffeeTypes() {
        // File coffeeDirectory = new File("data/coffee/");
        // File[] coffeeFiles = coffeeDirectory.listFiles();

        ArrayList<Coffee> coffeeTypes;

        try {
            coffeeTypes = objectMapper.readValue(new File(coffeesPath.toString()), new TypeReference<ArrayList<Coffee>>() {});
        } catch (IOException e ) {
            throw new RuntimeException(e);
        }

        return coffeeTypes;
    }

    // private static String loadFile(String path) {
    //     try {
    //         File file = new File(path);
    //         FileReader fr = new FileReader(file);
    //         BufferedReader readFile = new BufferedReader(fr);

    //         return readFile.toString();

    //     } catch (FileNotFoundException e) {
    //         throw new RuntimeException(e);
    //     }

    // }

}