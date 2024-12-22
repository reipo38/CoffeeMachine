package com.greasy.fighters.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import com.greasy.fighters.coffee.machine.ControlPanel;
import com.greasy.fighters.enums.Consumables;
import com.greasy.fighters.enums.Nominals;

import static com.greasy.fighters.gui.VisualManager.WINDOW_WIDTH;
import static com.greasy.fighters.gui.VisualManager.ELEMENT_HEIGHT;
import static com.greasy.fighters.gui.VisualManager.ELEMENT_X_OFFSET;

public class AdminInterface {
    private final ControlPanel controlPanel;


    // Определяне на конфигурацията на компонентите по редове
    private final int[][] componentTypePerRow = new int[][]{
            new int[]{0, 1, 2, 2},
            new int[]{0, 2},
            new int[]{1, 2},
            new int[]{1, 1, 1, 1},
            new int[]{2, 2}
    };

    // Текстове за различните компоненти
    private final String[][] componentTexts = new String[][]{
            new String[]{"ILLEGAL_STATE", "Amount", "Add", "Remove"},
            new String[]{"ILLEGAL_STATE", "Delete"},
            new String[]{"Name", "Create"},
            new String[]{"Price", "Coffee amount", "Has milk", "Water needed"},
            new String[]{"←", "→"}
    };

    // Декларация на компоненти
    private final Component[][] components = new Component[][]{
            new Component[4],
            new Component[2],
            new Component[2],
            new Component[4],
            new Component[2]
    };

    // Заглавия на етикетите
    private final String[] labelsTitles = new String[]{"Add/ Remove consumable:", "Delete Coffee:", "Add new Coffee:", "Statistics:"};

    private final JPanel panel;
    private final JButton regimeButton;

    // Конструктор за инициализиране на административния интерфейс
    public AdminInterface(JPanel panel, ControlPanel controlPanel, JButton regimeButton) {
        this.panel = panel;
        this.controlPanel = controlPanel;
        this.regimeButton = regimeButton;
        this.panel.setLayout(null);
    }

    // Зареждане на административния интерфейс
    protected void loadAdminInterface() {
        loadConsumables(true); // Зареждане на левите консумативи
        loadConsumables(false); // Зареждане на десните консумативи
        loadLabels(); // Зареждане на етикетите
        for (int i = 0; i < componentTypePerRow.length; i++) {
            loadComponentsRow(i); // Зареждане на компонентите по редове
        }
        loadDiagram(); // Зареждане на диаграмата
    }

    // Зареждане на компонентите по редове
    private void loadComponentsRow(int i) {
        int yPosition = ELEMENT_HEIGHT * (4 + i * 2) - (i != 3 ? 0 : ELEMENT_HEIGHT/2);
        int width = (WINDOW_WIDTH - ELEMENT_X_OFFSET) / componentTypePerRow[i].length;
        int[] componentTypes = componentTypePerRow[i];
        for (int id = 0; id < componentTypes.length; id++) {
            Component component = loadComponent(i, id);
            component.setBounds(ELEMENT_X_OFFSET + width * id, yPosition, width - ELEMENT_X_OFFSET, ELEMENT_HEIGHT);
            components[i][id] = component;
            panel.add(component);
        }
    }

    // Зареждане на компонент за даден ред
    private Component loadComponent(int i, int id) {
        return switch (componentTypePerRow[i][id]) {
            case 0 -> getComboBox(i); // Зареждане на JComboBox
            case 1 -> new PlaceholderJTextField(componentTexts[i][id]); // Зареждане на PlaceholderJTextField
            case 2 -> getButton(i, id); // Зареждане на бутон
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    // Създаване на JComboBox компонент
    private JComboBox<String> getComboBox(int i) {
        // Избиране на елементи за JComboBox в зависимост от реда
        String[] items = (i == 0) ? Stream.concat(Arrays.stream(Consumables.stringValues()).skip(1), Arrays.stream(Nominals.stringValues()))
                .toArray(String[]::new) : controlPanel.getCoffeeNames();

        return new JComboBox<>(items);
    }

    // Създаване на JButton компонент
    private Component getButton(int i, int id) {
        JButton button = new JButton(componentTexts[i][id]);
        button.addActionListener(e -> handleButtonAction(i, id)); // Добавяне на слушател на събития за бутона
        return button;
    }

    // Обработка на действието при натискане на бутон
    private void handleButtonAction(int i, int id) {
        try {
            switch (i) {
                case 0 -> controlPanel.changePropertiesValue((String) ((JComboBox<String>) components[0][0]).getSelectedItem(), Integer.parseInt(((PlaceholderJTextField) components[0][1]).getText()) * (id == 2 ? 1 : -1));
                case 1 -> controlPanel.deleteCoffeeByName((String) ((JComboBox<String>) components[1][0]).getSelectedItem());
                case 2 -> controlPanel.addNewCoffee(
                        ((PlaceholderJTextField) components[2][0]).getText(),                       // име на кафето
                        Integer.parseInt(((PlaceholderJTextField) components[3][0]).getText()),     // цена на кафето
                        Integer.parseInt(((PlaceholderJTextField) components[3][1]).getText()),     // количество кафе
                        Boolean.parseBoolean(((PlaceholderJTextField) components[3][2]).getText()), // съдържа ли мляко
                        Integer.parseInt(((PlaceholderJTextField) components[3][3]).getText()));    // необходимо количество вода
                case 4 -> controlPanel.changeSelectedStatisticsDate(id == 0);
            }
            reloadPanel(); // Презареждане на панела
        } catch (NumberFormatException ignored) {
        }
    }

    // Презареждане на панела
    private void reloadPanel() {
        panel.removeAll();
        panel.add(regimeButton); // Добавяне на бутона обратно
        loadAdminInterface(); // Презареждане на интерфейса
        panel.repaint();
    }

    // Зареждане на консумативи
    private void loadConsumables(boolean left) {
        String[] entries = left ? Consumables.stringValues() : Nominals.stringValues();
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            int value = left ? controlPanel.getConsumableAmount(entry) : controlPanel.getCoinAmount(entry);
            String strValue = left && i == 0 ? value / 100 + "." + value % 100 : String.valueOf(value);
            JLabel label = new JLabel(String.format("%s available: %s", entry, strValue));
            label.setBounds(left ? ELEMENT_X_OFFSET : WINDOW_WIDTH / 2, ELEMENT_HEIGHT / 2 + ELEMENT_HEIGHT / 2 * i, WINDOW_WIDTH, ELEMENT_HEIGHT);
            panel.add(label);
        }
    }

    // Зареждане на етикетите за различните секции
    private void loadLabels() {
        for (int i = 0; i < labelsTitles.length; i++) {
            JLabel label = new JLabel(labelsTitles[i]);
            if (i == labelsTitles.length - 1)
                i++; // Инкрементиране на индекса за последния етикет, за да се подредят статистиките правилно
            label.setBounds(ELEMENT_X_OFFSET, ELEMENT_HEIGHT * 4 + ELEMENT_HEIGHT * 2 * i - ELEMENT_HEIGHT / 2, WINDOW_WIDTH, ELEMENT_HEIGHT / 2);
            panel.add(label);
        }
    }

    // Зареждане и показване на диаграмата за статистиките
    private void loadDiagram() {
        String date = controlPanel.getSelectedStatisticsDate();
        HashMap<String, Integer> coffeeData = controlPanel.getStatisticsForDate(date);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : coffeeData.entrySet()) {
            dataset.addValue(entry.getValue(), "Ordered", entry.getKey()); // Добавяне на стойностите в набора за диаграмата
        }
        JFreeChart chart = ChartFactory.createBarChart(date, "", "Ordered", dataset);
        chart.setBackgroundPaint(new Color(238, 238, 238)); // * за да се слива с фона
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(ELEMENT_X_OFFSET, ELEMENT_HEIGHT * 13, WINDOW_WIDTH - ELEMENT_X_OFFSET * 2, ELEMENT_HEIGHT * 5);
        panel.add(chartPanel); // Добавяне на диаграмата в панела
    }
}
