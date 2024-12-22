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

    public DataHandler(Calendar calendar) {
        objectMapper = new ObjectMapper();
        this.calendar = calendar;
    }

    public ArrayList<Coffee> loadCoffees() {
        try {
            return objectMapper.readValue(coffeesPath.toFile(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load coffee types from path: " + coffeesPath, e);
        }
    }

    public void saveStatistics(HashMap<String, Integer> statistics) {
        Path statisticsPath = getStatisticsPath(calendar.getCurrentDate());
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

    public String getPassword() {
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordPath.toFile()))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error reading password file: " + passwordPath, e);
        }
    }

    public HashMap<String, Integer> loadStatistic() {
        return loadHashMapStringInteger(getStatisticsPath(calendar.getCurrentDate()));
    }

    private Path getStatisticsPath(String date) {
        return Paths.get("data/statistics/" + date + ".json");
    }

    private HashMap<String, Integer> loadHashMapStringInteger(Path filePath) {
        try {
            return objectMapper.readValue(filePath.toFile(), new TypeReference<>() {
            });
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
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

    public void saveConsumables(HashMap<String, Integer> consumables) {
        saveToJson(consumablesPath, consumables);
    }

    public void saveCoffees(ArrayList<Coffee> coffees) {
        saveToJson(coffeesPath, coffees);
    }

    public void saveCoins(HashMap<String, Integer> coins) {
        saveToJson(moneyPath, coins);
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
}