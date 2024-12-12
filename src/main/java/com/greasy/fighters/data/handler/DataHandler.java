package com.greasy.fighters.data.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greasy.fighters.calendar.Calendar;
import com.greasy.fighters.coffee.machine.Coffee;
import com.greasy.fighters.coffee.machine.ControlPanel;

public class DataHandler {
    private Path consumablesPath = Paths.get("data/consumables.json");
    private Path coffeesPath = Paths.get("data/coffees.json");
    private Path passwordPath = Paths.get("data/password.txt");
    private Path moneyPath = Paths.get("data/money.json");

    private ObjectMapper objectMapper;
    private ControlPanel controlPanel;
    private Calendar calendar;

    public DataHandler(ControlPanel controlPanel) {
        objectMapper = new ObjectMapper();
        this.controlPanel = controlPanel;
        this.calendar = new Calendar();
    }

    public void saveStatistics() {
        Path statisticsPath = Paths.get("data/statistics/" + calendar.getCurrentDate() + ".json");
        HashMap<String, Integer> statistics = controlPanel.getStatistics().getDailyStatistic();
        try {
            objectMapper.writeValue(new File(statisticsPath.toString()), statistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Integer> loadConsumables() {
        return loadHashMapStringInteger(consumablesPath);
    }

    public HashMap<String, Integer> loadCoins() {
        return loadHashMapStringInteger(moneyPath);
    }

    public void saveConsumables(HashMap<String, Integer> consumables) {
        saveToJson(consumablesPath, consumables);
    }

    public ArrayList<Coffee> loadCoffees() {
        // File coffeeDirectory = new File("data/coffee/");
        // File[] coffeeFiles = coffeeDirectory.listFiles();

        ArrayList<Coffee> coffeeTypes;

        try {
            coffeeTypes = objectMapper.readValue(new File(coffeesPath.toString()), new TypeReference<>() {
            });
        } catch (IOException e ) {
            throw new RuntimeException(e);
        }

        return coffeeTypes;
    }

    public void saveCoffees(ArrayList<Coffee> coffees) {
        saveToJson(coffeesPath, coffees);
    }

    private void savePassword(String password) { // * ненужен метод, ама го добавих, ако евентуално добавим смяна на парола
        try {
            FileWriter fileWriter = new FileWriter(passwordPath.toString());
            fileWriter.write(password);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPassword() {
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

    public HashMap<String, Integer> loadStatistic() {
        String filename = calendar.getCurrentDate();
        return loadStatisticByFilename(filename);
    }

    // ! не знам как по друг начин да се направят тези два метода

    public HashMap<String, Integer> loadStatistic(LocalDate date) {
        String filename = controlPanel.getCalendar().formatDate(date);
        return loadStatisticByFilename(filename);
    }

    // * ChatGPT ми каза това
    private HashMap<String, Integer> loadStatisticByFilename(String filename) {
        Path filePath = Paths.get("data/statistics", filename + ".json");
        return loadHashMapStringInteger(filePath);
    }

    public HashMap<String, Integer> loadHashMapStringInteger(Path filePath) {
        String filePathString = filePath.toString();

        try {
            HashMap<String, Integer> hashMap = objectMapper.readValue(
                new File(filePathString), 
                new TypeReference<HashMap<String, Integer>>() {}
            );

            // System.out.println(hashMap.toString());

            return hashMap;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePathString);
            return new HashMap<String, Integer>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /*
    public static HashMap<String, Integer> loadMoney() {
        HashMap<String, Integer> originalHashMap = loadHashMapStringInteger(moneyPath); // * JSON е тъп и не може да се използва нещо различно от низ като ключ

        HashMap<Integer, Integer> moneyHashMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : originalHashMap.entrySet()) {
            /*
             * може би трябва да се премести в друг метод
            Integer newKey = entry.getKey().length();
            moneyHashMap.put(newKey, entry.getValue());
        }

        return moneyHashMap;
    }
    */

    private void saveToJson(Path path, Object data) {
        File file = new File(path.toString());

        try {
            objectMapper.writeValue(file, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCoins(HashMap<String, Integer> coins) {
        saveToJson(moneyPath, coins);
    }
}