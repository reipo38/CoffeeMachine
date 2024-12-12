package com.greasy.fighters.gui;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.greasy.fighters.enums.Consumables;
import com.greasy.fighters.enums.Nominals;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import com.greasy.fighters.coffee.machine.ControlPanel;
import com.greasy.fighters.data.handler.DataHandler;

public class AdminInterface {
    private final ControlPanel controlPanel;

    private final int windowWidth;
    private final int elementHeight;
    private final int elementXOffset;

    private final int[][] componentTypePerRow = new int[][]{
            new int[]{0, 1, 2, 2},
            new int[]{0, 2},
            new int[]{1, 2},
            new int[]{1, 1, 1, 1},
            new int[]{2, 2}
    };

    private final String[][] componentTexts = new String[][]{
            new String[]{"ILLEGAL_STATE", "Amount", "Add", "Remove"},
            new String[]{"ILLEGAL_STATE", "Delete"},
            new String[]{"Name", "Create"},
            new String[]{"Price", "Coffee amount", "Has milk", "Water needed"},
            new String[]{"<-", "->"}
    };

    private final Component[][] components = new Component[][]{
            new Component[4],
            new Component[2],
            new Component[2],
            new Component[4],
            new Component[2]
    };

    private final String[] labelsTitles = new String[]{"Add/ Remove consumable:", "Delete Coffee:", "Add new Coffee:", "Statistics:"};

    private final JPanel panel;
    private final JButton regimeButton;

    public AdminInterface(JPanel panel, ControlPanel controlPanel, int windowWidth, int elementHeight, int elementXOffset, JButton regimeButton) {
        this.panel = panel;
        this.controlPanel = controlPanel;
        this.windowWidth = windowWidth;
        this.elementHeight = elementHeight;
        this.elementXOffset = elementXOffset;
        this.regimeButton = regimeButton;
        this.panel.setLayout(null);
    }

    protected void loadAdminInterface() {
        loadConsumables(true);
        loadConsumables(false);
        loadLabels();
        for (int i = 0; i < componentTypePerRow.length; i++) {
            loadComponentsRow(i);
        }
        loadDiagram();
    }

    private void loadComponentsRow(int i) {
        int yPosition = elementHeight * (4 + i * 2);
        int width = (windowWidth - elementXOffset) / componentTypePerRow[i].length;
        int[] componentTypes = componentTypePerRow[i];
        for (int id = 0; id < componentTypes.length; id++) {
            Component component = loadComponent(i, id);
            component.setBounds(elementXOffset + width * id, yPosition, width - elementXOffset, elementHeight);
            components[i][id] = component;
            panel.add(component);
        }
    }

    private Component loadComponent(int i, int id) {
        return switch (componentTypePerRow[i][id]) {
            case 0 -> getComboBox(i);
            case 1 -> new PlaceholderJTextField(componentTexts[i][id]);
            case 2 -> getButton(i, id);
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    private JComboBox<String> getComboBox(int i) {
        String[] items = (i == 0) ? Stream.concat(Arrays.stream(Consumables.stringValues()).skip(1), Arrays.stream(Nominals.stringValues()))
                .toArray(String[]::new) : controlPanel.getCoffeeNames();

        return new JComboBox<>(items);
    }

    private Component getButton(int i, int id) {
        JButton button = new JButton(componentTexts[i][id]);
        button.addActionListener(e -> handleButtonAction(i, id));
        return button;
    }

    @SuppressWarnings("unchecked")
    private void handleButtonAction(int i, int id) {
        try {
            switch (i) {
                case 0 -> controlPanel.changePropertiesValue( (String) ((JComboBox<String>) components[0][0]).getSelectedItem(), Integer.parseInt(((PlaceholderJTextField) components[0][1]).getText()) * (id == 2 ? 1 : -1));
                case 1 -> controlPanel.deleteCoffeeByName( (String) ((JComboBox<String>) components[1][0]).getSelectedItem());
                case 2 -> controlPanel.addNewCoffee(
                        ((PlaceholderJTextField) components[2][0]).getText(),                       // name
                        Integer.parseInt(((PlaceholderJTextField) components[3][0]).getText()),     // price
                        Integer.parseInt(((PlaceholderJTextField) components[3][1]).getText()),     // coffeeNeeded
                        Boolean.parseBoolean(((PlaceholderJTextField) components[3][2]).getText()), // hasMilk
                        Integer.parseInt(((PlaceholderJTextField) components[3][3]).getText()));    // waterNeeded
                //case 4 -> //TODO tuka trq da e logikata za smqna na statistikite. id=0 - nazad ; id=1 - napred
            }
            reloadPanel();
        } catch (NumberFormatException ignored){}
        }

    private void reloadPanel() {
        panel.removeAll();
        panel.add(regimeButton);
        loadAdminInterface();
        panel.repaint();
    }

    private void loadConsumables(boolean left) {
        String[] entries = left ? Consumables.stringValues() : Nominals.stringValues();
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            int value = left ? controlPanel.getConsumableAmount(entry) : controlPanel.getCoinAmount(entry);
            JLabel label = new JLabel(String.format("%s available: %d", entry, value));
            label.setBounds(left ? elementXOffset : windowWidth/2, elementHeight/2 + elementHeight / 2 * i, windowWidth, elementHeight);
            panel.add(label);
        }
    }

    private void loadLabels() {
        for (int i = 0; i < labelsTitles.length; i++) {
            JLabel label = new JLabel(labelsTitles[i]);
            if (i == labelsTitles.length - 1)
                i++; //i се инкрементира при зареждане на последния label за да може статистиките да излязат по-надолу спрямо горната секция
            label.setBounds(elementXOffset, elementHeight * 4 + elementHeight * 2 * i - elementHeight / 2, windowWidth, elementHeight / 2);
            panel.add(label);
        }
    }

    private void loadDiagram() {
        /*
         * може би премахни Statistics: label-а, защото има заглавие
         */
        HashMap<String, Integer> coffeeData = controlPanel.getDataHandler().loadStatistic(); // TODO: виж статистиките за друг ден

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : coffeeData.entrySet()) {
            String category = entry.getKey();
            Integer value = entry.getValue();

            dataset.addValue(value, "Ordered", category);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Ordered Coffees Today",
                "",
                "Ordered",
                dataset
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(elementXOffset, elementHeight*13, windowWidth - elementXOffset*2, elementHeight*5);
        panel.add(chartPanel);
    }
}
