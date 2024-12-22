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

    public void setSelectedDateYesterday() {
        selectedDate = calculateDate(selectedDate, -1);
    }

    public void setSelectedDateTomorrow() {
        selectedDate = calculateDate(selectedDate, 1);
    }

    public String getCurrentDate() {
        LocalDate now = LocalDate.now();
        return formatDate(now);
    }

    private LocalDate calculateDate(LocalDate date, int daysToAdd) {
        return date.plusDays(daysToAdd);
    }
}
