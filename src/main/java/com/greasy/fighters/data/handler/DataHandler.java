package com.greasy.fighters.data.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greasy.fighters.calendar.Calendar;
import com.greasy.fighters.coffee.machine.Coffee;

public class DataHandler {
    private final Path consumablesPath = Paths.get("data/consumables.json");
    private final Path coffeesPath = Paths.get("data/coffees.json");
    private final Path passwordPath = Paths.get("data/password.txt");
    private final Path moneyPath = Paths.get("data/money.json");

    private final ObjectMapper objectMapper;
    private final Calendar calendar;

    public DataHandler() {
        objectMapper = new ObjectMapper();
        calendar = new Calendar();
    }

    public void saveStatistics(HashMap<String, Integer> statistics) {
        Path statisticsPath = Paths.get("data/statistics/" + calendar.getCurrentDate() + ".json");
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
        ArrayList<Coffee> coffeeTypes;
        try {
            coffeeTypes = objectMapper.readValue(new File(coffeesPath.toString()), new TypeReference<>() {
            });
        } catch (IOException e) {
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

    public HashMap<String, Integer> loadStatistic(String date) {
        return loadStatisticByFilename(date);
    }

    // * ChatGPT ми каза това
    private HashMap<String, Integer> loadStatisticByFilename(String filename) {
        Path filePath = Paths.get("data/statistics", filename + ".json");
        return loadHashMapStringInteger(filePath);
    }

    public HashMap<String, Integer> loadHashMapStringInteger(Path filePath) {
        String filePathString = filePath.toString();
        try {
            return objectMapper.readValue(
                    new File(filePathString),
                    new TypeReference<>() {
                    }
            );
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToJson(Path path, Object data) {
        File file = new File(path.toString());

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data); // * за да се показва хубаво
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCoins(HashMap<String, Integer> coins) {
        saveToJson(moneyPath, coins);
    }
}