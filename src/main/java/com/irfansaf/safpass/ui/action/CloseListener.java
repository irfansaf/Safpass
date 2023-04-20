package com.irfansaf.safpass.ui.action;

import com.irfansaf.safpass.ui.SafPassFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Listener for window close.
 *
 * @author Irfan Saf
 */
public class CloseListener extends WindowAdapter{

    /**
     * Calls the {@code exitFrame} method of main frame.
     * @param event the event to be processed
     */
    @Override
    public void windowClosing(WindowEvent event) {
        if (event.getSource() instanceof SafPassFrame) {
            ((SafPassFrame) event.getSource()).exitFrame();
        }
    }
}
