package data.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import statistic.Statistics;

public class DataHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Path statisticsPath =  Paths.get("data/statistics/");

    public static void saveStatistics() {
        HashMap<String, Integer> statistics = Statistics.getDailyStatistic();
        try {
            objectMapper.writeValue(new File(statisticsPath.toString() + "example.json"), statistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO: .json file names
    }
}
