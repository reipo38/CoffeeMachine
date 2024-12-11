package com.greasy.fighters.coffee.machine;

import java.util.HashMap;

import com.greasy.fighters.data.handler.DataHandler;

public class ControlPanel {
    /*** Клас ControlPanel. Отговаря за администраторката логика в кафемашината. Управляват се количествата на всичките съставки, общият брой пари, максималното количество захар, изолзваното количесто мляко и т.н.
     * поле moneySymbol - лесен начин за промяна на символа за парите
     * поле coffeeMachine - кафемашината, която този контролен панел управлява
     * поле consumables - съдържа количеството пари и всички съставки, налични в кафемашината
     * поле milkNeeded - количеството мляко, което се използва за приготвянето на едно кафе с мляко
     * поле sugarMax- максималното количество захар, което може да бъде избрано
     * поле sugarChangeBy - количество захар, с което избраното количество се променя. То винаги е една пета от максималното количество захар
     */
    private String moneySymbol = "bgn";

    private CoffeeMachine coffeeMachine;

    private HashMap<String, Integer> consumables;

    //TODO Krasi vurji tiq tupotii s jeison

    // речмик, който пази количеството монети от всеки номинал. ПОДЛЕЖИ НА ИЗМЕНЕНИЕ. ЗА СЕГА ЛОГИКАТА Е ТАКАВА
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

    public void deleteCoffeeByName(String coffeeName) {
        Coffee coffee = coffeeMachine.getCoffeeByName(coffeeName);
        coffeeMachine.deleteCoffee(coffee);
    }

    // Метод за проверка дали всички нужни съставки за кафето са налични
    public boolean ingredientsAvailable(Coffee coffee) {
        return consumables.get("Coffee") >= coffee.getCoffeeNeeded() && ((coffee.hasMilk() && consumables.get("Milk") >= milkNeeded) || !coffee.hasMilk());
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

    // set метод за максималното количество захар. sugarChangeBy винаги е една пета от него
    public void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
        sugarChangeBy = sugarMax / 5;
    }

    public int getSugarChangeBy() {
        return sugarChangeBy;
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
