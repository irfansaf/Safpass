package com.irfansaf.safpass.ui.action;

import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

public abstract class TextComponentAction extends TextAction {

    public TextComponentAction(String text, KeyStroke accelerator, int mnemonic) {
        super(text);
        if (accelerator != null) {
            putValue(ACCELERATOR_KEY,accelerator);
        }
        putValue(MNEMONIC_KEY, mnemonic);
    }

    public abstract boolean isEnabled(JTextComponent component);
}
