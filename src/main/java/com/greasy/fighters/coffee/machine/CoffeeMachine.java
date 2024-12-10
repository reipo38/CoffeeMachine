package com.greasy.fighters.coffee.machine;

import java.util.ArrayList;

import com.greasy.fighters.Main;
import com.greasy.fighters.data.handler.DataHandler;
import com.greasy.fighters.statistic.Statistics;

public class CoffeeMachine {

    private final ArrayList<Coffee> coffees;
    private final ControlPanel controlPanel = new ControlPanel(this);
    private final int[] coins = new int[]{5, 10, 20, 50, 100, 200};

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
            int[] coinCounts = calculateChange(insertedMoney);
            StringBuilder changeMessage = new StringBuilder("Returning change:\n");

            for (int i = coins.length - 1; i >= 0; i--) {
                if (coinCounts[i] > 0) {
                    if (coins[i] >= 100) {
                        changeMessage.append(String.format("%d x %.2f BGN\n", coinCounts[i], coins[i] / 100.0));
                    } else {
                        changeMessage.append(String.format("%d x %d stotinki\n", coinCounts[i], coins[i]));
                    }
                }
            }

            Main.visualManager.setOutputText(changeMessage.toString());
            insertedMoney = 0;
        }
    }

    private int[] calculateChange(int amount) {
        int[] coinCounts = new int[coins.length];
        
        for (int i = coins.length - 1; i >= 0; i--) {
            coinCounts[i] = amount / coins[i];
            amount %= coins[i];
        }
        
        return coinCounts;
    }

    private void processCoffeePurchase(Coffee coffee) {
        controlPanel.updateInternalValues(coffee, sugarNeeded);
        prepareCoffee(coffee);
        insertedMoney = 0;
    }

    private void prepareCoffee(Coffee coffee) {
        int change = insertedMoney - coffee.getPrice();
        int[] coinCounts = calculateChange(change);
        StringBuilder changeMessage = new StringBuilder();

        for (int i = coins.length - 1; i >= 0; i--) {
            if (coinCounts[i] > 0) {
                if (coins[i] >= 100) {
                    changeMessage.append(String.format("%d x %.2f BGN, ", coinCounts[i], coins[i] / 100.0));
                } else {
                    changeMessage.append(String.format("%d x %d stotinki, ", coinCounts[i], coins[i]));
                }
            }
        }

        if (changeMessage.length() > 0) {
            changeMessage.setLength(changeMessage.length() - 2);
        }
        Main.visualManager.setOutputText(
                String.format("Your %s is ready. (%dg coffee, %dml water, %dg sugar%s%nReturning change: %s",
                        coffee.getName(), coffee.getCoffeeNeeded(), coffee.getWaterNeeded(), sugarNeeded, 
                        (coffee.hasMilk() ? String.format(", %dml milk)", controlPanel.getMilkNeeded()) : ")"),
                        changeMessage.toString()));
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