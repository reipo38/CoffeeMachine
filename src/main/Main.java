package main;

import java.io.IOException;

import coffee.machine.CoffeeMachine;
import coffee.machine.ControlPanel;
import gui.VisualElements;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws IOException {
        CoffeeMachine coffeeMachine = new CoffeeMachine();
        ControlPanel controlPanel = coffeeMachine.getControlPanel();

        controlPanel.addNewCoffee("Coffee", 70, 50, false, 100);
        controlPanel.addNewCoffee("Coffee Long", 70, 50, false, 150);
        controlPanel.addNewCoffee("Coffee Double", 120, 100, false, 200);
        controlPanel.addNewCoffee("Coffee with milk", 120, 50, true, 100);
        controlPanel.addNewCoffee("Machiatto", 140, 40, true, 100);
        controlPanel.addNewCoffee("Cappuccino", 140, 40, true, 100);

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
        coffeeMachine.buyCoffee(3);

        JPanel panel = new JPanel();

        VisualElements visualElements = new VisualElements(panel, controlPanel);

        JFrame frame = new JFrame("Coffee machine"); // Създаване на основен прозорец
        frame.setSize(visualElements.WINDOW_WIDTH(), visualElements.WINDOW_HEIGHT()); // Задаване на размер на прозореца
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Определяне на операция при затваряне на прозореца
        frame.setVisible(true); // Показване на прозореца

        visualElements.loadVisualElements();

        frame.add(panel);
        panel.setLayout(null); // Без специфичен layout, позволява гъвкаво разположение на компонентите
    }
}