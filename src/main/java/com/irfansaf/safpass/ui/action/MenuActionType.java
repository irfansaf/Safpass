package com.irfansaf.safpass.ui.action;

import com.irfansaf.safpass.ui.GeneratePasswordDialog;
import com.irfansaf.safpass.ui.MessageDialog;
import com.irfansaf.safpass.ui.SafPassFrame;
import com.irfansaf.safpass.ui.helper.EntryHelper;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.security.Key;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import static com.irfansaf.safpass.ui.MessageDialog.getIcon;
import static com.irfansaf.safpass.ui.helper.FileHelper.*;
import static javax.swing.KeyStroke.getKeyStroke;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.ALT_DOWN_MASK;

public enum MenuActionType {
    NEW_FILE(new AbstractMenuAction("New", getIcon("new"), getKeyStroke(KeyEvent.VK_N, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            createNew(SafPassFrame.getInstance());
        }
    }),
    OPEN_FILE(new AbstractMenuAction("Open File...", getIcon("open"), getKeyStroke(KeyEvent.VK_O, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            openFile(SafPassFrame.getInstance());
        }
    }),
    SAVE_FILE(new AbstractMenuAction("Save", getIcon("save"), getKeyStroke(KeyEvent.VK_S, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            saveFile(SafPassFrame.getInstance(), false);
        }
    }),
    SAVE_AS_FILE(new AbstractMenuAction("Save As...", getIcon("save_as"), null) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            saveFile(SafPassFrame.getInstance(), true);
        }
    }),
    EXPORT_XML(new AbstractMenuAction("Export to XML...", getIcon("export"), null) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            exportFile(SafPassFrame.getInstance());
        }
    }),
    IMPORT_XML(new AbstractMenuAction("Import from XML...", getIcon("import"), null) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            importFile(SafPassFrame.getInstance());
        }
    }),
    CHANGE_PASSWORD(new AbstractMenuAction("Change Password...", getIcon("lock"), null) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            SafPassFrame parent = SafPassFrame.getInstance();
            char[] password = MessageDialog.showPasswordDialog(parent, true);
            if (password == null) {
                MessageDialog.showInformationMessage(parent, "Password has not been modified.");
            } else {
                parent.getModel().setPassword(password);
                parent.getModel().setModified(true);
                parent.refreshFrameTitle();
                MessageDialog.showInformationMessage(parent,
                        "Password has been successfully modified.\n\nSave the file now in order to\nget the new password applied.");
            }
        }
    }),
    GENERATE_PASSWORD(new AbstractMenuAction("Generate Password...", getIcon("generate"), getKeyStroke(KeyEvent.VK_Z, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            new GeneratePasswordDialog(SafPassFrame.getInstance());
        }
    }),
    EXIT(new AbstractMenuAction("Exit", getIcon("exit"), getKeyStroke(KeyEvent.VK_F4, ALT_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            SafPassFrame.getInstance().exitFrame();
        }
    }),
    ABOUT(new AbstractMenuAction("About SafPass...", getIcon("info"), getKeyStroke(KeyEvent.VK_F1, 0)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<b>" + SafPassFrame.PROGRAM_NAME + "</b>\n");
            stringBuilder.append("version: " + SafPassFrame.PROGRAM_VERSION + "\n");
            stringBuilder.append("Copyright &copy; 2023 Irfan Saf\n");
            stringBuilder.append("\n");
            stringBuilder.append("Java version: ").append(System.getProperties().getProperty("java.version")).append("\n");
            stringBuilder.append(System.getProperties().getProperty("java.vendor"));
            MessageDialog.showInformationMessage(SafPassFrame.getInstance(), stringBuilder.toString());
        }
    }),
    LICENSE(new AbstractMenuAction("License", getIcon("license"),null) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            MessageDialog.showTextFile(SafPassFrame.getInstance(), "License", "license.txt");
        }
    });


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

    public static void bindAllActions(JComponent component) {
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
