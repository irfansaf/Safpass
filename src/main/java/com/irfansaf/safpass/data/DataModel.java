package com.irfansaf.safpass.data;

import com.irfansaf.safpass.xml.bind.Entries;
import com.irfansaf.safpass.xml.bind.Entry;

import java.util.List;
import java.util.stream.Collectors;

public final class DataModel {

    private static DataModel instance;
    private Entries entries = new Entries();
    private String fileName = null;
    private char[] password = null;
    private boolean modified = false;

    private DataModel() {
        // Not Intended to be instantiated
    }

    /**
     * Gets the DataModel singleton instance.
     *
     * @return instance of the DataModel
     */
    public static synchronized DataModel getInstance() {
        if (instance == null) {
            instance = new DataModel();
        }
        return instance;
    }

    /**
     * Gets list of entries.
     *
     * @return list of entries
     */
    public Entries getEntries() {
        return this.entries;
    }

    /**
     * Sets list of entries.
     *
     * @param entries
     */
    public void setEntries(final Entries entries) {
        this.entries = entries;
    }

    /**
     * Gets the filename for the data model.
     *
     * @return filename
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Sets the filename for data model.
     * @param fileName
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the modified state of the data model.
     *
     * @return modified state of the data model
     */
    public boolean isModified() {
        return this.modified;
    }

    /**
     * Sets the modified state of the data model.
     *
     * @param modified state
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    /**
     * Clears all fields of the data model.
     */
    public void clear() {
        this.entries.getEntry().clear();
        this.fileName = null;
        this.password = null;
        this.modified = false;
    }

    /**
     * Gets the list of entry titles.
     *
     * @return list of entry titles
     */
    public List<String> getTitles() {
        return this.entries.getEntry().stream()
                .map(Entry::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Gets entry by title
     *
     * @param title entry title
     * @return entry {@code null} can be null
     */
    public Entry getEntryByTitle(String title) {
        int entryIndex = getEntryIndexByTitle(title);
        if (entryIndex != -1) {
            return this.entries.getEntry().get(entryIndex);
        }
        return null;
    }

    /**
     * Gets entry index by title
     *
     * @param title entry title
     * @return entry index
     */
    private int getEntryIndexByTitle(String title) {
        return getTitles().indexOf(title);
    }




}
