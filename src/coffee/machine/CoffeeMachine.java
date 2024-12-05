package coffee.machine;

import statistic.Statistics;

import java.util.ArrayList;

public class CoffeeMachine {

    private final ArrayList<Coffee> coffees = new ArrayList<>();
    private final ControlPanel controlPanel = new ControlPanel(this);

    private int sugarNeeded;

    private int insertedMoney;

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
        System.out.printf("""
                Dropping cup.
                Adding %dg sugar.%s
                Your %s is finished.
                """, sugarNeeded, (coffee.hasMilk() ? String.format("%nAdding %dml milk.", controlPanel.getMilkNeeded()) : ""), coffee.getName());
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

    public ArrayList<Coffee> getCoffees() {
        return coffees;
    }
}