package com.irfansaf.safpass.ui.helper;


import com.irfansaf.safpass.ui.EntryDialog;
import com.irfansaf.safpass.ui.SafPassFrame;
import com.irfansaf.safpass.util.ClipboardUtils;
import com.irfansaf.safpass.xml.bind.Entry;

import static com.irfansaf.safpass.ui.MessageDialog.*;

public final class EntryHelper {

    private EntryHelper() {
        // Not Intended to be Instantiated
    }

    /**
     * Deltes an entry.
     *
     * @param parent component
     */
    public static void deleteEntry(SafPassFrame parent) {
        if (parent.getEntryTitleTable().getSelectedRow() == -1) {
            showWarningMessage(parent, "Please select an entry.");
            return;
        }
        int option = showQuestionMessage(parent, "Do you really want to delete this entry?", YES_NO_OPTION);
        if (option == YES_OPTION) {
            String title = (String) parent.getEntryTitleTable().getValueAt(parent.getEntryTitleTable().getSelectedRow(), 0);
            parent.getModel().getEntries().getEntry().remove(parent.getModel().getEntryByTitle(title));
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(null);
        }
    }

    /**
     * Duplicate entry
     *
     * @param parent component
     */
    public static void duplicateEntry(SafPassFrame parent) {
        if (parent.getEntryTitleTable().getSelectedRow() == -1) {
            showWarningMessage(parent, "Please select an entry.");
            return;
        }
        String title = (String) parent.getEntryTitleTable().getValueAt(parent.getEntryTitleTable().getSelectedRow(), 0);
        Entry originalEntry = parent.getModel().getEntryByTitle(title);
        EntryDialog dialog = new EntryDialog(parent, "Duplicate Entry", originalEntry, true);
        dialog.getModifiedEntry().ifPresent(entry -> {
            parent.getModel().getEntries().getEntry().add(entry);
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(entry.getTitle());
        });
    }

    /**
     * Edits the entry.
     *
     * @param parent component
     */
    public static void editEntry(SafPassFrame parent) {
        if (parent.getEntryTitleTable().getSelectedRow() == -1) {
            showWarningMessage(parent, "Please select an entry.");
            return;
        }
        String title = (String) parent.getEntryTitleTable().getValueAt(parent.getEntryTitleTable().getSelectedRow(), 0);
        Entry originalEntry = parent.getModel().getEntryByTitle(title);
        EntryDialog dialog = new EntryDialog(parent, "Edit Entry", originalEntry, false);
        dialog.getModifiedEntry().ifPresent(entry -> {
            entry.setCreationDate(originalEntry.getCreationDate());
            parent.getModel().getEntries().getEntry().remove(originalEntry);
            parent.getModel().getEntries().getEntry().add(entry);
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(entry.getTitle());
        });
    }

    /**
     * Adds an entry.
     *
     * @param parent component
     */
    public static void addEntry(SafPassFrame parent) {
        EntryDialog dialog = new EntryDialog(parent, "Add New Entry", null, true);
        dialog.getModifiedEntry().ifPresent(entry -> {
            parent.getModel().getEntries().getEntry().add(entry);
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(entry.getTitle());
        });
    }

    /**
     * Gets the selected entry.
     *
     * @param parent component
     * @return the entry or null
     */
    public static Entry getSelectedEntry(SafPassFrame parent) {
        if (parent.getEntryTitleTable().getSelectedRow() == -1) {
            showWarningMessage(parent, "Please select an entry.");
            return null;
        }
        String title = (String) parent.getEntryTitleTable().getValueAt(parent.getEntryTitleTable().getSelectedRow(), 0);
        return parent.getModel().getEntryByTitle(title);
    }

    /**
     * Copy entry field value to clipboard.
     *
     * @param parent component
     * @param content the content to copy
     */
    public static void copyEntryField(SafPassFrame parent, String content) {
        try {
            ClipboardUtils.setClipboardContent(content);
        } catch (Exception e) {
            showErrorMessage(parent, e.getMessage());
        }
    }
}
