package com.irfansaf.safpass.data;

import com.irfansaf.safpass.xml.bind.Entries;

import java.io.IOException;
import java.io.InputStream;

/**
 * Repository class for reading and writing (encrypted) XML documents.
 *
 * @author Irfan Saf
 */
public final class EntriesRepository {

    /**
     * File name to read/write.
     */
    private final String fileName;

    public EntriesRepository(String fileName) {
        this.fileName = fileName;
    }

    public static EntriesRepository newInstance(final String fileName) {
        return new EntriesRepository(fileName);
    }

    public Entries readDocument() throws IOException {
        InputStream inputStream = null;
        Entries entries;
    }
}
