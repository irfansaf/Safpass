package com.irfansaf.safpass.data;

/**
 * Exception if the processing of XML document fails.
 *
 * @author Irfan Saf
 */
public class DocumentProcessException extends Exception{

    public DocumentProcessException(String message) {
        super("Cannon process document due to the following exception:\n" + message);
    }
}
