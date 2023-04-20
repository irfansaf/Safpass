package com.irfansaf.safpass.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public final class ClipboardUtils {

    /**
     * Empty clipboard content.
     */
    private static final EmptyClipboardContent EMPTY_CONTENT = new EmptyClipboardContent();

    private ClipboardUtils() {
        // utility class
    }

    /**
     * Sets text to the system clipboard.
     *
     * @param str text
     * @throws Exception when clipboard is not accessible
     */
    public static void setClipboardContent(String str) throws Exception {
        if (str == null || str.isEmpty()) {
            clearClipboardContent();
            return;
        }
        try {
            StringSelection selection = new StringSelection(str);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        } catch (Throwable throwable) {
            throw new Exception("Cannot set clipboard content.");
        }
    }

    /**
     * Clears the system clipboard.
     *
     * @throws Exception when clipboard is not accessible
     */
    public static void clearClipboardContent() throws Exception {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(EMPTY_CONTENT, EMPTY_CONTENT);
        } catch (Throwable throwable) {
            throw new Exception("Cannot set clipboard content.");
        }
    }

    /**
     * Get text from system clipboard.
     *
     * @return the text, or {@code null} if there is no content
     */
    public static String getClipboardContent() {
        String result = null;
        try {
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                result = String.valueOf(contents.getTransferData(DataFlavor.stringFlavor));
            }
        } catch (Throwable throwable) {
            // ignore
        }
        return result == null || result.isEmpty() ? null : result;
    }

    /**
     * Class representing an empty clipboard content. With the help of this
     * class, the content of clipboard can be cleared.
     *
     * @author Gabor_Bata
     *
     */
    protected static final class EmptyClipboardContent implements Transferable, ClipboardOwner {

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
            // do nothing
        }
    }
}
