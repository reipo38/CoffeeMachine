package com.greasy.fighters;

import com.greasy.fighters.coffee.machine.CoffeeMachine;
import com.greasy.fighters.coffee.machine.ControlPanel;
import com.greasy.fighters.data.handler.DataHandler;
import com.greasy.fighters.gui.VisualManager;
import com.greasy.fighters.statistic.Statistics;

public class Main {
    public static final CoffeeMachine coffeeMachine = new CoffeeMachine();
    private static final ControlPanel controlPanel = coffeeMachine.getControlPanel();
    public static final VisualManager visualManager = new VisualManager(coffeeMachine);

    public static void main(String[] args) {

        System.out.println(controlPanel.getCoins());
        // controlPanel.addNewCoffee("Coffee", 70, 50, false, 100);
        // controlPanel.addNewCoffee("Coffee Long", 70, 50, false, 150);
        // controlPanel.addNewCoffee("Coffee Double", 120, 100, false, 200);
        // controlPanel.addNewCoffee("Coffee with milk", 120, 50, true, 100);
        // controlPanel.addNewCoffee("Machiatto", 140, 40, true, 100);
        // controlPanel.addNewCoffee("Cappuccino", 140, 40, true, 100);

        // controlPanel.addCoffee(1000);
        // controlPanel.addSugar(1000);
        // controlPanel.addMilk(1000);
        // controlPanel.addWater(1000);

        controlPanel.setSugarMax(25);
        controlPanel.setMilkNeeded(25);

        visualManager.loadGUI();
    }
}