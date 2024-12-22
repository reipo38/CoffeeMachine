package com.greasy.fighters.enums;

import java.util.Arrays;

public enum Consumables {
    MONEY("Money"),
    COFFEE("Coffee"),
    SUGAR("Sugar"),
    MILK("Milk"),
    WATER("Water");

    private final String strValue;

    Consumables(String strValue) {
        this.strValue = strValue;
    }

    public static String[] stringValues() {
        return Arrays.stream(Consumables.values()) // Create a stream from enum values
                .map(Consumables::toString)   // Map each enum constant to its string value
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return strValue;
    }
}