package gui;

import coffee.machine.ControlPanel;

import javax.swing.*;

public class VisualManager {
    private VisualElements visualElements;
    private ControlPanel controlPanel;

    public VisualManager(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    public void loadGUI() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null); // Без специфичен layout, позволява гъвкаво разположение на компонентите
        visualElements = new VisualElements(jPanel, controlPanel);
        visualElements.loadVisualElements();
        loadFrame(jPanel);
    }

    public void setOutputText(String text) {
        visualElements.setOutputText(text);
    }

    private void loadFrame(JPanel jPanel) {
        JFrame frame = new JFrame("Coffee machine"); // Създаване на основен прозорец
        frame.setSize(visualElements.WINDOW_WIDTH(), visualElements.WINDOW_HEIGHT()); // Задаване на размер на прозореца
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Определяне на операция при затваряне на прозореца
        frame.setVisible(true); // Показване на прозореца
        frame.add(jPanel);
    }
}
