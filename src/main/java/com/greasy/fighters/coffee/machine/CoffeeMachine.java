package com.greasy.fighters.coffee.machine;

import java.util.ArrayList;

import com.greasy.fighters.Main;
import com.greasy.fighters.data.handler.DataHandler;
import com.greasy.fighters.statistic.Statistics;

public class CoffeeMachine {

    private final ArrayList<Coffee> coffees;
    private final ControlPanel controlPanel = new ControlPanel(this);
    private final int[] coins = new int[]{200, 100, 50, 20, 10, 5};

    private int sugarNeeded;

    private int insertedMoney;

    public CoffeeMachine(ArrayList<Coffee> coffees) {
        this.coffees = coffees;
    }

    public void addNewCoffee(Coffee coffee) {
        coffees.add(coffee);
        DataHandler.saveCoffees(coffees);
    }

    public void deleteCoffee(Coffee coffee) {
        coffees.remove(coffee);
        DataHandler.saveCoffees(coffees);
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
            Main.visualManager.setOutputText(calculateChange(insertedMoney));
            insertedMoney = 0;
        }
    }

    private void processCoffeePurchase(Coffee coffee) {
        controlPanel.updateInternalValues(coffee, sugarNeeded);
        prepareCoffee(coffee);
        insertedMoney = 0;
    }

    private void prepareCoffee(Coffee coffee) {
        Main.visualManager.setOutputText(
                String.format("Your %s is ready. (%dg coffee, %dml water, %dg sugar%s%nDropping %s.",
                        coffee.getName(), coffee.getCoffeeNeeded(), coffee.getWaterNeeded(), sugarNeeded, (coffee.hasMilk() ? String.format(", %dml milk)", controlPanel.getMilkNeeded()) : ")"), calculateChange(insertedMoney - coffee.getPrice())));
    }

    private String calculateChange(int amountMoney) {
        StringBuilder str = new StringBuilder();
        for(int i: coins){
            if (controlPanel.getAmountOfCoins().get(i) != 0) {
                int coinAmount = amountMoney/i;
                if (coinAmount != 0) {
                    str.append(String.format(moneyAsString(i, coinAmount)));
                }
                amountMoney %=i;
            }
        }
        return str.toString();


    }

    private String moneyAsString(int coinNominal, int moneyAmount) {
        if (coinNominal >= 100) {
            return String.format(" %d:coins:%d; ", coinNominal/100, moneyAmount);
        }
        return String.format("  0.%d coins:%d ; ", coinNominal, moneyAmount);

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
            coffeeNames[i] = String.format("%s - %d%s", coffees.get(i).getName(), coffees.get(i).getPrice(), controlPanel.getMoneySymbol());
        }
        return coffeeNames;
    }

    public int[] getCoins() {
        return coins;
    }

    public int getSugarPercentage() {
        return (sugarNeeded * 100) / controlPanel.getSugarMax();
    }

    public Coffee getCoffeeByName(String name) {
        for (Coffee coffee : coffees) {
            if (coffee.getName().equals(name)) {
                return coffee;
            }
        }

        return null;
    }
}