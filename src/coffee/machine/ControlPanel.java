package coffee.machine;

import java.util.HashMap;

import data.handler.DataHandler;

public class ControlPanel {
    private String moneySymbol = "bgn";

    private CoffeeMachine coffeeMachine;

    /*
        indices:
            0 - money
            1 - coffee
            2 - milk
            3 - water
            4 - sugar
     */

    // private int[] consumablesAvailable = new int[5];
    private HashMap<String, Integer> consumables;

    private int milkNeeded;

    private int sugarChangeBy;
    private int sugarMax;

    public enum Consumable {
        Money,
        Coffee,
        Milk,
        Sugar,
        Alchohol
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

    public boolean hasEnoughMilk(){
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

    public void addMoney(int amount) {
        consumables.put("Money", consumables.get("Money") + amount);
    }

    public void addSugar(int amount) {
        consumables.put("Sugar", consumables.get("Sugar") + amount);
    }

    public void addCoffee(int amount) {
        consumables.put("Coffee", consumables.get("Coffee") + amount);
    }

    public void addMilk(int amount) {
        consumables.put("Milk", consumables.get("Milk") + amount);
    }

    public void addWater(int amount) {
        consumables.put("Water", consumables.get("Water") + amount);
    }

    public String getMoneySymbol() {
        return moneySymbol;
    }

    public int getSugarMax() {
        return sugarMax;
    }

    public void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
        sugarChangeBy = sugarMax/5;
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

    public String[] getCoffeeNames() {
        return coffeeMachine.getCoffeeNames();
    }
}