package coffee.machine;

import main.Main;
import statistic.Statistics;

import java.util.ArrayList;

public class CoffeeMachine {

    private final ArrayList<Coffee> coffees;
    private final ControlPanel controlPanel = new ControlPanel(this);
    private final int[] coins = new int[]{5, 10, 20, 50, 100, 200};

    private int sugarNeeded;

    private int insertedMoney;

    public CoffeeMachine(ArrayList<Coffee> coffees) {
        this.coffees = coffees;
    }

    protected void addNewCoffee(Coffee coffee) {
        coffees.add(coffee);
    }

    public void changeSugarQuantity(boolean increment) {
        if (increment) {
            if (sugarNeeded != controlPanel.getSugarMax()) sugarNeeded += controlPanel.getSugarChangeBy();
        } else {
            if (sugarNeeded != 0) sugarNeeded -= controlPanel.getSugarChangeBy();
        }
    }

    public void insertMoney(int amount) {
        insertedMoney += amount;
    }

    public void buyCoffee(int id) {
        Coffee coffee = getCoffeeById(id);
        if (coffee == null) {
            Main.visualManager.setOutputText("Coffee not found.");
            return;
        }
        if (isIngredientsAvailable(coffee)) {
            if (hasSufficientMoney(coffee)) {
                processCoffeePurchase(coffee);
                Statistics.addCoffeeToDailyStatistic(coffee);
            } else {
                Main.visualManager.setOutputText("Not enough money.");
            }
        } else {
            Main.visualManager.setOutputText("Sorry, this coffee is currently not available.");
        }
    }

    public void returnMoney() {
        if (insertedMoney > 0) {
            Main.visualManager.setOutputText(String.format("Dropping %d%s.", insertedMoney, controlPanel.getMoneySymbol()));
        }
    }

    private void processCoffeePurchase(Coffee coffee) {
        controlPanel.updateInternalValuesCHANGETHEFUCKINGNAME(coffee, sugarNeeded);

        prepareCoffee(coffee);
        insertedMoney = 0;
    }

    private void prepareCoffee(Coffee coffee) {
        Main.visualManager.setOutputText(
                String.format("Your %s is ready. (%dg coffee, %dml water, %dg sugar%s%nDropping %d coins.",
                        coffee.getName(), coffee.getCoffeeNeeded(), coffee.getWaterNeeded(), sugarNeeded, (coffee.hasMilk() ? String.format(", %dml milk)", controlPanel.getMilkNeeded()) : ")"), insertedMoney - coffee.getPrice()));
    }

    private Coffee getCoffeeById(int id) {
        if (id >= 0 && id < coffees.size()) {
            return coffees.get(id);
        }
        return null;
    }

    private boolean isIngredientsAvailable(Coffee coffee) {
        return ingredientsAvailable(coffee);
    }

    private boolean hasSufficientMoney(Coffee coffee) {
        return insertedMoney >= coffee.getPrice();
    }

    private boolean ingredientsAvailable(Coffee coffee) {
        return controlPanel.getCoffeeAvailable() >= coffee.getCoffeeNeeded() && ((coffee.hasMilk() && controlPanel.hasEnoughMilk()) || !coffee.hasMilk());
    }

    public int getInsertedMoney() {
        return insertedMoney;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public String[] getCoffeeNames() {
        int numCoffees = coffees.size();
        String[] coffeeNames = new String[numCoffees];
        for (int i = 0; i < numCoffees; i++) {
            coffeeNames[i] = coffees.get(i).getName();
        }
        return coffeeNames;
    }

    public int[] getCoins() {
        return coins;
    }

    public int getSugarPercentage() {
        return (sugarNeeded * 100) / controlPanel.getSugarMax();
    }
}