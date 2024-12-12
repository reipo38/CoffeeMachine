package com.greasy.fighters.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Calendar {
    
    public Calendar() {

    }

    private String formatDate(LocalDate date) {
        return date.getMonthValue() + "." + date.getDayOfMonth() + "." + date.getYear();
    }

    public String getDateMonthYear() {
        LocalDate now = LocalDate.now();
        return formatDate(now);
    }

    private LocalDate stringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        return localDate;
    }

    public String calculateDate(String date, int daysToAdd) {
        LocalDate localDate = stringToLocalDate(date);
        LocalDate resultDate = localDate.plusDays(daysToAdd);
        return formatDate(resultDate);
    }
    
    public String calculateTomorrow(String date) {
        return calculateDate(date, 1);
    }
    
    public String calculateYesterday(String date) {
        return calculateDate(date, -1);
    }
}
