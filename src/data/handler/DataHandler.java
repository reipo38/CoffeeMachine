package data.handler;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;

import coffee.machine.Coffee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import statistic.Statistics;

import static com.sun.management.HotSpotDiagnosticMXBean.ThreadDumpFormat.JSON;

public class DataHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Path consumablesPath = Paths.get("data/consumables.json");

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