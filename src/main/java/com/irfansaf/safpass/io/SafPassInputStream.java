package com.irfansaf.safpass.io;

import com.irfansaf.safpass.ui.SafPassFrame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Input stream to read SafPass file format and provide key for the underlying
 * crypt input stream.
 *
 * @author Irfan Saf
 */
public class SafPassInputStream extends InputStream implements SafPassStream {
    private final InputStream parent;
    private final byte[] generatedKey;

    public SafPassInputStream(InputStream parent, char[] key) throws IOException {
        this.parent = parent;
        if ((this).markSupported()) {
            this.parent.mark(FILE_FORMAT_IDENTIFIER.length + 1);
        }
        byte[] identifier = readBytes(parent, FILE_FORMAT_IDENTIFIER.length);
        int fileVersion = parent.read();

        if (!Arrays.equals(FILE_FORMAT_IDENTIFIER, identifier) && this.parent.markSupported()) {
            // Initial version of SafPass had no file Identifier, We assume version 0
            fileVersion = 0;
            this.parent.reset();
        }

        FileVersionType fileVersionType = Objects.requireNonNull(SUPPORTED_FILE_VERSIONS.get(fileVersion),
                "Unsupported file version: " + fileVersion);

        byte[] salt = readBytes(parent, fileVersionType.getSaltLength());
        this.generatedKey = fileVersionType.getKeyGenerator().apply(key, salt);
    }

    @Override
    public byte[] getKey() {
        return generatedKey;
    }

    @Override
    public int read() throws IOException {
        return parent.read();
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }

    private byte[] readBytes(InputStream stream, int length) throws IOException {
        byte[] result = new byte[length];
        int bytesRead = 0;
        while (length > 0 && bytesRead < length) {
            int cur = stream.read(result, bytesRead, length - bytesRead);
            if (cur < 0) {
                throw new IndexOutOfBoundsException("Invalid file format");
            }
            bytesRead += cur;
        }
        return result;
    }
}
