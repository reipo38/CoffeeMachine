package com.greasy.fighters.data.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greasy.fighters.coffee.machine.Coffee;
import com.greasy.fighters.statistic.Statistics;

public class DataHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Path consumablesPath = Paths.get("data/consumables.json");
    private static final Path coffeesPath = Paths.get("data/coffees.json");
    private static final Path passwordPath = Paths.get("data/password.txt");
    private static final Path moneyPath = Paths.get("data/money.json");

    public static void saveStatistics() {
        Path statisticsPath = Paths.get("data/statistics/" + getDateMonthYear() + ".json");
        HashMap<String, Integer> statistics = Statistics.getDailyStatistic();
        try {
            objectMapper.writeValue(new File(statisticsPath.toString()), statistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, Integer> loadConsumables() {
        return loadHashMapStringInteger(consumablesPath);
    }

    public static HashMap<Integer, Integer> loadMoney() {
        HashMap<String, Integer> originalHashMap = loadHashMapStringInteger(moneyPath); // * JSON е тъп и не може да се използва нещо различно от низ като ключ

        HashMap<Integer, Integer> moneyHashMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : originalHashMap.entrySet()) {
            /*
             * може би трябва да се премести в друг метод
             */
            Integer newKey = entry.getKey().length();
            moneyHashMap.put(newKey, entry.getValue());
        }

        return moneyHashMap;
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

    private static void savePassword(String password) { // * ненужен метод, ама го добавих, ако евентуално добавим смяна на парола
        try {
            FileWriter fileWriter = new FileWriter(passwordPath.toString());
            fileWriter.write(password);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPassword() {
        File passwordFile = new File(passwordPath.toString());
        String password;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(passwordFile));
            password = reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return password;
    }

    public static HashMap<String, Integer> loadStatistic() {
        String filename = getDateMonthYear();
        return loadStatisticByFilename(filename);
    }

    // ! не знам как по друг начин да се направят тези два метода

    public static HashMap<String, Integer> loadStatistic(String filename) {
        return loadStatisticByFilename(filename);
    }

    // * ChatGPT ми каза това
    private static HashMap<String, Integer> loadStatisticByFilename(String filename) {
        Path filePath = Paths.get("data/statistics", filename + ".json");
        return loadHashMapStringInteger(filePath);
    }

    public static HashMap<String, Integer> loadHashMapStringInteger(Path filePath) {
        String filePathString = filePath.toString();

        try {
            HashMap<String, Integer> hashMap = objectMapper.readValue(
                new File(filePathString), 
                new TypeReference<HashMap<String, Integer>>() {}
            );

            System.out.println(hashMap.toString());

            return hashMap;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePathString);
            return new HashMap<String, Integer>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCoffees(ArrayList<Coffee> coffees) {
        File file = new File(coffeesPath.toString());

        try {
            objectMapper.writeValue(file, coffees);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}