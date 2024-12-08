package gui;

import javax.swing.JButton;
import javax.swing.JFrame;

public class AdminPanel {
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
}
