package com.irfansaf.safpass.crypt.io;

import com.irfansaf.safpass.crypt.CBC;
import com.irfansaf.safpass.io.SafPassOutputStream;
import com.irfansaf.safpass.util.CryptUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class CryptOutputStream extends OutputStream {

    /**
     * Cipher.
     */
    private final CBC _cipher;

    /**
     * Buffer for sending single {@code byte}s.
     */
    private final byte[] _buffer = new byte[1];

    /**
     * Initializes the cipher with the given JPass stream.
     *
     * @param parent underlying {@link java.io.OutputStream}
     * @throws IOException if file header values can't be written to the
     * underlying stream
     */
    public CryptOutputStream(SafPassOutputStream parent) throws IOException {
        this(parent, parent.getKey());
    }

    /**
     * Initializes the cipher with the given key and initial values.
     *
     * @param parent underlying {@link java.io.OutputStream}
     * @param key key for the cipher algorithm
     * @param iv initial values for the CBC scheme
     */
    public CryptOutputStream(OutputStream parent, byte[] key, byte[] iv) {
        this._cipher = new CBC(iv, key, parent);
    }

    /**
     * Initializes the cipher with the given key. The initial values for the CBC
     * scheme will be random and sent to the underlying stream.
     *
     * @param parent underlying {@link java.io.OutputStream}
     * @param key key for the cipher algorithm
     * @throws IOException if the initial values can't be written to the
     * underlying stream
     */
    public CryptOutputStream(OutputStream parent, byte[] key)
            throws IOException {
        byte[] iv = new byte[16];
        Random rnd = CryptUtils.newRandomNumberGenerator();
        rnd.nextBytes(iv);
        parent.write(iv);

        this._cipher = new CBC(iv, key, parent);
    }

    /**
     * Encrypts a single {@code byte}.
     *
     * @param b {@code byte} to be encrypted
     * @throws IOException if encrypted data can't be written to the underlying
     * stream
     */
    @Override
    public void write(int b) throws IOException {
        this._buffer[0] = (byte) b;
        this._cipher.encrypt(this._buffer);
    }

    /**
     * Encrypts a {@code byte} array.
     *
     * @param b {@code byte} array to be encrypted
     * @throws IOException if encrypted data can't be written to the underlying
     * stream
     */
    @Override
    public void write(byte[] b) throws IOException {
        this._cipher.encrypt(b);
    }

    /**
     * Finalizes the encryption and closes the underlying stream.
     *
     * @throws IOException if the encryption fails or the encrypted data can't
     * be written to the underlying stream
     */
    @Override
    public void close() throws IOException {
        this._cipher.finishEncryption();
    }

}
