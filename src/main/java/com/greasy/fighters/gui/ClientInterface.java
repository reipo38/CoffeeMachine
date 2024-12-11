package com.greasy.fighters.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;

import com.greasy.fighters.coffee.machine.CoffeeMachine;

public class ClientInterface {
    private final CoffeeMachine coffeeMachine;

    private final int windowWidth;
    private final int elementHeight;
    private final int elementXOffset;

    private JLabel outputLabel;
    private JTextArea outputTextArea;

    private JLabel[] labels;

    private final JPanel panel;

    public ClientInterface(JPanel panel, CoffeeMachine coffeeMachine, int windowWidth, int elementHeight, int elementXOffset) {
        this.panel = panel;
        this.coffeeMachine = coffeeMachine;
        this.windowWidth = windowWidth;
        this.elementHeight = elementHeight;
        this.elementXOffset = elementXOffset;
    }

    private JButton createButton(int index, int buttonIndex, boolean isCoffeeButton, int buttonWidth, ArrayList<ArrayList<String>> buttonLabels) {
        String buttonText = buttonLabels.get(index).get(buttonIndex);
        JButton button = new JButton(buttonText);
    
        int xPosition = elementXOffset + (isCoffeeButton ? buttonIndex % 2 : buttonIndex) * buttonWidth;
        int yPosition = calculateButtonYPosition(index, buttonIndex, isCoffeeButton);
    
        button.setBounds(xPosition, yPosition, buttonWidth - elementXOffset, elementHeight);
        button.addActionListener(e -> handleButtonAction(index, buttonIndex));
        button.setFocusable(false); 
    
        return button;
    }
    protected void loadClientInterface() {
        ArrayList<ArrayList<String>> buttonLabels = createButtonLabels();
        int[] buttonCounts = new int[]{7, 2, buttonLabels.get(2).size()};  // Coin buttons, Sugar buttons, Coffee buttons
        String[] labelsTitles = {"Insert coins (currently 0):", "Change sugar (currently 0%):", "Coffees:"};
        int labelsCount = labelsTitles.length;
        labels = new JLabel[labelsCount];
        for (int i = 0; i < labelsCount; i++) {
            loadLabel(i, labelsTitles);
            loadButtons(i, buttonCounts, buttonLabels);
        }
        loadOutputArea();
    }

    private ArrayList<ArrayList<String>> createButtonLabels() {
        ArrayList<ArrayList<String>> labelsList = new ArrayList<>();
        labelsList.add(new ArrayList<>(List.of("2", "1", "0.50", "0.20", "0.10", "0.05", "Drop")));  // Coin insertion buttons
        labelsList.add(new ArrayList<>(List.of("-", "+")));  // Sugar adjustment buttons
        labelsList.add(IntStream.range(0, coffeeMachine.getCoffeeNames().length) // Iterate over the indices
                .mapToObj(i -> coffeeMachine.getCoffeeNames()[i] + " " + coffeeMachine.getCoffeePrices()[i]) // Concatenate corresponding elements
                .collect(Collectors.toCollection(ArrayList::new))); // Collect the result into an ArrayList
        return labelsList;
    }

    private void loadLabel(int index, String[] labelsTitles) {
        JLabel label = new JLabel(labelsTitles[index]);
        label.setBounds(elementXOffset, calculateLabelYPosition(index), windowWidth, elementHeight / 2);
        labels[index] = label;
        panel.add(label);
    }

    private int calculateLabelYPosition(int index) {
        return elementHeight * 3 + elementHeight * 2 * index - elementHeight / 2;
    }

    private void loadButtons(int index, int[] buttonCounts, ArrayList<ArrayList<String>> buttonLabels) {
        boolean isCoffeeButton = index == 2;
        int buttonsInRow = isCoffeeButton ? 2 : buttonCounts[index];
        int totalButtons = buttonCounts[index];
        int buttonWidth = (windowWidth - elementXOffset) / buttonsInRow;

        for (int i = 0; i < totalButtons; i++) {
            JButton button = createButton(index, i, isCoffeeButton, buttonWidth, buttonLabels);
            panel.add(button);
        }
    }


    private int calculateButtonYPosition(int index, int buttonIndex, boolean isCoffeeButton) {
        int yPosition = elementHeight * 3 + elementHeight * 2 * index;

        if (isCoffeeButton) {
            yPosition += (buttonIndex / 2) * (elementHeight + elementHeight / 2);
        }
        return yPosition;
    }

    private void handleButtonAction(int index, int buttonIndex) {
        switch (index) {
            case 0 -> handleCoinInsertion(buttonIndex);
            case 1 -> handleSugarAdjustment(buttonIndex);
            case 2 -> handleCoffeePurchase(buttonIndex);
        }
    }

    private void handleCoinInsertion(int buttonIndex) {
        if (buttonIndex == 6) {
            coffeeMachine.returnMoney();
            labels[0].setText("Insert coins (currently 0):");
        } else {
            coffeeMachine.insertMoney(coffeeMachine.getCoins()[buttonIndex]);
            labels[0].setText(String.format("Insert coins (currently %d):", coffeeMachine.getInsertedMoney()));
        }
    }

    private void handleSugarAdjustment(int buttonIndex) {
        coffeeMachine.changeSugarQuantity(buttonIndex == 1); // true for "+" button, false for "-"
        labels[1].setText(String.format("Change sugar (currently %d%%):", coffeeMachine.getSugarPercentage()));
    }

    private void handleCoffeePurchase(int buttonIndex) {
        coffeeMachine.buyCoffee(buttonIndex);
        labels[0].setText(String.format("Insert coins (currently %d):", coffeeMachine.getInsertedMoney()));
    }

    private void loadOutputArea() {
        outputLabel = createLabel();
        panel.add(outputLabel);
        outputTextArea = createTextArea();
        panel.add(outputTextArea);
    }

    private JLabel createLabel() {
        JLabel label = new JLabel("Output:");
        label.setBounds(elementXOffset, elementHeight/2, windowWidth - elementXOffset*2, elementHeight/2);
        return label;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(elementXOffset, elementHeight, windowWidth - elementXOffset * 2, elementHeight);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        return textArea;
    }

    public void setOutputText(String text) {
        outputTextArea.setText(text);
    }

}
