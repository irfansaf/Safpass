package com.irfansaf.safpass.io;

import com.irfansaf.safpass.util.CryptUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream to write SafPass file format and provide key for the underlying
 * crypt output stream.
 *
 * @author Irfan Saf
 */
public class SafPassOutputStream extends OutputStream implements SafPassStream {

    private final OutputStream parent;
    private final byte[] generatedKey;

    public SafPassOutputStream(OutputStream parent, char[] key) throws IOException {
        this.parent = parent;

        // get the latest supported file version
        FileVersionType fileVersionType = SUPPORTED_FILE_VERSIONS.get(SUPPORTED_FILE_VERSIONS.lastKey());

        parent.write(FILE_FORMAT_IDENTIFIER);
        parent.write(fileVersionType.getVersion());

        byte[] salt = CryptUtils.generateRandomSalt(fileVersionType.getSaltLength());
        if (salt.length > 0) {
            parent.write(salt);
        }
        this.generatedKey = fileVersionType.getKeyGenerator().apply(key, salt);
    }

    @Override
    public void write(int b) throws IOException {
        parent.write(b);
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }

    @Override
    public byte[] getKey() {
        return generatedKey;
    }
}
