package main;

import coffee.machine.CoffeeMachine;
import coffee.machine.ControlPanel;
import data.handler.DataHandler;
import gui.*;

public class Main {
    private static CoffeeMachine coffeeMachine = new CoffeeMachine(DataHandler.loadCoffeeTypes());
    private static ControlPanel controlPanel = coffeeMachine.getControlPanel();
    public static final VisualManager visualManager = new VisualManager(controlPanel);

    public static void main(String[] args) {
        // controlPanel.addNewCoffee("Coffee", 70, 50, false, 100);
        // controlPanel.addNewCoffee("Coffee Long", 70, 50, false, 150);
        // controlPanel.addNewCoffee("Coffee Double", 120, 100, false, 200);
        // controlPanel.addNewCoffee("Coffee with milk", 120, 50, true, 100);
        // controlPanel.addNewCoffee("Machiatto", 140, 40, true, 100);
        // controlPanel.addNewCoffee("Cappuccino", 140, 40, true, 100);

        controlPanel.addMoney(1000);
        controlPanel.addCoffee(1000);
        controlPanel.addSugar(1000);
        controlPanel.addMilk(1000);
        controlPanel.addWater(1000);

        controlPanel.setSugarChangeBy(5);
        controlPanel.setSugarMax(20);
        controlPanel.setMilkNeeded(25);

        coffeeMachine.insertMoney(200);
        coffeeMachine.changeSugarQuantity(true);

        visualManager.loadGUI();

        coffeeMachine.buyCoffee(0);

    }
}