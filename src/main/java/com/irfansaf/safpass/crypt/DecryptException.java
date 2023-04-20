package com.irfansaf.safpass.crypt;

/**
 * Exception, if the decryption fails. {@link CBC} throws this exception, if the
 * last block is not a legal conclusion of a decryption stream.
 *
 * @author Timm Knape
 * @version $Revision: 1.3 $
 */
public final class DecryptException extends Exception {

    /**
     * Creates the exception.
     */
    public DecryptException() {
        super("Decryption failed.");
    }
}
