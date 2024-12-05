package gui;

import coffee.machine.ControlPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class VisualElements {
    private final ControlPanel controlPanel;

    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 1000;

    private final int ELEMENT_HEIGHT = WINDOW_HEIGHT / 25;

    private final int ELEMENT_LEFT_X = WINDOW_WIDTH / 25;

    private final String[] LABELS = new String[]{"Output:", "Insert coins (currently X):", "Change sugar (currently X%):", "Coffees:"};

    private int[] countButtons;

    private ArrayList<ArrayList<String>> BUTTONS_TEXT;

    private final JPanel panel;

    public VisualElements(JPanel panel, ControlPanel controlPanel) {
        this.panel = panel;
        this.controlPanel = controlPanel;
    }

    public void loadVisualElements() {
        BUTTONS_TEXT = new ArrayList<>() {{
            add(new ArrayList<>(List.of(".05", ".10", ".20", ".50", "1", "2", "Drop")));
            add(new ArrayList<>(List.of("-", "+")));
            add(new ArrayList<>(List.of(controlPanel.getCoffeeNames())));
        }};
        countButtons = new int[]{7, 2, BUTTONS_TEXT.get(2).size()};

        int i;
        for (i = 0; i < 3; i++) {
            loadLabel(i);
            loadButtons(i);
        }
        loadLabel(i);
        loadOutputTextField();
    }

    private void loadLabel(int id) {
        JLabel label = new JLabel(LABELS[id]);
        label.setBounds(ELEMENT_LEFT_X, ELEMENT_HEIGHT + ELEMENT_HEIGHT * 2 * id - ELEMENT_HEIGHT / 2, 400, ELEMENT_HEIGHT / 2);
        panel.add(label);
    }

    private void loadButtons(int id) {
        boolean printingCoffees = id == 2;
        int countButtonsPerRow = printingCoffees ? 2 : countButtons[id];
        int totalButtons = countButtons[id];
        int width = (WINDOW_WIDTH - ELEMENT_LEFT_X) / countButtonsPerRow;
        for (int i = 0; i < totalButtons; i++) {
            JButton button = new JButton(BUTTONS_TEXT.get(id).get(i));
            int yPosition = ELEMENT_HEIGHT*3 + ELEMENT_HEIGHT * 2 * id;
            int xPosition = ELEMENT_LEFT_X + (printingCoffees ? i % 2 : i) * width;
            if (printingCoffees) {
                yPosition += (i / 2) * (ELEMENT_HEIGHT + ELEMENT_HEIGHT / 2);
            }
            button.setBounds(xPosition, yPosition, width - ELEMENT_LEFT_X, ELEMENT_HEIGHT);
            panel.add(button);
        }
    }

    private void loadOutputTextField() {
        JTextField textField = new JTextField();
        textField.setBounds(ELEMENT_LEFT_X, ELEMENT_HEIGHT, WINDOW_WIDTH - ELEMENT_LEFT_X*2, ELEMENT_HEIGHT);
        textField.setEditable(false);
        textField.setFocusable(false);
        panel.add(textField);
    }

    public int WINDOW_HEIGHT() {
        return WINDOW_HEIGHT;
    }

    public int WINDOW_WIDTH() {
        return WINDOW_WIDTH;
    }
}
