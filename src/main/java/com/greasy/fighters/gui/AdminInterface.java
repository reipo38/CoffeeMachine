package com.greasy.fighters.gui;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
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

import com.greasy.fighters.calendar.Calendar;
import com.greasy.fighters.coffee.machine.ControlPanel;
import com.greasy.fighters.enums.Consumables;
import com.greasy.fighters.enums.Nominals;

public class AdminInterface {
    private final ControlPanel controlPanel;

    private final int windowWidth;
    private final int elementHeight;
    private final int elementXOffset;

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
    public AdminInterface(JPanel panel, ControlPanel controlPanel, int windowWidth, int elementHeight, int elementXOffset, JButton regimeButton) {
        this.panel = panel;
        this.controlPanel = controlPanel;
        this.windowWidth = windowWidth;
        this.elementHeight = elementHeight;
        this.elementXOffset = elementXOffset;
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
        loadDiagram(controlPanel.getCalendar().getSelectedDate()); // Зареждане на диаграмата
    }

    // Зареждане на компонентите по редове
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
    @SuppressWarnings("unchecked")
    private void handleButtonAction(int i, int id) {
        try {
            switch (i) {
                case 0 -> controlPanel.changePropertiesValue( (String) ((JComboBox<String>) components[0][0]).getSelectedItem(), Integer.parseInt(((PlaceholderJTextField) components[0][1]).getText()) * (id == 2 ? 1 : -1));
                case 1 -> controlPanel.deleteCoffeeByName( (String) ((JComboBox<String>) components[1][0]).getSelectedItem());
                case 2 -> controlPanel.addNewCoffee(
                        ((PlaceholderJTextField) components[2][0]).getText(),                       // име на кафето
                        Integer.parseInt(((PlaceholderJTextField) components[3][0]).getText()),     // цена на кафето
                        Integer.parseInt(((PlaceholderJTextField) components[3][1]).getText()),     // количество кафе
                        Boolean.parseBoolean(((PlaceholderJTextField) components[3][2]).getText()), // съдържа ли мляко
                        Integer.parseInt(((PlaceholderJTextField) components[3][3]).getText()));    // необходимо количество вода
                case 4 -> {
                    Calendar calendar = controlPanel.getCalendar();
                    LocalDate selectedDate = calendar.getSelectedDate();

                    LocalDate newDate = (id == 0) // * проверява кой бутон е натиснат
                        ? calendar.calculateYesterday(selectedDate) //* ако е вярно
                        : calendar.calculateTomorrow(selectedDate); // * ако не е вярно

                    calendar.setSelectedDate(newDate);
                }
            }
            reloadPanel(); // Презареждане на панела
        } catch (NumberFormatException ignored){}
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
            String strValue = left && i == 0 ? value/100 + "." + value%100 : String.valueOf(value);
            JLabel label = new JLabel(String.format("%s available: %s", entry, strValue));
            label.setBounds(left ? elementXOffset : windowWidth / 2, elementHeight / 2 + elementHeight / 2 * i, windowWidth, elementHeight);
            panel.add(label);
        }
    }

    // Зареждане на етикетите за различните секции
    private void loadLabels() {
        for (int i = 0; i < labelsTitles.length; i++) {
            JLabel label = new JLabel(labelsTitles[i]);
            if (i == labelsTitles.length - 1)
                i++; // Инкрементиране на индекса за последния етикет, за да се подредят статистиките правилно
            label.setBounds(elementXOffset, elementHeight * 4 + elementHeight * 2 * i - elementHeight / 2, windowWidth, elementHeight / 2);
            panel.add(label);
        }
    }

    // Зареждане и показване на диаграмата за статистиките
    private void loadDiagram(LocalDate date) {
        /*
         * Може би премахни "Statistics:" етикета, защото диаграмата вече има заглавие
         */

        HashMap<String, Integer> coffeeData = controlPanel.getDataHandler().loadStatistic(date); // TODO: Проверка на статистиките за друг ден

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : coffeeData.entrySet()) {
            String category = entry.getKey();
            Integer value = entry.getValue();

            dataset.addValue(value, "Ordered", category); // Добавяне на стойностите в набора за диаграмата
        }

        JFreeChart chart = ChartFactory.createBarChart(
                controlPanel.getCalendar().formatDate(date), // Заглавие на диаграмата
                "",
                "Ordered",
                dataset
        );
        chart.setBackgroundPaint(new Color(238, 238, 238)); // * за да се слива с фона
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(elementXOffset, elementHeight * 13, windowWidth - elementXOffset * 2, elementHeight * 5);
        panel.add(chartPanel); // Добавяне на диаграмата в панела
    }
}
