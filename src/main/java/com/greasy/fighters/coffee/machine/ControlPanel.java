package com.greasy.fighters.coffee.machine;

import java.util.HashMap;
import java.util.Map;

import com.greasy.fighters.calendar.Calendar;
import com.greasy.fighters.data.handler.DataHandler;
import com.greasy.fighters.enums.Consumables;
import com.greasy.fighters.statistic.Statistics;

public class ControlPanel {
    /***
     * Клас ControlPanel. Отговаря за администраторската логика в кафемашината. Управлява се количествата на всички съставки,
     * общият брой пари, максималното количество захар, използваното количество мляко и т.н.
     * Полета:
     * moneySymbol - лесен начин за промяна на символа за парите (например "лв", "bgn")
     * coffeeMachine - кафемашината, която този контролен панел управлява
     * consumables - съдържа количеството пари и всички съставки, налични в кафемашината (кофе, вода, мляко, захар и т.н.)
     * milkNeeded - количеството мляко, което се използва за приготвянето на едно кафе с мляко
     * sugarMax - максималното количество захар, което може да бъде избрано от потребителя
     * sugarChangeBy - количество захар, с което избраното количество се променя. То винаги е една пета от максималното количество захар
     */
    private final String moneySymbol = "bgn"; // Символ за парите (може да бъде променен)

    private final CoffeeMachine coffeeMachine; // Инстанция на кафемашината, която управляваме
    private final Statistics statistics;
    private final DataHandler dataHandler;
    private final Calendar calendar;

    private final HashMap<String, Integer> consumables; // Речник със съставките в кафемашината

    // Речник, който пази количеството монети от всеки номинал.
    private final HashMap<String, Integer> coins;

    private int milkNeeded; // Количеството мляко, което е нужно за всяко кафе с мляко

    private int sugarChangeBy; // Количеството захар, с което се променя избраната захар
    private int sugarMax; // Максималното количество захар, което може да бъде избрано

    // Конструктор на ControlPanel, инициализиращ кафемашината и зареждащ съставките
    public ControlPanel(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
        this.dataHandler = new DataHandler(this);
        this.statistics = new Statistics(this);
        this.calendar = new Calendar();
        this.consumables = this.dataHandler.loadConsumables(); // Зареждаме съставките от външно хранилище (например JSON файл)
        this.coins = this.dataHandler.loadCoins();

        this.coffeeMachine.setCoffees(dataHandler.loadCoffees());

        statistics.loadDailyStatistic();
        updateTotalMoneyAmount();
    }

    // Метод за добавяне на ново кафе към кафемашината
    public void addNewCoffee(String coffeeType, int price, int needAmountOfCoffee, boolean hasMilk, int waterNeeded) {
        coffeeMachine.addNewCoffee(new Coffee(coffeeType, price, needAmountOfCoffee, hasMilk, waterNeeded));
    }

    // Метод за изтриване на кафе по име от кафемашината
    public void deleteCoffeeByName(String coffeeName) {
        Coffee coffee = coffeeMachine.getCoffeeByName(coffeeName);
        coffeeMachine.deleteCoffee(coffee);
    }

    // Метод за проверка дали всички нужни съставки за приготвянето на кафе са налични
    public boolean ingredientsAvailable(Coffee coffee) {
        return isEnoughConsumable(Consumables.COFFEE.toString(), coffee.getCoffeeNeeded()) &&
                (!coffee.hasMilk() || isEnoughConsumable(Consumables.MILK.toString(), milkNeeded)) &&
                isEnoughConsumable(Consumables.WATER.toString(), coffee.getWaterNeeded()) &&
                isEnoughConsumable(Consumables.SUGAR.toString(), coffeeMachine.getSugarSelected());
    }

    // Примерен метод, където всички промени се обработват в една точка
    public void updateInternalValues(Coffee coffee, HashMap<String, Integer> coins) {
        // Обновяваме съставките въз основа на кафето и захарта
        for (Consumables consumable : Consumables.values()) {
            if (consumable == Consumables.MONEY) {
                continue;  // Пропускаме парите тук
            }
            updateConsumable(consumable, coffee);
        }

        changeCoinsAmount(coins, true);
    }

    private boolean isEnoughConsumable(String consumableKey, int amountNeeded) {
        return consumables.getOrDefault(consumableKey, 0) >= amountNeeded;
    }
/*
    // Метод за промяна на количеството монети и актуализиране на парите в една централизирана точка
    private void changeCoinsAmount(HashMap<String, Integer> coins, boolean add) {
        for (Map.Entry<String, Integer> entry : coins.entrySet()) {
            int newAmount = this.coins.getOrDefault(entry.getKey(), 0) + entry.getValue() * (add ? 1 : -1);
            this.coins.put(entry.getKey(), newAmount);
        }
        // Актуализиране на стойността на парите след всички промени
        updateTotalMoneyAmount();
    }

    // Метод за актуализиране на стойността на парите в една централизирана точка
    private void updateTotalMoneyAmount() {
        int money = coins.entrySet().stream()
                .mapToInt(entry -> parseCoinAmount(entry.getKey()) * entry.getValue())
                .sum();
        consumables.put(Consumables.MONEY.toString(), money);
        dataHandler.saveConsumables(consumables);
    }
    */


    // Метод за промяна на стойността на съставка
    public void changePropertiesValue(String property, int amount) {
        if (property.matches("-?\\d+(\\.\\d+)?")) {
            coins.put(property, coins.get(property) + amount);
            dataHandler.saveCoins(coins);
            updateTotalMoneyAmount();
        } else {
            consumables.put(property, consumables.get(property) + amount);
            dataHandler.saveConsumables(consumables);
        }
    }

    private void updateTotalMoneyAmount() {
        int totalMoney = coins.entrySet().stream()
                .mapToInt(entry -> parseCoinAmount(entry.getKey()) * entry.getValue())
                .sum();
        consumables.put(Consumables.MONEY.toString(), totalMoney);
        dataHandler.saveConsumables(consumables);
    }

    private void changeCoinsAmount(HashMap<String, Integer> coins, boolean add) {
        for (Map.Entry<String, Integer> entry : coins.entrySet()) {
            int newAmount = this.coins.getOrDefault(entry.getKey(), 0) + entry.getValue() * (add ? 1 : -1);
            this.coins.put(entry.getKey(), newAmount);
        }
        updateTotalMoneyAmount();
    }

    private int parseCoinAmount(String coin) {
        if (coin.length() > 1) {
            return Integer.parseInt(coin.substring(2));
        } else {
            return Integer.parseInt(coin) + 100;
        }
    }

    private void updateConsumable(Consumables consumable, Coffee coffee) {
        int amountToDeduct = switch (consumable) {
            case COFFEE -> coffee.getCoffeeNeeded();
            case MILK -> coffee.hasMilk() ? milkNeeded : 0;
            case WATER -> coffee.getWaterNeeded();
            case SUGAR -> coffeeMachine.getSugarSelected();
            default -> 0;
        };

        consumables.put(consumable.toString(), consumables.get(consumable.toString()) - amountToDeduct);
    }

    // Метод за актуализиране на количествата монети
    public void dropCoins(HashMap<String, Integer> coins) {
        changeCoinsAmount(coins, false);
    }

    // Метод за получаване на символа за парите
    public String getMoneySymbol() {
        return moneySymbol;
    }

    // Метод за задаване на максималното количество захар
    public int getSugarMax() {
        return sugarMax;
    }

    // set метод за задаване на максималното количество захар и автоматично изчисляване на sugarChangeBy
    public void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
        sugarChangeBy = sugarMax / 5; // Захарта се променя с една пета от максималното количество
    }

    // Метод за получаване на количеството, с което се променя захарта
    public int getSugarChangeBy() {
        return sugarChangeBy;
    }

    // Метод за получаване на количеството мляко, необходимо за приготвяне на кафе с мляко
    public int getMilkNeeded() {
        return milkNeeded;
    }

    // Метод за задаване на количеството мляко, необходимо за приготвяне на кафе с мляко
    public void setMilkNeeded(int milkNeeded) {
        this.milkNeeded = milkNeeded;
    }

    // Метод за получаване на количеството на дадена съставка
    public int getConsumableAmount(String consumable) {
        return consumables.get(consumable); // Връщаме стойността на съставката от речника
    }

    public int getCoinAmount(String coinNominal) {
        return coins.get(coinNominal);
    }

    // Метод за получаване на монетите по номинали
    public HashMap<String, Integer> getCoins() {
        return coins;
    }

    // Метод за получаване на имената на наличните кафета
    public String[] getCoffeeNames() {
        return coffeeMachine.getCoffeeNames();
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void changeDateForStatistics(boolean increment) {
        calendar.setSelectedDate(increment ? calendar.calculateYesterday(calendar.getSelectedDate()) : calendar.calculateTomorrow(calendar.getSelectedDate()));

    }
}
