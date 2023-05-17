package com.irfansaf.safpass.ui;

import javax.swing.*;

import com.irfansaf.safpass.ui.action.TextComponentActionType;
import com.irfansaf.safpass.ui.action.TextComponentPopupListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class TextComponentFactory {
    private TextComponentFactory() {
        // not intended to be instantiated
    }

    /**
     * Creates a new {@link JTextField} instance with a context pop-up menu by
     * default.
     *
     * @return the new instance
     */
    public static JTextField newTextField() {
        return newTextField(null);
    }

    /**
     * Creates a new {@link JTextField} instance with a context pop-up menu by
     * default.
     *
     * @param text the initial text
     * @return the new instance
     */
    public static JTextField newTextField(String text) {
        JTextField textField = text == null ? new JTextField() : new JTextField(text);
        textField.addMouseListener(new TextComponentPopupListener());
        TextComponentActionType.bindAllActions(textField);
        return textField;
    }

    /**
     * Creates a new {@link JPasswordField} instance with a context pop-up menu
     * by default.
     *
     * @return the new instance
     */
    public static JPasswordField newPasswordField() {
        return newPasswordField(false);
    }

    /**
     * Creates a new {@link JPasswordField} instance with a context pop-up menu
     * by default.
     *
     * @param copyEnabled forces the copy of password field content to clipboard
     * @return the new instance
     */
    public static JPasswordField newPasswordField(boolean copyEnabled) {
        JPasswordField passwordField = new CopiablePasswordField(copyEnabled);
        passwordField.addMouseListener(new TextComponentPopupListener());
        TextComponentActionType.bindAllActions(passwordField);
        return passwordField;
    }

    /**
     * Creates a new {@link JTextArea} instance with a context pop-up menu by
     * default.
     *
     * @return the new instance
     */
    public static JTextArea newTextArea() {
        return newTextArea(null);
    }

    /**
     * Creates a new {@link JTextArea} instance with a context pop-up menu by
     * default.
     *
     * @param text the initial text
     * @return the new instance
     */
    public static JTextArea newTextArea(String text) {
        JTextArea textArea = text == null ? new JTextArea() : new JTextArea(text);
        textArea.addMouseListener(new TextComponentPopupListener());
        TextComponentActionType.bindAllActions(textArea);
        return textArea;
    }

    public static JComboBox<String> newURLDropdown() {
        String[] predefinedURLs = { "Twitter.com", "Instagram.com", "Facebook.com", "Gmail.com", "Custom" };

        JComboBox<String> urlDropdown = new JComboBox<>(predefinedURLs);

        urlDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> comboBox = (JComboBox<String>) e.getSource();

                if ("Custom".equals(comboBox.getSelectedItem())) {
                    String customURL = JOptionPane.showInputDialog(comboBox, "Enter custom URL:");

                    if (customURL != null && !customURL.trim().isEmpty()) {
                        comboBox.addItem(customURL);
                        comboBox.setSelectedItem(customURL);
                    }
                }
            }
        });

        return urlDropdown;
    }


}
