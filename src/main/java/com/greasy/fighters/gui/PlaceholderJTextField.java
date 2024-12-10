package com.greasy.fighters.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

// Клас за текстово поле с поддържан placeholder текст
class PlaceholderJTextField extends JTextField {
    // Строка, която представлява placeholder текста
    private final String placeholder;
    // Променлива, указваща дали показваме placeholder
    private boolean showingPlaceholder;

    // Конструктор, който приема текста на placeholder
    public PlaceholderJTextField(String placeholder) {
        super(); // Извикване на родителския конструктор
        this.showingPlaceholder = true; // Задаване на начален статус за показване на placeholder
        this.placeholder = placeholder; // Инициализиране на placeholder текста
        setForeground(Color.GRAY); // Задаване на цвят на текста на сив
        setText(placeholder); // Поставяне на placeholder текста в полето

        // Добавяне на слушател за фокус
        addFocusListener(new FocusAdapter() {
            // Метод, извикван при получаване на фокус
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) { // Проверка дали показваме placeholder
                    setText(""); // Изчистване на текста
                    setForeground(Color.BLACK); // Задаване на черен цвят на текста
                    showingPlaceholder = false; // Обновяване на статус
                }
            }

            // Метод, извикван при загуба на фокус
            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) { // Проверка дали полето е празно
                    setForeground(Color.GRAY); // Задаване на сив цвят на текста
                    setText(placeholder); // Връщане на placeholder текста
                    showingPlaceholder = true; // Обновяване на статус
                }
            }
        });
    }

    // Метод за нулиране на полето
    public void reset() {
        this.showingPlaceholder = true; // Задаване на статус за показване на placeholder
        setForeground(Color.GRAY); // Задаване на сив цвят на текста
        setText(placeholder); // Поставяне на placeholder текста
    }
}
