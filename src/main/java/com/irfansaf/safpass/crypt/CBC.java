package com.irfansaf.safpass.crypt;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implements the &quot;Cipher Block Chaining Mode&quot;. As cipher the class
 * {@link AES256} will be used.
 *
 * @OriginalAuthor Timm Knape
 * @RevisionAuthor Irfan Saf
 * @version Revision: 1.4
 */
public class CBC {

    /**
     * size of a block in {@code byte}'s
     */
    private static final int BLOCK_SIZE = 16;
    /**
     * cipher
     */
    private final AES256 _cipher;

    /**
     * last calculated block
     */
    private final byte[] _current;

    /**
     * temporary block. It will only be used for decryption.
     */
    private byte[] _buffer = null;

    /**
     * Temporary block.
     */
    private final byte[] _tmp;

    /**
     * buffer of the last output block. It will only be used for decryption.
     */
    private byte[] _outBuffer = null;

    /**
     * Is the output buffer filled?
     */
    private boolean _outBufferUsed = false;

    /**
     * Temporary buffer to accumulate whole blocks of data
     */
    private final byte[] _overflow;

    /**
     * How many {@code byte}s of {@link CBC#_overflow} are used?
     */
    private int _overflowUsed;

    private final OutputStream _output;

    /**
     * Creates the temporary buffers.
     *
     * @param iv initial value of {@link CBC#_tmp}
     * @param key key for {@link CBC#_cipher}
     * @param output stream where the encrypted or decrypted data is written
     */
    public CBC(byte[] iv, byte[] key, OutputStream output) {
        this._cipher = new AES256(key);
        this._current = new byte[BLOCK_SIZE];
        System.arraycopy(iv, 0, this._current, 0, BLOCK_SIZE);
        this._tmp = new byte[BLOCK_SIZE];
        this._buffer = new byte[BLOCK_SIZE];
        this._outBuffer = new byte[BLOCK_SIZE];
        this._outBufferUsed = false;
        this._overflow = new byte[BLOCK_SIZE];
        this._overflowUsed = 0;
        this._output = output;
    }

    /**
     * Encrypts a block. {@link CBC#_current} will be modified
     *
     * @param inBuffer array containing the input block
     * @param outBuffer storage of the encrpted block
     */
    private void encryptBlock(byte[] inBuffer, byte[] outBuffer) {
        for (int i = 0; i < BLOCK_SIZE; ++i) {
            this._current[i] ^= inBuffer[i];
        }
        this._cipher.encrypt(this._current, 0, this._current, 0);
        System.arraycopy(this._current, 0, outBuffer, 0, BLOCK_SIZE);
    }

    /**
     * Decrypts a block. {@link CBC#_current} will be modified.
     *
     * @param inBuffer storage of the encrypted block
     */
    private void decryptBlock(byte[] inBuffer) {
        System.arraycopy(inBuffer, 0, this._buffer, 0, BLOCK_SIZE);
        this._cipher.decrypt(this._buffer, 0, this._tmp, 0);
        for (int i = 0; i < BLOCK_SIZE; ++i) {
            this._tmp[i] ^= this._current[i];
            this._current[i] = this._buffer[i];
            this._outBuffer[i] = this._tmp[i];
        }
    }

    /**
     * Encrypts the array. The whole array will be encrypted.
     *
     * @param data {@code byte}s that should be encrypted
     * @throws IOException if the writing fails
     */
    public void encrypt(byte[] data) throws IOException {
        if (data != null) {
            encrypt(data, data.length);
        }
    }

    /**
     * Decrypts the array. The whole array will be decrypted.
     *
     * @param data {@code byte}s that should be decrypted
     * @throws IOException if the writing fails
     */
    public void decrypt(byte[] data) throws IOException {
        if (data != null) {
            decrypt(data, data.length);
        }
    }

    /**
     * Encrypts a part of the array. Only the first {@code length} {@code byte}s
     * of the array will be encrypted.
     *
     * @param data {@code byte}s that should be encrypted
     * @param length number of {@code byte}s that should be encrypted
     * @throws IOException if the writing fails
     */
    public void encrypt(byte[] data, int length) throws IOException {
        if (data == null || length <= 0) {
            return;
        }

        for (int i = 0; i < length; ++i) {
            this._overflow[this._overflowUsed++] = data[i];
            if (this._overflowUsed == BLOCK_SIZE) {
                encryptBlock(this._overflow, this._outBuffer);
                this._output.write(this._outBuffer);
                this._overflowUsed = 0;
            }
        }
    }

    /**
     * Decrypts a part of the array. Only the first {@code length} {@code byte}s
     * of the array will be decrypted.
     *
     * @param data {@code byte}s that should be decrypted
     * @param length number of {@code byte}s that should be decrypted
     * @throws IOException if the writing fails
     */
    public void decrypt(byte[] data, int length) throws IOException {
        if (data == null || length <= 0) {
            return;
        }

        for (int i = 0; i < length; ++i) {
            this._overflow[this._overflowUsed++] = data[i];
            if (this._overflowUsed == BLOCK_SIZE) {
                if (this._outBufferUsed) {
                    this._output.write(this._outBuffer);
                }
                decryptBlock(this._overflow);
                this._outBufferUsed = true;
                this._overflowUsed = 0;
            }
        }
    }

    /**
     * Finishes the encryption process.
     *
     * @throws IOException if the writing fails
     */
    public void finishEncryption() throws IOException {
        byte pad = (byte) (BLOCK_SIZE - this._overflowUsed);
        while (this._overflowUsed < BLOCK_SIZE) {
            this._overflow[this._overflowUsed++] = pad;
        }

        encryptBlock(this._overflow, this._outBuffer);
        this._output.write(this._outBuffer);
        this._output.close();
    }
}
