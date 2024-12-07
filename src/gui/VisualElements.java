package gui;

import coffee.machine.CoffeeMachine;
import coffee.machine.ControlPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class VisualElements {
    private final CoffeeMachine coffeeMachine;
    private final ControlPanel controlPanel;

    // Constants for layout
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 1000;

    private int ELEMENT_HEIGHT = WINDOW_HEIGHT / 20;
    private int ELEMENT_X_OFFSET = WINDOW_WIDTH / 30;

    private final String[] LABELS_TITLES = {"Output:", "Insert coins (currently 0):", "Change sugar (currently 0%):", "Coffees:"};
    private final JLabel[] labels = new JLabel[LABELS_TITLES.length];

    private int[] countButtons;
    private ArrayList<ArrayList<String>> BUTTONS_TEXT;

    private final JPanel panel;
    private JTextArea outputTextField;

    public VisualElements(JPanel panel, CoffeeMachine coffeeMachine) {
        this.panel = panel;
        this.coffeeMachine = coffeeMachine;
        this.controlPanel = coffeeMachine.getControlPanel();
    }

    protected void loadClientInterface() {
        BUTTONS_TEXT = createButtonsText();
        countButtons = new int[]{7, 2, BUTTONS_TEXT.get(2).size()};

        for (int i = 0; i < 3; i++) {
            loadLabel(i);
            loadButtons(i);
        }
        loadLabel(3);  // Етикетите са с един повече от бутоните. За това се налага да се зареди последният етикет (Coffees.)
        loadOutputTextField();
        loadAdminButton();
    }

    protected void setOutputText(String text) {
        outputTextField.setText(text);
    }

    private ArrayList<ArrayList<String>> createButtonsText() {
        ArrayList<ArrayList<String>> buttonsText = new ArrayList<>();
        buttonsText.add(new ArrayList<>(List.of(".05", ".10", ".20", ".50", "1", "2", "Drop")));
        buttonsText.add(new ArrayList<>(List.of("-", "+")));
        buttonsText.add(new ArrayList<>(List.of(coffeeMachine.getCoffeeNames())));
        return buttonsText;
    }

    private void loadLabel(int id) {
        JLabel label = new JLabel(LABELS_TITLES[id]);
        label.setBounds(ELEMENT_X_OFFSET, ELEMENT_HEIGHT + ELEMENT_HEIGHT * 2 * id - ELEMENT_HEIGHT / 2, 400, ELEMENT_HEIGHT / 2);
        labels[id] = label;
        panel.add(label);
    }

    private void loadButtons(int id) {
        boolean isCoffeeButton = id == 2;
        int countButtonsPerRow = isCoffeeButton ? 2 : countButtons[id];
        int totalButtons = countButtons[id];
        int width = (WINDOW_WIDTH - ELEMENT_X_OFFSET) / countButtonsPerRow;

        for (int i = 0; i < totalButtons; i++) {
            JButton button = createButton(id, i, isCoffeeButton, width);
            panel.add(button);
        }
    }

    private JButton createButton(int id, int i, boolean isCoffeeButton, int width) {
        JButton button = new JButton(BUTTONS_TEXT.get(id).get(i));
        int yPosition = ELEMENT_HEIGHT * 3 + ELEMENT_HEIGHT * 2 * id;
        int xPosition = ELEMENT_X_OFFSET + (isCoffeeButton ? i % 2 : i) * width;
        if (isCoffeeButton) {
            yPosition += (i / 2) * (ELEMENT_HEIGHT + ELEMENT_HEIGHT / 2);
        }
        button.setBounds(xPosition, yPosition, width - ELEMENT_X_OFFSET, ELEMENT_HEIGHT);
        button.addActionListener(_ -> buttonAction(id, i));
        return button;
    }

    private void buttonAction(int id, int i) {
        switch (id) {
            case 0 -> handleCoinInsertion(i);
            case 1 -> handleSugarAdjustment(i);
            case 2 -> handleCoffeePurchase(i);
        }
    }

    private void handleCoinInsertion(int i) {
        if (i == 6) {
            coffeeMachine.returnMoney();
            labels[1].setText("Insert coins (currently 0):");
        } else {
            coffeeMachine.insertMoney(coffeeMachine.getCoins()[i]);
            labels[1].setText(String.format("Insert coins (currently %d):", coffeeMachine.getInsertedMoney()));
        }
    }

    private void handleSugarAdjustment(int i) {
        coffeeMachine.changeSugarQuantity(i == 1);
        labels[2].setText(String.format("Change sugar (currently %d%%):", coffeeMachine.getSugarPercentage()));
    }

    private void handleCoffeePurchase(int i) {
        coffeeMachine.buyCoffee(i);
        labels[1].setText(String.format("Insert coins (currently %d)", coffeeMachine.getInsertedMoney()));
    }

    private void loadOutputTextField() {
        outputTextField = new JTextArea();
        outputTextField.setBounds(ELEMENT_X_OFFSET, ELEMENT_HEIGHT, WINDOW_WIDTH - ELEMENT_X_OFFSET * 2, ELEMENT_HEIGHT);
        outputTextField.setEditable(false);
        outputTextField.setFocusable(false);
        panel.add(outputTextField);
    }

    private void loadAdminButton() {
        JButton button = new JButton("Admin Menu");
        int width = WINDOW_WIDTH/5;
        int xPosition = WINDOW_WIDTH - width - ELEMENT_X_OFFSET;
        int yPosition = WINDOW_HEIGHT - ELEMENT_HEIGHT*2;
        button.setBounds(xPosition, yPosition, width, ELEMENT_HEIGHT);
        button.addActionListener(_ -> loadAdminInterface());
        panel.add(button);
    }

    private void loadAdminInterface() {
        //TO DO
    }

    public int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    public int getWindowWidth() {
        return WINDOW_WIDTH;
    }
}
