package com.irfansaf.safpass.ui.action;

import com.irfansaf.safpass.ui.EntryDialog;
import com.irfansaf.safpass.ui.GeneratePasswordDialog;
import com.irfansaf.safpass.ui.MessageDialog;
import com.irfansaf.safpass.ui.SafPassFrame;
import com.irfansaf.safpass.ui.helper.EntryHelper;
import com.irfansaf.safpass.xml.bind.Entry;

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
    }),
    ADD_ENTRY(new AbstractMenuAction("Add Entry...", getIcon("entry_new"), getKeyStroke(KeyEvent.VK_Y, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.addEntry(SafPassFrame.getInstance());
        }
    }),
    EDIT_ENTRY(new AbstractMenuAction("Edit Entry...", getIcon("entry_edit"), getKeyStroke(KeyEvent.VK_E, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.editEntry(SafPassFrame.getInstance());
        }
    }),
    DUPLICATE_ENTRY(new AbstractMenuAction("Duplicate Entry...", getIcon("entry_duplicate"), getKeyStroke(KeyEvent.VK_K, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.duplicateEntry(SafPassFrame.getInstance());
        }
    }),
    DELETE_ENTRY(new AbstractMenuAction("Delete Entry...", getIcon("entry_delete"), getKeyStroke(KeyEvent.VK_D, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.deleteEntry(SafPassFrame.getInstance());
        }
    }),
    COPY_URL(new AbstractMenuAction("Copy URL", getIcon("url"), getKeyStroke(KeyEvent.VK_U, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            SafPassFrame parent = SafPassFrame.getInstance();
            Entry entry = EntryHelper.getSelectedEntry(parent);
            if (entry != null) {
                EntryHelper.copyEntryField(parent, entry.getUrl());
            }
        }
    }),
    COPY_USER(new AbstractMenuAction("Copy User Name", getIcon("user"), getKeyStroke(KeyEvent.VK_B, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            SafPassFrame parent = SafPassFrame.getInstance();
            Entry entry = EntryHelper.getSelectedEntry(parent);
            if (entry != null) {
                EntryHelper.copyEntryField(parent, entry.getUrl());
            }
        }
    }),
    COPY_PASSWORD(new AbstractMenuAction("Copy Password", getIcon("keyring"), getKeyStroke(KeyEvent.VK_C, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            SafPassFrame parent = SafPassFrame.getInstance();
            Entry entry = EntryHelper.getSelectedEntry(parent);
            if (entry != null) {
                EntryHelper.copyEntryField(parent, entry.getPassword());
            }
        }
    }),
    CLEAR_CLIPBOARD(new AbstractMenuAction("Clear Clipboard", getIcon("clear"), getKeyStroke(KeyEvent.VK_X, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.copyEntryField(SafPassFrame.getInstance(), null);
        }
    }),
    FIND_ENTRY(new AbstractMenuAction("Find Entry", getIcon("find"), getKeyStroke(KeyEvent.VK_F, CTRL_DOWN_MASK)) {
        @Override
        public void actionPerformed(ActionEvent ev) {
            SafPassFrame.getInstance().getSearchPanel().setVisible(true);
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
