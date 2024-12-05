package data.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import statistic.Statistics;

public class DataHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Path statisticsPath =  Paths.get("data/statistics/");

    public static void saveStatistics() throws StreamWriteException, DatabindException, IOException {
        HashMap<String, Integer> statistics = Statistics.getDailyStatistic();
        objectMapper.writeValue(new File(statisticsPath.toString() + "example.json"), statistics);
        // TODO: .json file names
    }
}
