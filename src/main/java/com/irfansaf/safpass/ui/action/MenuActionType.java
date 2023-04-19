package com.irfansaf.safpass.ui.action;

import com.irfansaf.safpass.Safpass;
import com.irfansaf.safpass.ui.SafPassFrame;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static com.irfansaf.safpass.ui.MessageDialog.getIcon;
import static javax.swing.KeyStroke.getKeyStroke;

public enum MenuActionType {
    NEW_FILE(new AbstractMenuAction("New", getIcon("new"), getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            createNew(SafPassFrame.getInstance());
        }
    })


    private final String name;
    private final AbstractMenuAction action;

    MenuActionType(AbstractMenuAction action) {
        this.name = String.format("safpass.menu.%s_action", this.name().toLowerCase());
        this.action = action;
    }

    public String getName() {
        return this.name();
    }

    public AbstractMenuAction getAction() {
        return this.action;
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) this.action.getValue(Action.ACCELERATOR_KEY);
    }

    public static void bindAllActtions(JComponent component) {
        ActionMap actionMap = component.getActionMap();
        InputMap inputMap = component.getInputMap();
        for (MenuActionType type : values()) {
            actionMap.put(type.getName(), type.getAction());
            KeyStroke acc = type.getAccelerator();
            if (acc != null) {
                inputMap.put(type.getAccelerator(), type.getName());
            }
        }
    }
}
