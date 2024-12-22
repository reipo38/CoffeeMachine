package com.greasy.fighters.gui;

import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.greasy.fighters.coffee.machine.CoffeeMachine;
import com.greasy.fighters.coffee.machine.ControlPanel;

public class VisualManager {
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 1000;

    public static final int ELEMENT_HEIGHT = WINDOW_HEIGHT / 20;
    public static final int ELEMENT_X_OFFSET = WINDOW_WIDTH / 30;

    private final ClientInterface clientInterface;
    private final AdminInterface adminInterface;
    private final PasswordPopup passwordPopup;

    private boolean regime = false; // Client - false; Admin - true;
    private final JPanel panel;

    private JButton regimeButton;

    public VisualManager(CoffeeMachine coffeeMachine, ControlPanel controlPanel) {
        panel = new JPanel();
        panel.setLayout(null); // Flexible layout
        initializeUI();
        clientInterface = new ClientInterface(panel, coffeeMachine);
        regimeButton = createRegimeButton();
        adminInterface = new AdminInterface(panel, controlPanel, regimeButton);
        passwordPopup = new PasswordPopup(controlPanel);
    }

    public void loadGUI() {
        clientInterface.loadClientInterface();
        loadChangeRegimeButton();
        loadFrame();
    }

    public void setOutputText(String text) {
        clientInterface.setOutputText(text);
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

    private void loadChangeRegimeButton() {
        panel.add(regimeButton);
    }

    private JButton createRegimeButton() {
        regimeButton = new JButton("Admin Menu");
        regimeButton.setBounds(WINDOW_WIDTH - WINDOW_WIDTH / 5 - ELEMENT_X_OFFSET, WINDOW_HEIGHT - ELEMENT_HEIGHT * 2, WINDOW_WIDTH / 5, ELEMENT_HEIGHT);
        regimeButton.addActionListener(e -> toggleRegime());
        return regimeButton;
    }

    private void toggleRegime() {
        if (!regime) { // ако сме в главния панел
            boolean isPasswordValidated = passwordPopup.validatePassword(); // валидира паролата
            if (!isPasswordValidated) {
                return; // спира метода до тук
            }
        }
        regime = !regime;
        panel.removeAll();
        loadChangeRegimeButton();
        panel.repaint();
        updateRegime();
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
}