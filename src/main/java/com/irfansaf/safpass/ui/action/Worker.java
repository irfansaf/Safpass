package com.irfansaf.safpass.ui.action;

import com.irfansaf.safpass.ui.MessageDialog;
import com.irfansaf.safpass.ui.SafPassFrame;

import javax.swing.SwingWorker;

/**
 * Worker class for time consuming tasks. While the task is running, the main
 * application is disabled, and a progress indicator is shown.
 *
 * @author Irfan Saf
 *
 */
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
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
        super.done();
        stopProcessing();
        try {
            get();
        } catch (Exception e) {
            showErrorMessage(e);
        }
    }

    /**
     * Shows the message of the corresponding exception..
     *
     * @param e the exception
     */
    protected void showErrorMessage(final Exception e) {
        String message;
        if (e.getCause() != null) {
            message = e.getCause().getMessage();
        } else {
            message = e.getMessage();
        }
        MessageDialog.showErrorMessage(this.parent, message);
    }

    /**
     * Stops progress indicator and refreshes UI.
     */
    protected void stopProcessing() {
        this.parent.setProcessing(false);
        this.parent.refreshAll();
    }
}
