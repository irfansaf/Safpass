package com.irfansaf.safpass.ui;

import com.irfansaf.safpass.Safpass;
import com.irfansaf.safpass.util.SpringUtilities;
import com.irfansaf.safpass.util.StringUtils;
import com.irfansaf.safpass.xml.bind.Entry;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static com.irfansaf.safpass.ui.helper.EntryHelper.copyEntryField;

/**
 * A dialog with the entry data.
 *
 * @author Irfan Saf
 *
 */
public class EntryDialog extends JDialog implements ActionListener {

    private static final char NULL_ECHO = '\0';

    private final JPanel fieldPanel;
    private final JPanel notesPanel;
    private final JPanel buttonPanel;
    private final JPanel passwordButtonPanel;

    private final JTextField titleField;
    private final JTextField userField;
    private final JPasswordField passwordField;
    private final JPasswordField repeatField;
    private final JComboBox<String> urlField;
    private final JTextArea notesField;

    private final JButton okButton;
    private final JButton cancelButton;
    private final JToggleButton showButton;
    private final JButton generateButton;
    private final JButton copyButton;

    private final char originalEcho;

    private Entry modifiedEntry;
    private final boolean newEntry;
    private String originalTitle;

    /**
     * Creates a new EntryDialog instance.
     *
     * @param parent component
     * @param title dialog title
     * @param entry the entry to fill form data, can be null
     * @param newEntry new entry marker
     */
    public EntryDialog(SafPassFrame parent, String title, Entry entry, boolean newEntry) {
        super(parent, title, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.newEntry = newEntry;
        this.modifiedEntry = null;

        this.titleField = TextComponentFactory.newTextField();
        this.urlField = TextComponentFactory.newURLDropdown();
        this.userField = TextComponentFactory.newTextField();
        this.passwordField = TextComponentFactory.newPasswordField(true);
        this.originalEcho = this.passwordField.getEchoChar();
        this.repeatField = TextComponentFactory.newPasswordField(true);

        this.showButton = new JToggleButton("Show", MessageDialog.getIcon("show"));
        this.showButton.setActionCommand("show_button");
        this.showButton.setMnemonic(KeyEvent.VK_S);
        this.showButton.addActionListener(this);
        this.generateButton = new JButton("Generate", MessageDialog.getIcon("generate"));
        this.generateButton.setActionCommand("generate_button");
        this.generateButton.setMnemonic(KeyEvent.VK_G);
        this.generateButton.addActionListener(this);
        this.copyButton = new JButton("Copy", MessageDialog.getIcon("keyring"));
        this.copyButton.setActionCommand("copy_button");
        this.copyButton.setMnemonic(KeyEvent.VK_P);
        this.copyButton.addActionListener(this);

        this.passwordButtonPanel = new JPanel(new SpringLayout());
        this.passwordButtonPanel.add(this.showButton);
        this.passwordButtonPanel.add(this.generateButton);
        this.passwordButtonPanel.add(this.copyButton);
        SpringUtilities.makeCompactGrid(this.passwordButtonPanel,
                1, 3, // rows, columns
                0, 0, // initX, initY
                5, 0); // xPad, yPad

        this.fieldPanel = new JPanel(new SpringLayout());
        this.fieldPanel.add(new JLabel("Title:"));
        this.fieldPanel.add(this.titleField);
        this.fieldPanel.add(new JLabel("URL:"));
        this.fieldPanel.add(this.urlField);
        this.fieldPanel.add(new JLabel("User name:"));
        this.fieldPanel.add(this.userField);
        this.fieldPanel.add(new JLabel("Password:"));
        this.fieldPanel.add(this.passwordField);
        this.fieldPanel.add(new JLabel("Repeat:"));
        this.fieldPanel.add(this.repeatField);
        this.fieldPanel.add(new JLabel(""));
        this.fieldPanel.add(this.passwordButtonPanel);
        SpringUtilities.makeCompactGrid(this.fieldPanel,
                6, 2, // rows, columns
                5, 5, // initX, initY
                5, 5); // xPad, yPad

        this.notesField = TextComponentFactory.newTextArea();
        this.notesField.setFont(TextComponentFactory.newTextField().getFont());
        this.notesField.setLineWrap(true);
        this.notesField.setWrapStyleWord(true);

        this.notesPanel = new JPanel(new BorderLayout(5, 5));
        this.notesPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        this.notesPanel.add(new JLabel("Notes:"), BorderLayout.NORTH);
        this.notesPanel.add(new JScrollPane(this.notesField), BorderLayout.CENTER);

        this.okButton = new JButton("OK", MessageDialog.getIcon("accept"));
        this.okButton.setActionCommand("ok_button");
        this.okButton.setMnemonic(KeyEvent.VK_O);
        this.okButton.addActionListener(this);

        this.cancelButton = new JButton("Cancel", MessageDialog.getIcon("cancel"));
        this.cancelButton.setActionCommand("cancel_button");
        this.cancelButton.setMnemonic(KeyEvent.VK_C);
        this.cancelButton.addActionListener(this);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.buttonPanel.add(this.okButton);
        this.buttonPanel.add(this.cancelButton);

        getContentPane().add(this.fieldPanel, BorderLayout.NORTH);
        getContentPane().add(this.notesPanel, BorderLayout.CENTER);
        getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);

        fillDialogFromEntry(entry);
        setSize(450, 400);
        setMinimumSize(new Dimension(370, 300));
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("show_button".equals(command)) {
            this.passwordField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.originalEcho);
            this.repeatField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.originalEcho);
        } else if ("ok_button".equals(command)) {
            if (this.titleField.getText().trim().isEmpty()) {
                MessageDialog.showWarningMessage(this, "Please fill the title field.");
                return;
            } else if (!checkEntryTitle()) {
                MessageDialog.showWarningMessage(this, "Title is already exists,\nplease enter a different title.");
                return;
            } else if (!Arrays.equals(this.passwordField.getPassword(), this.repeatField.getPassword())) {
                MessageDialog.showWarningMessage(this, "Password and repeated passwords are not identical.");
                return;
            }
            this.modifiedEntry = getEntryFromDialog();
            dispose();
        } else if ("cancel_button".equals(command)) {
            dispose();
        } else if ("generate_button".equals(command)) {
            GeneratePasswordDialog gpd = new GeneratePasswordDialog(this);
            gpd.getGeneratedPassword()
                    .filter(password -> password != null && !password.isEmpty())
                    .ifPresent(password -> {
                        this.passwordField.setText(password);
                        this.repeatField.setText(password);
                    });
        } else if ("copy_button".equals(command)) {
            copyEntryField(SafPassFrame.getInstance(), String.valueOf(this.passwordField.getPassword()));
        }
    }

    /**
     * Retrieves the form data.
     *
     * @return an entry
     */
    private Entry getEntryFromDialog() {
        Entry entry = new Entry();

        String title = StringUtils.stripNonValidXMLCharacters(this.titleField.getText());
        String user = StringUtils.stripNonValidXMLCharacters(this.userField.getText());
        String password = StringUtils.stripNonValidXMLCharacters(String.valueOf(this.passwordField.getPassword()));
        String url = null;
        if (this.urlField.getSelectedItem() instanceof String) {
            String selectedURL = (String) this.urlField.getSelectedItem();
            if (!"Custom".equals(selectedURL)) {
                url = StringUtils.stripNonValidXMLCharacters(selectedURL);
            } else {
                String customURL = JOptionPane.showInputDialog(this.urlField, "Enter Custom URL:");
                if (customURL != null && !customURL.trim().isEmpty()) {
                    url = StringUtils.stripNonValidXMLCharacters(customURL);
                }
            }
        }
        String notes = StringUtils.stripNonValidXMLCharacters(this.notesField.getText());

        entry.setTitle(title == null || title.isEmpty() ? null : title);
        entry.setUser(user == null || user.isEmpty() ? null : user);
        entry.setPassword(password == null || password.isEmpty() ? null : password);
        entry.setUrl(url == null || url.isEmpty() ? null : url);
        entry.setNotes(notes == null || notes.isEmpty() ? null : notes);

        return entry;
    }

    public Optional<Entry> getModifiedEntry() {
        return Optional.ofNullable(this.modifiedEntry);
    }

    /**
     * Gets the form data (entry) of this dialog.
     *
     * @return nonempty form data if the 'OK' button is pressed, otherwise
     * an empty data
     */
    private boolean checkEntryTitle() {
        boolean titleIsOk = true;
        SafPassFrame parent = SafPassFrame.getInstance();
        String currentTitleText = StringUtils.stripNonValidXMLCharacters(this.titleField.getText());
        if (currentTitleText == null) {
            currentTitleText = "";
        }
        if (this.newEntry || !currentTitleText.equalsIgnoreCase(this.originalTitle)) {
            for (Entry entry : parent.getModel().getEntries().getEntry()) {
                if (currentTitleText.equalsIgnoreCase(entry.getTitle())) {
                    titleIsOk = false;
                    break;
                }
            }
        }
        return titleIsOk;
    }

    private void fillDialogFromEntry(Entry entry) {
        if (entry == null) {
            return;
        }
        this.originalTitle = entry.getTitle() == null ? "" : entry.getTitle();
        this.titleField.setText(this.originalTitle + (this.newEntry ? " (copy)" : ""));
        this.userField.setText(entry.getUser() == null ? "" : entry.getUser());
        this.passwordField.setText(entry.getPassword() == null ? "" : entry.getPassword());
        this.repeatField.setText(entry.getPassword() == null ? "" : entry.getPassword());

        if (entry.getUrl() == null) {
            this.urlField.setSelectedItem("");
        } else {
            int index = -1;
            String url = entry.getUrl();
            for (int i = 0; i < this.urlField.getItemCount(); i++) {
                if (url.equals(this.urlField.getItemAt(i))) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                this.urlField.setSelectedItem(index);
            } else {
                this.urlField.setSelectedItem("Custom");
                this.urlField.addItem(url);
            }
        }

        this.notesField.setText(entry.getNotes() == null ? "" : entry.getNotes());
        this.notesField.setCaretPosition(0);
    }
}
