package com.greasy.fighters.calendar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Calendar {
    
    public Calendar() {

    }

    private String formatDate(LocalDateTime date) {
        return date.getMonthValue() + "." + date.getDayOfMonth() + "." + date.getYear();
    }

    public String getDateMonthYear() {
        LocalDateTime now = LocalDateTime.now();
        return formatDate(now);
    }

    private LocalDateTime stringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);

        return localDateTime;
    }

    public String calculateTomorrow(String date) {
        LocalDateTime localDateTime = stringToLocalDateTime(date);

        return formatDate(localDateTime);
    }
}
