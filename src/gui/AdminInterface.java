package gui;

import coffee.machine.ControlPanel;
import coffee.machine.ControlPanel.Consumable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminInterface {
    private final ControlPanel controlPanel;

    private final int windowWidth;
    private final int elementHeight;
    private final int elementXOffset;

    private String[] consumablesNames;
    private int[] numberOfComponents = new int[]{4, 2, 2, 4};

    private String[] labelsTitles = new String[]{"Add/ Remove consumable:", "Delete Coffee:", "Add new Coffee:", "Statistics:"};

    private final JPanel panel;
    private String[] buttonsText = new String[]{"Add", "Remove"};

    public AdminInterface(JPanel panel, ControlPanel controlPanel, int windowWidth, int elementHeight, int elementXOffset) {
        this.panel = panel;
        this.controlPanel = controlPanel;
        this.windowWidth = windowWidth;
        this.elementHeight = elementHeight;
        this.elementXOffset = elementXOffset;
        consumablesNames = controlPanel.getConsumablesNames();
    }

    protected void loadAdminInterface() {
        loadConsumables();
        loadLabels();
        loadComponents(0);
        loadComponents(1);
        loadComponents(2);
        loadComponents(3);
    }

    private void loadConsumables() {
        Consumable[] consumables = Consumable.values();

        for (int i = 0; i < consumables.length; i++) {
            Consumable consumable = consumables[i];
            JLabel label = new JLabel(String.format("%s available: %d", consumable.toString(), controlPanel.getConsumableValue(consumable)));
            label.setBounds(elementXOffset, elementHeight * 2 + elementHeight / 2 * i, windowWidth, elementHeight);
            panel.add(label);
        }
    }

    private void loadLabels() {
        for (int i = 0; i < labelsTitles.length; i++) {
            JLabel label = new JLabel(labelsTitles[i]);
            if (i == labelsTitles.length - 1)
                i++; //i се инкрементира при зареждане на последния label за да може статистиките да излязат по-надолу спрямо горната секция
            label.setBounds(elementXOffset, elementHeight * 6 + elementHeight * 2 * i - elementHeight / 2, windowWidth, elementHeight / 2);
            panel.add(label);
        }
    }

    private void loadComponents(int id) {
        int yPosition = elementHeight * (6 + id * 2);
        int width = (windowWidth - elementXOffset) / numberOfComponents[id];
        for (int i = 0; i < numberOfComponents[id]; i++) {
            Component component = switch (id) {
                case 0 -> loadComponents(Math.min(i, 2), i-2);
                case 1 -> loadComponents(i == 0 ? 0 : 2, i);
                case 2 -> loadComponents(i+1, 0);
                case 3 -> loadComponents(1, 0);
                default -> throw new IllegalStateException("Unexpected value: " + id);
            };
            component.setBounds(elementXOffset + width * i, yPosition, width - elementXOffset, elementHeight);
            panel.add(component);
        }
    }

    private Component loadComponents(int type, int id) {
        return switch (type) {
            case 0 -> getComboBox(id);   // Get ComboBox for type 0
            case 1 -> new JTextField();  // Get JTextField for type 1
            case 2 -> {
                JButton button = new JButton(buttonsText[id]);
                button.addActionListener(e -> handleButtonAction(id));
                yield button;
            }  // Get JButton for type 2
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private void handleButtonAction(int id) {
        //case 0 ->
    }

    // Method to create ComboBox based on id
    private JComboBox<String> getComboBox(int id) {
        String[] items = (id == 0) ? controlPanel.getCoffeeNames() : consumablesNames;
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.addActionListener(e -> {
            String selectedItem = (String) comboBox.getSelectedItem();
            System.out.println("Selected: " + selectedItem);
        });
        return comboBox;
    }

    /*
    // TODO
    // * ChatGPT предложи да има един главен панел и два под  
    // * панела (панел за поръчки и администраторскя панел), които да се обедният с CardLayout

    // * За статистките може да използваме JFreeChart или JMathPlot

    // CardLayout cardLayout = new CardLayout();
    // JPanel cardPanel = new JPanel(cardLayout);

    // JPanel page1 = new JPanel();
    // page1.add(new JLabel("Page 1"));
    // JButton toPage2 = new JButton("Go to Page 2");
    // page1.add(toPage2);

    // JPanel page2 = new JPanel();
    // page2.add(new JLabel("Page 2"));
    // JButton toPage1 = new JButton("Go to Page 1");
    // page2.add(toPage1);

    // cardPanel.add(page1, "Page 1");
    // cardPanel.add(page2, "Page 2");

    public static void main(String[] args) { // * Само за тестване
        JFrame frame = new JFrame("Admin Panel");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 1000);
        frame.setVisible(true);

        // JButton coffeeManagementButton = new JButton("Coffee Management");

        JButton addCoffeeButton = new JButton("Add Coffee"); // * Евентуален панел за добавяне на кафе или отделен прозорец
        JButton removeCoffeeButton = new JButton("Remove Coffee");
        JButton editCoffeeButton = new JButton("Edit Coffee");

        // * Позициите не знам как да ги изчисля

        // Колко и какви консумативи има + зареждане

        frame.add(addCoffeeButton);
        frame.add(removeCoffeeButton);
        frame.add(editCoffeeButton);
    }

     */
}
