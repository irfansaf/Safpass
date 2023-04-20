package com.irfansaf.safpass.data;

import com.irfansaf.safpass.crypt.io.CryptInputStream;
import com.irfansaf.safpass.crypt.io.CryptOutputStream;
import com.irfansaf.safpass.io.SafPassInputStream;
import com.irfansaf.safpass.io.SafPassOutputStream;
import com.irfansaf.safpass.xml.bind.Entries;
import com.irfansaf.safpass.xml.converter.XmlConverter;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.irfansaf.safpass.util.StringUtils.stripString;

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

    /**
     * Key for encryption.
     */
    private final char[] key;

    /**
     * Converter between document objects and streams representing XMLs
     */
    private static final XmlConverter<Entries> CONVERTER = new XmlConverter<>(Entries.class);

    public EntriesRepository(String fileName, final char[] key) {
        this.fileName = fileName;
        this.key = key;
    }

    /**
     * Creates a document repository with no encryption.
     *
     * @param fileName
     * @return a new DocumentHelper object
     */
    public static EntriesRepository newInstance(final String fileName) {
        return new EntriesRepository(fileName, null);
    }

    /**
     * Creates a document repository with encryption
     *
     * @param fileName
     * @param key for encryption
     * @return a new DocumentHelper object
     */
    public static EntriesRepository newInstance(final String fileName, final char[] key) {
        return new EntriesRepository(fileName, key);
    }

    /**
     * Reads and XML file to an {@link Entries} object.
     *
     * @return the document
     * @throws IOException when I/O error occured (including incorrect password
     * or file format issues)
     * @throws DocumentProcessException when document could not be read
     */
    public Entries readDocument() throws IOException, DocumentProcessException {
        InputStream inputStream = null;
        Entries entries;
        try {
            if (this.key == null) {
                inputStream = new BufferedInputStream(new FileInputStream(this.fileName));
            } else {
                inputStream = new GZIPInputStream(new CryptInputStream(new SafPassInputStream(new BufferedInputStream(new FileInputStream(this.fileName)), this.key)));
            }
            entries = CONVERTER.read(inputStream);
        } catch (IOException e) {
            throw new DocumentProcessException(stripString(e.getMessage()));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return entries;
    }

    /**
     * Writes a document into an XML file.
     *
     * @param document the document
     * @throws DocumentProcessException when document could not be saved
     * @throws IOException when document could not be saved
     */
    public void writeDocument(final Entries document) throws DocumentProcessException, IOException {
        OutputStream outputStream = null;
        try {
            if (this.key == null) {
                outputStream = new BufferedOutputStream(new FileOutputStream(this.fileName));
            } else {
                outputStream = new GZIPOutputStream(new CryptOutputStream(new SafPassOutputStream(new BufferedOutputStream(new FileOutputStream(this.fileName)), this.key)));
            }
            CONVERTER.write(document, outputStream);
        } catch (Exception e) {
            throw new DocumentProcessException(stripString(e.getMessage()));
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


}
