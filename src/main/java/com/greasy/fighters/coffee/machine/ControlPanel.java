package com.greasy.fighters.coffee.machine;

import java.util.HashMap;
import java.util.Map;

import com.greasy.fighters.calendar.Calendar;
import com.greasy.fighters.data.handler.DataHandler;
import com.greasy.fighters.enums.Consumables;
import com.greasy.fighters.enums.Nominals;
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
    private final Calendar calendar;
    private final DataHandler dataHandler;

    private final HashMap<String, Integer> consumables; // Речник със съставките в кафемашината

    // Речник, който пази количеството монети от всеки номинал.
    private final HashMap<String, Integer> coins;

    private int milkNeeded; // Количеството мляко, което е нужно за всяко кафе с мляко

    private int sugarChangeBy; // Количеството захар, с което се променя избраната захар
    private int sugarMax; // Максималното количество захар, което може да бъде избрано

    // Конструктор на ControlPanel, инициализиращ кафемашината и зареждащ съставките
    public ControlPanel(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
        this.coffeeMachine.setControlPanel(this);

        calendar = new Calendar();
        dataHandler = new DataHandler(calendar);
        statistics = new Statistics();
        consumables = dataHandler.loadConsumables(); // Зареждаме съставките от външно хранилище (например JSON файл)
        coins = dataHandler.loadCoins();

        this.coffeeMachine.setCoffees(dataHandler.loadCoffees());
        statistics.setDailyStatistic(dataHandler.loadStatistic());

        updateTotalMoneyAmount();
    }

    // Метод за добавяне на ново кафе към кафемашината
    public void addNewCoffee(String coffeeType, int price, int needAmountOfCoffee, boolean hasMilk, int waterNeeded) {
        coffeeMachine.addCoffee(new Coffee(coffeeType, price, needAmountOfCoffee, hasMilk, waterNeeded));
        saveChanges();
    }

    // Метод за изтриване на кафе по име от кафемашината
    public void deleteCoffeeByName(String coffeeName) {
        Coffee coffee = coffeeMachine.getCoffeeByName(coffeeName);
        coffeeMachine.removeCoffee(coffee);
        saveChanges();
    }

    // Метод за промяна на стойността на съставка
    public void changePropertiesValue(String property, int amount) {
        if (property.matches("-?\\d+(\\.\\d+)?")) {
            coins.put(property, coins.get(property) + amount);
            updateTotalMoneyAmount();
        } else {
            consumables.put(property, consumables.get(property) + amount);
        }
        saveChanges();
    }

    // Метод за проверка дали всички нужни съставки за приготвянето на кафе са налични
    protected boolean ingredientsAvailable(Coffee coffee) {
        return isEnoughConsumable(Consumables.COFFEE.toString(), coffee.getCoffeeNeeded()) &&
                (isEnoughConsumable(Consumables.MILK.toString(), milkNeeded) || !coffee.hasMilk()) &&
                isEnoughConsumable(Consumables.WATER.toString(), coffee.getWaterNeeded()) &&
                isEnoughConsumable(Consumables.SUGAR.toString(), coffeeMachine.getSugarSelected());
    }

    protected void updateInventory(Coffee coffee, HashMap<String, Integer> coins) {
        for (Consumables consumable : Consumables.values()) {
            if (consumable == Consumables.MONEY) {
                updateCoinsAmount(coins, true);
            } else {
                updateConsumable(consumable, coffee);
            }
        }
    }

    private void updateCoinsAmount(HashMap<String, Integer> coins, boolean add) {
        for (Map.Entry<String, Integer> entry : coins.entrySet()) {
            int newAmount = this.coins.getOrDefault(entry.getKey(), 0) + entry.getValue() * (add ? 1 : -1);
            this.coins.put(entry.getKey(), newAmount);
        }
        updateTotalMoneyAmount();
    }

    // Метод за актуализиране на стойността на парите в една централизирана точка
    private void updateTotalMoneyAmount() {
        int totalMoney = coins.entrySet()
                .stream()
                .mapToInt(entry -> parseCoinAmount(entry.getKey()) * entry.getValue()) // Convert key to int and multiply by value
                .sum();
        consumables.put(Consumables.MONEY.toString(), totalMoney);
        saveChanges();
    }

    private int parseCoinAmount(String coin) {
        if (coin.contains(".")) {
            return (int) (Double.parseDouble(coin) * 100);
        } else {
            return Integer.parseInt(coin) * 100;
        }
    }

    private boolean isEnoughConsumable(String consumableKey, int amountNeeded) {
        return consumables.getOrDefault(consumableKey, 0) >= amountNeeded;
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

    private void saveChanges() {
        dataHandler.saveConsumables(consumables);
        dataHandler.saveCoffees(coffeeMachine.getCoffees());
        dataHandler.saveCoins(coins);
    }

    // Метод за получаване на символа за парите
    public String getMoneySymbol() {
        return moneySymbol;
    }

    // set метод за задаване на максималното количество захар и автоматично изчисляване на sugarChangeBy
    public void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
        sugarChangeBy = sugarMax / 5; // Захарта се променя с една пета от максималното количество
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

    // Метод за получаване на имената на наличните кафета
    public String[] getCoffeeNames() {
        return coffeeMachine.getCoffeeNames();
    }

    public String getSelectedStatisticsDate() {
        return calendar.getCurrentDate();
    }

    public void changeSelectedStatisticsDate(boolean increment) {
        if (increment) {
            calendar.setSelectedDateTomorrow();
        } else {
            calendar.setSelectedDateYesterday();
        }
    }

    public HashMap<String, Integer> getSelectedStatistics() {
        return dataHandler.loadStatistic();
    }

    public String getPassword() {
        return dataHandler.getPassword();
    }

    // Метод за получаване на количеството, с което се променя захарта
    protected int getSugarChangeBy() {
        return sugarChangeBy;
    }

    // Метод за задаване на максималното количество захар
    protected int getSugarMax() {
        return sugarMax;
    }

    // Метод за получаване на количеството мляко, необходимо за приготвяне на кафе с мляко
    protected int getMilkNeeded() {
        return milkNeeded;
    }

    protected void addCoin(Nominals nominal) {
        changePropertiesValue(nominal.toString(), 1);
    }

    // Метод за актуализиране на количествата монети
    protected void removeCoins(HashMap<String, Integer> coins) {
        updateCoinsAmount(coins, false);
    }

    // Метод за получаване на монетите по номинали
    protected HashMap<String, Integer> getCoins() {
        return coins;
    }

    protected void addCoffeeToDailyStatistics(Coffee coffee) {
        statistics.addCoffeeToDailyStatistic(coffee);
        dataHandler.saveStatistics(statistics.getDailyStatistic());
    }
}