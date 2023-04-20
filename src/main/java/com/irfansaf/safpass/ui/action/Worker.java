package com.irfansaf.safpass.ui.action;

import com.irfansaf.safpass.ui.MessageDialog;
import com.irfansaf.safpass.ui.SafPassFrame;

import javax.swing.SwingWorker;

public abstract class Worker extends SwingWorker<Void, Void> {
    /**
     * Main application frame.
     */
    private final SafPassFrame parent;

    /**
     * Creates a new worker instance.
     *
     * @param parent main application frame
     */
    public Worker(final SafPassFrame parent) {
        this.parent = parent;
        this.parent.setProcessing(true);
    }

    /**
     * Sets back the processing state of the frame, and refreshes the frame
     * content.
     */
    @Override
    protected void done() {
        super.done();
        stopProcessing();
    }

    protected void showErrorMessage(final Exception e) {
        String message;
        if (e.getCause() != null) {
            message = e.getMessage().getMessage();
        } else {
            message = e,getMessage();
        }
        MessageDialog.showErrorMessage(this.parent, message);
    }

    protected void stopProcessing() {
        this.parent.setProcessing(false);
        this.parent.refreshAll();
    }
}
