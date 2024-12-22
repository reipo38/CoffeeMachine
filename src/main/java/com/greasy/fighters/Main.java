package com.greasy.fighters;

import com.greasy.fighters.coffee.machine.CoffeeMachine;
import com.greasy.fighters.coffee.machine.ControlPanel;
import com.greasy.fighters.gui.VisualManager;

public class Main {
    public static final CoffeeMachine coffeeMachine = new CoffeeMachine();
    private static final ControlPanel controlPanel = new ControlPanel(coffeeMachine);
    public static final VisualManager visualManager = new VisualManager(coffeeMachine, controlPanel);

    public static void main(String[] args) {

        System.out.println(controlPanel.getCoins());

        controlPanel.setSugarMax(25);
        controlPanel.setMilkNeeded(25);

        visualManager.loadGUI();
    }
}