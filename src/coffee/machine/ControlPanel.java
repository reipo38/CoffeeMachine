package coffee.machine;

import java.util.ArrayList;

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

    private int[] consumablesAvailable = new int[5];

    private int milkNeeded;

    private int sugarChangeBy;
    private int sugarMax;

    public ControlPanel(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }

    public void addNewCoffee(String coffeeType, int price, int needAmountOfCoffee, boolean hasMilk, int waterNeeded) {
        coffeeMachine.addNewCoffee(new Coffee(coffeeType, price, needAmountOfCoffee, hasMilk, waterNeeded));
    }

    public void withdrawMoney(int amount) {
        System.out.printf("Withdrawing %d%s.", amount, moneySymbol);
        consumablesAvailable[0] -= amount;
    }

    public boolean hasEnoughMilk(){
        return consumablesAvailable[2] >= milkNeeded;
    }

    public void updateInternalValues(Coffee coffee, int sugar) {
        consumablesAvailable[0] += coffee.getPrice();
        consumablesAvailable[1] -= coffee.getCoffeeNeeded();
        if (coffee.hasMilk()) {
            consumablesAvailable[2] -= milkNeeded;
        }
        consumablesAvailable[3] -= coffee.getWaterNeeded();
        consumablesAvailable[4] -= sugar;
    }

    public void addMoney(int amount) {
        consumablesAvailable[0] += amount;
    }

    public void addSugar(int amount) {
        consumablesAvailable[4] += amount;
    }

    public void addCoffee(int amount) {
        consumablesAvailable[1] += amount;
    }

    public void addMilk(int amount) {
        consumablesAvailable[2] += amount;
    }

    public void addWater(int amount) {
        consumablesAvailable[3] += amount;
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
        return consumablesAvailable[1];
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

    public int getConsumableValue(int id) {
        return consumablesAvailable[id];
    }

    public String[] getCoffeeNames() {
        return coffeeMachine.getCoffeeNames();
    }
}