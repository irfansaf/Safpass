package com.irfansaf.safpass.ui;

import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.irfansaf.safpass.ui.action.TextComponentActionType;
import com.irfansaf.safpass.ui.action.TextComponentPopupListener;

public final class TextComponentFactory {
    private TextComponentFactory() {
        // not intended to be instantiated
    }

    public static JTextField newTextField() {
        return newTextField(null);
    }

    /**
     * Creates a new {@link JTextField} instance with a context pop-up menu by default
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
}
