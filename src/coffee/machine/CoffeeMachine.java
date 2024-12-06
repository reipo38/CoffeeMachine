package coffee.machine;

import main.Main;
import statistic.Statistics;

import java.util.ArrayList;

import static main.Main.visualManager;

public class CoffeeMachine {

    private final ArrayList<Coffee> coffees;
    private final ControlPanel controlPanel = new ControlPanel(this);

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

    public void returnMoney() {
        dropCoins(insertedMoney);
        insertedMoney = 0;
    }

    public void buyCoffee(int id) {
        Coffee coffee = getCoffeeById(id);
        if (coffee == null) {

            System.out.println("Coffee not found.");
            return;
        }
        if (isIngredientsAvailable(coffee)) {
            if (hasSufficientMoney(coffee)) {
                processCoffeePurchase(coffee);
                dropCoins(insertedMoney - coffee.getPrice());
                Statistics.addCoffeeToDailyStatistic(coffee);
            } else {
                System.out.println("Not enough money.");
            }
        } else {
            System.out.println("Sorry, this coffee is currently not available.");
        }
    }

    private void processCoffeePurchase(Coffee coffee) {
        prepareCoffee(coffee);
        controlPanel.updateInternalValuesCHANGETHEFUCKINGNAME(coffee, sugarNeeded);
    }

    private void prepareCoffee(Coffee coffee) {
        Main.visualManager.setOutputText(
                String.format("Your %s is ready. (%dg coffee, %dml water, %dg sugar%s",
                        coffee.getName(), coffee.getCoffeeNeeded(), coffee.getWaterNeeded(), sugarNeeded, (coffee.hasMilk() ? String.format(", %dml milk)", controlPanel.getMilkNeeded()) : ")")));
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

    private void dropCoins(int money) {
        if (money > 0) {
            System.out.printf("Dropping %d%s.", money, controlPanel.getMoneySymbol());
        }
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

    protected ArrayList<Coffee> getCoffees() {
        return coffees;
    }
}