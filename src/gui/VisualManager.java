package gui;

import coffee.machine.CoffeeMachine;

import javax.swing.*;
import java.awt.*;

import java.util.Timer;
import java.util.TimerTask;

public class VisualManager {
    private VisualElements visualElements;
    private final CoffeeMachine coffeeMachine;

    public VisualManager(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }

    public void loadGUI() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null); // Без специфичен layout, позволява гъвкаво разположение на компонентите
        visualElements = new VisualElements(jPanel, coffeeMachine);
        initializeUI();
        visualElements.loadClientInterface();
        loadFrame(jPanel);

    }

    public void setOutputText(String text) {
        visualElements.setOutputText(text);
        clearAfterTimout();
    }

    private void loadFrame(JPanel jPanel) {
        JFrame frame = new JFrame("Coffee machine"); // Създаване на основен прозорец
        frame.setSize(visualElements.getWindowWidth(), visualElements.getWindowHeight()); // Задаване на размер на прозореца
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Определяне на операция при затваряне на прозореца
        frame.setVisible(true); // Показване на прозореца
        frame.add(jPanel);
    }

    private void initializeUI() {
        Font globalFont = new Font("Arial", Font.PLAIN, visualElements.getWindowWidth() / 40);
        Font textAreaFont = new Font("Arial", Font.PLAIN, visualElements.getWindowWidth() / 50);
        UIManager.put("Label.font", globalFont);
        UIManager.put("Button.font", globalFont);
        UIManager.put("TextArea.font", textAreaFont);
    }

    private void clearAfterTimout() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                visualElements.setOutputText("");
            }
        }, 5000);
    }
}
