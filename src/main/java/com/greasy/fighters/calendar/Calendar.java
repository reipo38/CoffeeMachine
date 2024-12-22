package com.greasy.fighters.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Calendar {
    private LocalDate selectedDate;
    
    public Calendar() {
        selectedDate = LocalDate.now();
    }

    public String formatDate(LocalDate date) {
        return date.getMonthValue() + "." + date.getDayOfMonth() + "." + date.getYear();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getCurrentDate() {
        LocalDate now = LocalDate.now();
        return formatDate(now);
    }

    private LocalDate stringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        return localDate;
    }

    public LocalDate calculateDate(LocalDate date, int daysToAdd) {
        return date.plusDays(daysToAdd);
    }
    
    public LocalDate calculateTomorrow(LocalDate date) {
        return calculateDate(date, 1);
    }
    
    public LocalDate calculateYesterday(LocalDate date) {
        return calculateDate(date, -1);
    }
}
