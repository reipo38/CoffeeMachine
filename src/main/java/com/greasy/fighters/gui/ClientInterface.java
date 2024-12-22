package com.greasy.fighters.gui;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.*;

import com.greasy.fighters.coffee.machine.CoffeeMachine;
import com.greasy.fighters.enums.Nominals;

import static com.greasy.fighters.gui.VisualManager.WINDOW_WIDTH;
import static com.greasy.fighters.gui.VisualManager.ELEMENT_HEIGHT;
import static com.greasy.fighters.gui.VisualManager.ELEMENT_X_OFFSET;

public class ClientInterface {
    private final CoffeeMachine coffeeMachine;

    private JTextArea outputTextArea;

    private String[][] buttonLabels;

    private JLabel[] labels;

    private final JPanel panel;

    public ClientInterface(JPanel panel, CoffeeMachine coffeeMachine) {
        this.panel = panel;
        this.coffeeMachine = coffeeMachine;
    }

    protected void loadClientInterface() {
        buttonLabels = createButtonLabels();
        int[] buttonCounts = new int[]{7, 2, buttonLabels[2].length};  // Coin buttons, Sugar buttons, Coffee buttons
        String[] labelsTitles = {"Insert coins (currently 0):", "Change sugar (currently 0%):", "Coffees:"};
        int labelsCount = labelsTitles.length;
        labels = new JLabel[labelsCount];
        for (int i = 0; i < labelsCount; i++) {
            loadLabel(i, labelsTitles);
            loadButtons(i, buttonCounts);
        }
        loadOutputArea();
    }

    private String[][] createButtonLabels() {
        return new String[][]{
                Stream.concat(Arrays.stream(Nominals.stringValues()), Stream.of("Drop")).toArray(String[]::new),
                new String[]{"-", "+"},
                IntStream.range(0, coffeeMachine.getCoffeeNames().length)
                        .mapToObj(i -> coffeeMachine.getCoffees().get(i).getName() + " - " + coffeeMachine.getCoffees().get(i).priceString() + coffeeMachine.getMoneySymbol())
                        .toArray(String[]::new)
        };
    }

    private void loadLabel(int index, String[] labelsTitles) {
        JLabel label = new JLabel(labelsTitles[index]);
        label.setBounds(ELEMENT_X_OFFSET, calculateLabelYPosition(index), WINDOW_WIDTH, ELEMENT_HEIGHT / 2);
        labels[index] = label;
        panel.add(label);
    }

    private int calculateLabelYPosition(int index) {
        return ELEMENT_HEIGHT * 3 + ELEMENT_HEIGHT * 2 * index - ELEMENT_HEIGHT / 2;
    }

    private void loadButtons(int index, int[] buttonCounts) {
        boolean isCoffeeButton = index == 2;
        int buttonsInRow = isCoffeeButton ? 2 : buttonCounts[index];
        int totalButtons = buttonCounts[index];
        int buttonWidth = (WINDOW_WIDTH - ELEMENT_X_OFFSET) / buttonsInRow;

        for (int i = 0; i < totalButtons; i++) {
            JButton button = createButton(index, i, isCoffeeButton, buttonWidth);
            panel.add(button);
        }
    }


    private int calculateButtonYPosition(int index, int buttonIndex, boolean isCoffeeButton) {
        int yPosition = ELEMENT_HEIGHT * 3 + ELEMENT_HEIGHT * 2 * index;

        if (isCoffeeButton) {
            yPosition += (buttonIndex / 2) * (ELEMENT_HEIGHT + ELEMENT_HEIGHT / 2);
        }
        return yPosition;
    }

    private JButton createButton(int index, int buttonIndex, boolean isCoffeeButton, int buttonWidth) {
        String buttonText = buttonLabels[index][buttonIndex];
        JButton button = new JButton(buttonText);

        int xPosition = ELEMENT_X_OFFSET + (isCoffeeButton ? buttonIndex % 2 : buttonIndex) * buttonWidth;
        int yPosition = calculateButtonYPosition(index, buttonIndex, isCoffeeButton);

        button.setBounds(xPosition, yPosition, buttonWidth - ELEMENT_X_OFFSET, ELEMENT_HEIGHT);
        button.addActionListener(e -> handleButtonAction(index, buttonIndex));
        button.setFocusable(false);

        return button;
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
            coffeeMachine.returnCoins();
            labels[0].setText("Insert coins (currently 0):");
        } else {
            coffeeMachine.insertCoin(Nominals.values()[buttonIndex]);
            labels[0].setText(String.format("Insert coins (currently %d):", coffeeMachine.getInsertedFunds()));
        }
    }

    private void handleSugarAdjustment(int buttonIndex) {
        coffeeMachine.changeSugarQuantity(buttonIndex == 1); // true for "+" button, false for "-"
        labels[1].setText(String.format("Change sugar (currently %d%%):", coffeeMachine.getSugarPercentage()));
    }

    private void handleCoffeePurchase(int buttonIndex) {
        coffeeMachine.buyCoffee(buttonIndex);
        labels[0].setText(String.format("Insert coins (currently %d):", coffeeMachine.getInsertedFunds()));
    }

    private void loadOutputArea() {
        JLabel outputLabel = createLabel();
        panel.add(outputLabel);
        outputTextArea = createTextArea();
        panel.add(outputTextArea);
    }

    private JLabel createLabel() {
        JLabel label = new JLabel("Output:");
        label.setBounds(ELEMENT_X_OFFSET, ELEMENT_HEIGHT / 2, WINDOW_WIDTH - ELEMENT_X_OFFSET * 2, ELEMENT_HEIGHT / 2);
        return label;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(ELEMENT_X_OFFSET, ELEMENT_HEIGHT, WINDOW_WIDTH - ELEMENT_X_OFFSET * 2, ELEMENT_HEIGHT);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        return textArea;
    }

    public void setOutputText(String text) {
        outputTextArea.setText(text);
    }
}
