package com.greasy.fighters.coffee.machine;

import java.util.HashMap;

import com.greasy.fighters.data.handler.DataHandler;

public class ControlPanel {
    private String moneySymbol = "bgn";

    private CoffeeMachine coffeeMachine;

    private HashMap<String, Integer> consumables;

    //TODO Krasi vurji tiq tupotii s jeison

    private HashMap<Integer, Integer> amountOfCoins = createAmountOfCoins();
    private HashMap<Integer, Integer> createAmountOfCoins() {
        HashMap<Integer, Integer> amountOfCoins = new HashMap<>();
        amountOfCoins.put(200, 0);
        amountOfCoins.put(100, 0);
        amountOfCoins.put(50, 10);
        amountOfCoins.put(20, 10);
        amountOfCoins.put(10, 10);
        amountOfCoins.put(5, 10);
        return amountOfCoins;
    }

    private int milkNeeded;

    private int sugarChangeBy;
    private int sugarMax;

    public enum Consumable {
        Money,
        Coffee,
        Water,
        Milk,
        Sugar
    }

    public ControlPanel(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
        this.consumables = DataHandler.loadConsumables();
    }

    public void addNewCoffee(String coffeeType, int price, int needAmountOfCoffee, boolean hasMilk, int waterNeeded) {
        coffeeMachine.addNewCoffee(new Coffee(coffeeType, price, needAmountOfCoffee, hasMilk, waterNeeded));
    }

    public void withdrawMoney(int amount) {
        System.out.printf("Withdrawing %d%s.", amount, moneySymbol);
        consumables.put("Money", consumables.get("Money") - amount);
    }

    public boolean hasEnoughMilk() {
        return consumables.get("Milk") >= milkNeeded;
    }

    public void updateInternalValues(Coffee coffee, int sugar) {
        consumables.put("Money", consumables.get("Money") + coffee.getPrice());
        consumables.put("Coffee", consumables.get("Coffee") - coffee.getCoffeeNeeded());
        if (coffee.hasMilk()) {
            consumables.put("Milk", consumables.get("Milk") - milkNeeded);
        }
        consumables.put("Water", consumables.get("Water") - coffee.getWaterNeeded());
        consumables.put("Sugar", consumables.get("Sugar") - sugar);
    }

    public String getMoneySymbol() {
        return moneySymbol;
    }

    public int getSugarMax() {
        return sugarMax;
    }

    public void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
        sugarChangeBy = sugarMax / 5;
    }

    public int getSugarChangeBy() {
        return sugarChangeBy;
    }

    public int getCoffeeAvailable() {
        return consumables.get("Coffee");
    }

    public int getMilkNeeded() {
        return milkNeeded;
    }

    public void setMilkNeeded(int milkNeeded) {
        this.milkNeeded = milkNeeded;
    }

    public String[] getConsumablesNames() {
        return new String[]{"Money", "Coffee", "Water", "Milk", "Sugar"};
    }

    public int getConsumableValue(Consumable consumable) {
        try {
            return consumables.get(consumable.toString());
        } catch (Exception e) {
            System.out.println("Consumable " + consumable.toString() + " is not found!");
            throw new RuntimeException(e);
        }

    }

    public void changeConsumableValue(String consumable, int amount) {
        //TODO krasi opravi go tva da pishe promenite v na jason faila
        consumables.put(consumable, consumables.get(consumable) + amount);
    }

    public HashMap<Integer, Integer> getAmountOfCoins() {
        return amountOfCoins;
    }

    public String[] getCoffeeNames() {
        return coffeeMachine.getCoffeeNames();
    }
}
