package gui;

import coffee.machine.CoffeeMachine;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class VisualManager {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 1000;

    private static final int ELEMENT_HEIGHT = WINDOW_HEIGHT / 20;
    private static final int ELEMENT_X_OFFSET = WINDOW_WIDTH / 30;

    private final ClientInterface clientInterface;
    private final AdminInterface adminInterface;

    private boolean regime = false; // Client - false; Admin - true;
    private final JPanel panel;

    private JLabel outputLabel;
    private JTextArea outputTextArea;
    private JButton regimeButton;

    public VisualManager(CoffeeMachine coffeeMachine) {
        panel = new JPanel();
        panel.setLayout(null); // Flexible layout
        clientInterface = new ClientInterface(panel, coffeeMachine, WINDOW_WIDTH, ELEMENT_HEIGHT, ELEMENT_X_OFFSET);
        adminInterface = new AdminInterface(panel, coffeeMachine.getControlPanel(), WINDOW_WIDTH, ELEMENT_HEIGHT, ELEMENT_X_OFFSET);
        initializeUI();
    }

    public void loadGUI() {
        clientInterface.loadClientInterface();
        //adminInterface.loadAdminInterface();
        loadOutputArea();
        loadChangeRegimeButton();
        loadFrame();
    }

    public void setOutputText(String text) {
        outputTextArea.setText(text);
        clearAfterTimeout();
    }

    private void initializeUI() {
        Font globalFont = new Font("Arial", Font.PLAIN, WINDOW_WIDTH / 40);
        Font textAreaFont = new Font("Arial", Font.PLAIN, WINDOW_WIDTH / 50);

        UIManager.put("Label.font", globalFont);
        UIManager.put("Button.font", globalFont);
        UIManager.put("TextField.font", globalFont);
        UIManager.put("ComboBox.font", globalFont);
        UIManager.put("TextArea.font", textAreaFont);
    }

    private void loadFrame() {
        JFrame frame = new JFrame("Coffee Machine");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(true);
    }

    private void loadOutputArea() {
        outputLabel = createLabel();
        panel.add(outputLabel);

        outputTextArea = createTextArea();
        panel.add(outputTextArea);
    }

    private JLabel createLabel() {
        JLabel label = new JLabel("Output:");
        label.setBounds(ELEMENT_X_OFFSET, ELEMENT_HEIGHT/2, WINDOW_WIDTH - ELEMENT_X_OFFSET*2, ELEMENT_HEIGHT/2);
        return label;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(VisualManager.ELEMENT_X_OFFSET, VisualManager.ELEMENT_HEIGHT, WINDOW_WIDTH - ELEMENT_X_OFFSET * 2, ELEMENT_HEIGHT);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        return textArea;
    }

    private void loadChangeRegimeButton() {
        regimeButton = new JButton("Admin Menu");
        regimeButton.setBounds(WINDOW_WIDTH - WINDOW_WIDTH / 5 - ELEMENT_X_OFFSET, WINDOW_HEIGHT - ELEMENT_HEIGHT * 2, WINDOW_WIDTH / 5, ELEMENT_HEIGHT);
        regimeButton.addActionListener(e -> toggleRegime());
        panel.add(regimeButton);
    }

    private void toggleRegime() {
        regime = !regime;
        removeRegimeSpecificComponents();
        panel.repaint();
        updateRegime();
    }

    private void removeRegimeSpecificComponents() {
        for (Component comp : panel.getComponents()) {
            if (comp != regimeButton && comp != outputTextArea && comp != outputLabel) {
                panel.remove(comp);
            }
        }
    }

    private void updateRegime() {
        if (regime) {
            regimeButton.setText("Client Menu");
            adminInterface.loadAdminInterface();
        } else {
            regimeButton.setText("Admin Menu");
            clientInterface.loadClientInterface();
        }
    }

    private void clearAfterTimeout() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                outputTextArea.setText("");
            }
        }, 5000);
    }
}