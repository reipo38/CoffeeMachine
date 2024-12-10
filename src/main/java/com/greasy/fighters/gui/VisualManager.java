package com.greasy.fighters.gui;

import java.awt.Component;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.greasy.fighters.coffee.machine.CoffeeMachine;

public class VisualManager {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 1000;

    private static final int ELEMENT_HEIGHT = WINDOW_HEIGHT / 20;
    private static final int ELEMENT_X_OFFSET = WINDOW_WIDTH / 30;

    private final ClientInterface clientInterface;
    private final AdminInterface adminInterface;
    private final PasswordPopup passwordPopup;

    private boolean regime = false; // Client - false; Admin - true;
    private final JPanel panel;

    private JButton regimeButton;

    public VisualManager(CoffeeMachine coffeeMachine) {
        panel = new JPanel();
        panel.setLayout(null); // Flexible layout
        clientInterface = new ClientInterface(panel, coffeeMachine, WINDOW_WIDTH, ELEMENT_HEIGHT, ELEMENT_X_OFFSET);
        adminInterface = new AdminInterface(panel, coffeeMachine.getControlPanel(), WINDOW_WIDTH, ELEMENT_HEIGHT, ELEMENT_X_OFFSET);
        passwordPopup = new PasswordPopup();
        initializeUI();
    }

    public void loadGUI() {
        clientInterface.loadClientInterface();
        //adminInterface.loadAdminInterface();
        loadChangeRegimeButton();
        loadFrame();
    }

    public void setOutputText(String text) {
        clientInterface.setOutputText(text);
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

    private void loadChangeRegimeButton() {
        regimeButton = new JButton("Admin Menu");
        regimeButton.setBounds(WINDOW_WIDTH - WINDOW_WIDTH / 5 - ELEMENT_X_OFFSET, WINDOW_HEIGHT - ELEMENT_HEIGHT * 2, WINDOW_WIDTH / 5, ELEMENT_HEIGHT);
        regimeButton.addActionListener(e -> toggleRegime());
        panel.add(regimeButton);
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

    private void clearAfterTimeout() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                clientInterface.setOutputText("");
            }
        }, 5000);
    }
}