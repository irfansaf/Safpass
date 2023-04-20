package com.irfansaf.safpass.ui;

import com.irfansaf.safpass.data.DataModel;
import com.irfansaf.safpass.ui.action.CloseListener;
import com.irfansaf.safpass.ui.action.MenuActionType;
import com.irfansaf.safpass.ui.helper.EntryHelper;
import com.irfansaf.safpass.ui.helper.FileHelper;
import com.irfansaf.safpass.util.Configuration;
import com.irfansaf.safpass.xml.bind.Entry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import static com.irfansaf.safpass.ui.MessageDialog.NO_OPTION;
import static com.irfansaf.safpass.ui.MessageDialog.YES_NO_CANCEL_OPTION;
import static com.irfansaf.safpass.ui.MessageDialog.YES_OPTION;
import static com.irfansaf.safpass.ui.MessageDialog.getIcon;
import static com.irfansaf.safpass.ui.MessageDialog.showQuestionMessage;

public final class SafPassFrame extends JFrame {
    private static final Logger LOG = Logger.getLogger(SafPassFrame.class.getName());

    private static SafPassFrame instance;
    public static final String PROGRAM_NAME = "Safpass Password Manager";
    public static final String PROGRAM_VERSION = "1.0.0-SNAPSHOT";

    private final JPanel topContainerPanel;
    private final JMenuBar safpassMenuBar;
    private final JMenu fileMenu;
    private final JMenu editMenu;
    private final JMenu toolsMenu;
    private final JMenu helpMenu;
    private final JScrollPane scrollPane;
    private JToolBar toolBar;
    private final SearchPanel searchPanel;
    private volatile boolean processing = false;
    private final EntryDetailsTable  entryDetailsTable;
    private final StatusPanel statusPanel;
    private JPopupMenu popup;
    private DataModel model = DataModel.getInstance();

    private SafPassFrame(String fileName) {
        try {
            setIconImages(Stream.of(16, 20, 32, 40, 64, 80, 128, 160)
                    .map(size -> getIcon("safpass", size, size).getImage())
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            LOG.log(Level.CONFIG, "Could not set application icon.", e);
        }

        this.toolBar = new JToolBar();
        this.toolBar.setFloatable(true);
        this.toolBar.add(MenuActionType.NEW_FILE.getAction());
        this.toolBar.add(MenuActionType.OPEN_FILE.getAction());
        this.toolBar.add(MenuActionType.SAVE_FILE.getAction());
        this.toolBar.addSeparator();
        this.toolBar.add(MenuActionType.ABOUT.getAction());
        this.toolBar.add(MenuActionType.EXIT.getAction());

        this.searchPanel = new SearchPanel(enabled -> {
            if (enabled) {
                refreshEntryTitleList(null);
            }
        });

        this.topContainerPanel = new JPanel(new BorderLayout());
        this.topContainerPanel.add(this.toolBar, BorderLayout.NORTH);
        this.topContainerPanel.add(this.searchPanel, BorderLayout.SOUTH);

        this.safpassMenuBar = new JMenuBar();

        this.fileMenu = new JMenu("File");
        this.fileMenu.setMnemonic(KeyEvent.VK_F);
        this.fileMenu.add(MenuActionType.NEW_FILE.getAction());
        this.fileMenu.add(MenuActionType.OPEN_FILE.getAction());
        this.fileMenu.add(MenuActionType.SAVE_FILE.getAction());
        this.fileMenu.add(MenuActionType.SAVE_AS_FILE.getAction());
        this.fileMenu.addSeparator();
        this.fileMenu.add(MenuActionType.EXPORT_XML.getAction());
        this.fileMenu.add(MenuActionType.IMPORT_XML.getAction());
        this.fileMenu.addSeparator();
        this.fileMenu.add(MenuActionType.CHANGE_PASSWORD.getAction());
        this.fileMenu.addSeparator();
        this.fileMenu.add(MenuActionType.EXIT.getAction());
        this.safpassMenuBar.add(this.fileMenu);

        this.editMenu = new JMenu("Edit");
        this.editMenu.setMnemonic(KeyEvent.VK_E);
        this.editMenu.add(MenuActionType.ADD_ENTRY.getAction());
        this.editMenu.add(MenuActionType.EDIT_ENTRY.getAction());
        this.editMenu.add(MenuActionType.DUPLICATE_ENTRY.getAction());
        this.editMenu.add(MenuActionType.DELETE_ENTRY.getAction());
        this.editMenu.addSeparator();
        this.editMenu.add(MenuActionType.COPY_URL.getAction());
        this.editMenu.add(MenuActionType.COPY_USER.getAction());
        this.editMenu.add(MenuActionType.COPY_PASSWORD.getAction());
        this.editMenu.addSeparator();
        this.editMenu.add(MenuActionType.FIND_ENTRY.getAction());
        this.safpassMenuBar.add(this.editMenu);

        this.toolsMenu = new JMenu("Tools");
        this.toolsMenu.setMnemonic(KeyEvent.VK_T);
        this.toolsMenu.add(MenuActionType.GENERATE_PASSWORD.getAction());
        this.safpassMenuBar.add(this.toolsMenu);

        this.helpMenu = new JMenu("Help");
        this.helpMenu.setMnemonic(KeyEvent.VK_H);
        this.helpMenu.add(MenuActionType.LICENSE.getAction());
        this.helpMenu.addSeparator();
        this.helpMenu.add(MenuActionType.ABOUT.getAction());
        this.safpassMenuBar.add(this.helpMenu);

        this.popup = new JPopupMenu();
        this.popup.add(MenuActionType.ADD_ENTRY.getAction());
        this.popup.add(MenuActionType.EDIT_ENTRY.getAction());
        this.popup.add(MenuActionType.DUPLICATE_ENTRY.getAction());
        this.popup.add(MenuActionType.DELETE_ENTRY.getAction());
        this.popup.addSeparator();
        this.popup.add(MenuActionType.COPY_URL.getAction());
        this.popup.add(MenuActionType.COPY_USER.getAction());
        this.popup.add(MenuActionType.COPY_PASSWORD.getAction());
        this.popup.addSeparator();
        this.popup.add(MenuActionType.FIND_ENTRY.getAction());

        this.entryDetailsTable = new EntryDetailsTable();
        this.scrollPane = new JScrollPane(this.entryDetailsTable);
        MenuActionType.bindAllActions(this.entryDetailsTable);

        this.statusPanel = new StatusPanel();

        refreshAll();

        getContentPane().add(this.topContainerPanel, BorderLayout.NORTH);
        getContentPane().add(this.scrollPane, BorderLayout.CENTER);
        getContentPane().add(this.statusPanel, BorderLayout.SOUTH);

        setJMenuBar(this.safpassMenuBar);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(450, 400);
        setMinimumSize(new Dimension(420, 200));
        addWindowListener(new CloseListener());
        setLocationRelativeTo(null);
        setVisible(true);
        FileHelper.openFileInBackground(fileName, this);

        // Set focus on the list for easier keyboard navigation
        this.entryDetailsTable.requestFocusInWindow();
    }

    public static SafPassFrame getInstance() {
        return getInstance(null);
    }

    public static synchronized SafPassFrame getInstance(String fileName) {
        if (instance == null) {
            instance = new SafPassFrame(fileName);
        }
        return instance;
    }

    /**
     * Gets the entry title list.
     *
     * @return entry title list
     */
    public JTable getEntryTitleTable() {
        return this.entryDetailsTable;
    }

    /**
     * Gets the data model of this frame.
     *
     * @return data model
     */
    public DataModel getModel() {
        return this.model;
    }

    /**
     * Clears data model.
     */
    public void clearModel() {
        this.model.clear();
        this.entryDetailsTable.clear();
    }

    /**
     * Refresh frame title based on data model.
     */
    public void refreshFrameTitle() {
        setTitle((getModel().isModified() ? "*" : "")
                + (getModel().getFileName() == null ? "Untitled" : getModel().getFileName()) + " - "
                + PROGRAM_NAME);
    }

    /**
     * Refresh the entry titles based on data model.
     *
     * @param selectTitle title to select, or {@code null} if nothing to select
     */
    public void refreshEntryTitleList(String selectTitle) {
        this.entryDetailsTable.clear();
        List<Entry> entries = new ArrayList<>(this.model.getEntries().getEntry());
        Collections.sort(entries, Comparator.comparing(Entry::getTitle, String.CASE_INSENSITIVE_ORDER));
        String searchCriteria = this.searchPanel.getSearhCriteria();
        entries.stream()
                .filter(entry -> searchCriteria.isEmpty() || entry.getTitle().toLowerCase().contains(searchCriteria.toLowerCase()))
                .forEach(this.entryDetailsTable::addRow);

        if (selectTitle != null) {
            int rowCount = this.entryDetailsTable.getModel().getRowCount();
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                String title = String.valueOf(this.entryDetailsTable.getModel());
                if (selectTitle.equals(title)) {
                    this.entryDetailsTable.setRowSelectionInterval(rowIndex, rowIndex);
                    break;
                }
            }

            if (searchCriteria.isEmpty()) {
                this.statusPanel.setText("Entries count: " + entries.size());
            } else {
                this.statusPanel.setText("Entries found: " + this.entryDetailsTable.getRowCount() + " / " + entries.size());
            }
        }
    }

    /**
     * Refresh frame title and entry list.
     */
    public void refreshAll() {
        refreshFrameTitle();
        refreshEntryTitleList(null);
    }

    /**
     * Exit the application
     */
    public void exitFrame() {
        if (Configuration.getInstance().is("clear.clipboard.on.exit.enabled", false)) {
            EntryHelper.copyEntryField(this, null);
        }
        if (this.processing) {
            return;
        }
        if (this.model.isModified()) {
            int option = showQuestionMessage(this, FileHelper.SAVE_MODIFIED_QUESTION_MESSAGE, YES_NO_CANCEL_OPTION);
            if (option == YES_OPTION) {
                FileHelper.saveFile(this,false, () -> System.exit(0));
                return;
            } else if (option != NO_OPTION) {
                return;
            }
        }
        System.exit(0);
    }

    public JPopupMenu getPopup() {
        return this.popup;
    }

    /**
     * Sets the processing state of this frame.
     *
     * @param processing state
     */
    public void setProcessing(boolean processing) {
        this.processing = processing;
        for (MenuActionType actionType : MenuActionType.values()) {
            actionType.getAction().setEnabled(!processing);
        }
        this.searchPanel.setEnabled(!processing);
        this.entryDetailsTable.setEnabled(!processing);
        this.statusPanel.setProcessing(processing);
    }

    public boolean isProcessing() {
        return this.processing;
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }
}
