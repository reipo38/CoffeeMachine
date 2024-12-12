package com.greasy.fighters;

import java.util.Arrays;

public class Enums {
    public enum Nominals {
        TWO("2", 200),
        ONE("1", 100),
        FIFTY_CENTS("0.50", 50),
        TWENTY_CENTS("0.20", 20),
        TEN_CENTS("0.10", 10),
        FIVE_CENTS("0.05", 5);

        private final String strValue;
        private final int intValue;

        Nominals(String strValue, int intValue) {
            this.strValue = strValue;
            this.intValue = intValue;
        }

        public int toInt() {
            return intValue;
        }

        public static String[] stringValues() {
            return Arrays.stream(Nominals.values()) // Create a stream from enum values
                    .map(Nominals::toString)   // Map each enum constant to its string value
                    .toArray(String[]::new);
        }

        @Override
        public String toString() {
            return strValue;
        }
    }

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
}
