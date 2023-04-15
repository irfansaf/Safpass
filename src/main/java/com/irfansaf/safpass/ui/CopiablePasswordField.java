package com.irfansaf.safpass.ui;

import javax.swing.JPasswordField;

public class CopiablePasswordField extends JPasswordField {

    private final boolean copyEnabled;

    public CopiablePasswordField(boolean copyEnabled) {
        super();
        this.copyEnabled = copyEnabled;
    }

    public boolean isCopyEnabled() { return this.copyEnabled; }
}
