package com.greasy.fighters.gui;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;

import com.greasy.fighters.coffee.machine.ControlPanel;

public class PasswordPopup {
    String password;
    String enteredPassword;
    MessageDigest md;
    ControlPanel controlPanel;

    public PasswordPopup(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        password = controlPanel.getDataHandler().getPassword();
        try {
            md = MessageDigest.getInstance("SHA-256"); // алгоритъм за хеширане SHA-256
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validatePassword() {
        enteredPassword = JOptionPane.showInputDialog(null, "Enter Password", "Authentication", JOptionPane.QUESTION_MESSAGE);
        if (enteredPassword == null) {
            return false;
        }
        String enteredHashedPassword = hashPassword(enteredPassword);
        return enteredHashedPassword.equals(password);
    }

    private String hashPassword(String password) { // хешира паролата
        return bytesToString(md.digest(password.getBytes()));
    }

    private String bytesToString(byte[] bytes) { // превръща байтове в низ
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) { // не знам как бачка това, ама бачка
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
