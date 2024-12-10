package com.greasy.fighters.gui;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;

import com.greasy.fighters.data.handler.DataHandler;

public class PasswordPopup {
    String password;
    String enteredPassword;
    MessageDigest md;

    public PasswordPopup() {
        password = DataHandler.getPassword();

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

        // System.out.println(hashedPasswordInput + "\n" + password);

        return enteredHashedPassword.equals(password);
    }

    public String hashPassword(String password) { // хешира паролата
        byte[] hashedPasswordBytes = md.digest(password.getBytes());

        String hashedPassword = bytesToString(hashedPasswordBytes);

        // System.out.println(hashedPassword);

        return hashedPassword;
    }

    String bytesToString(byte[] bytes) { // превръща байтове в низ
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
