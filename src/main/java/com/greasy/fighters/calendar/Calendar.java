package com.greasy.fighters.calendar;

import java.time.LocalDate;

public class Calendar {
    private LocalDate selectedDate;

    public Calendar() {
        selectedDate = LocalDate.now();
    }

    public void setSelectedDateYesterday() {
        selectedDate = calculateDate(selectedDate, -1);
    }

    public void setSelectedDateTomorrow() {
        selectedDate = calculateDate(selectedDate, 1);
    }

    public String getCurrentDate() {
        return formatDate(selectedDate);
    }

    private LocalDate calculateDate(LocalDate date, int daysToAdd) {
        return date.plusDays(daysToAdd);
    }

    private String formatDate(LocalDate date) {
        return date.getMonthValue() + "." + date.getDayOfMonth() + "." + date.getYear();
    }
}
