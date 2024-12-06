package coffee.machine;

import java.util.ArrayList;

public class ControlPanel {
    private String moneySymbol = "bgn";

    private CoffeeMachine coffeeMachine;
    private int moneyAvailable;

    private int coffeeAvailable;

    private int waterAvailable;

    private int milkAvailable;
    private int milkNeeded;

    private int sugarChangeBy;
    private int sugarAvailable;
    private int sugarMax;

    public ControlPanel(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }

    public void addNewCoffee(String coffeeType, int price, int needAmountOfCoffee, boolean hasMilk, int waterNeeded) {
        coffeeMachine.addNewCoffee(new Coffee(coffeeType, price, needAmountOfCoffee, hasMilk, waterNeeded));
    }

    public void withdrawMoney(int amount) {
        System.out.printf("Withdrawing %d%s.", amount, moneySymbol);
        moneyAvailable -= amount;
    }

    public boolean hasEnoughMilk(){
        return milkAvailable >= milkNeeded;
    }

    public void updateInternalValuesCHANGETHEFUCKINGNAME(Coffee coffee, int sugar) {
        moneyAvailable += coffee.getPrice();
        coffeeAvailable -= coffee.getCoffeeNeeded();
        waterAvailable -= coffee.getWaterNeeded();
        if (coffee.hasMilk()) {
            milkAvailable -= milkNeeded;
        }
        sugarAvailable -= sugar;
    }

    public String[] getCoffeeNames() {
        ArrayList<Coffee> coffees = coffeeMachine.getCoffees();
        int numCoffees = coffees.size();
        String[] coffeeNames = new String[numCoffees];
        for (int i = 0; i < numCoffees; i++) {
            coffeeNames[i] = coffees.get(i).getName();
        }
        return coffeeNames;
    }

    public void addMoney(int amount) {
        moneyAvailable += amount;
    }

    public void addSugar(int amount) {
        sugarAvailable += amount;
    }

    public void addCoffee(int amount) {
        coffeeAvailable += amount;
    }

    public void addMilk(int amount) {
        milkAvailable += amount;
    }

    public void addWater(int amount) {
        waterAvailable += amount;
    }

    public String getMoneySymbol() {
        return moneySymbol;
    }

    public int getSugarMax() {
        return sugarMax;
    }

    public void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
    }

    public int getSugarChangeBy() {
        return sugarChangeBy;
    }

    public void setSugarChangeBy(int sugarChangeBy) {
        this.sugarChangeBy = sugarChangeBy;
    }

    public int getCoffeeAvailable() {
        return coffeeAvailable;
    }

    public int getMilkNeeded() {
        return milkNeeded;
    }

    public void setMilkNeeded(int milkNeeded) {
        this.milkNeeded = milkNeeded;
    }
}