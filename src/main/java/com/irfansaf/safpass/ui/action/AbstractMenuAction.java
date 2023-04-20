package com.irfansaf.safpass.ui.action;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Class for handling menu actions.
 *
 * @author Irfan Saf
 */
public abstract class AbstractMenuAction extends AbstractAction {
    /**
     * Creates a new menu action
     *
     * @param text title of the action that appears on UI
     * @param icon icon of action
     * @param accelerator accelerator key
     */
    public AbstractMenuAction(String text, Icon icon, KeyStroke accelerator) {
        super(text,icon);
        putValue(SHORT_DESCRIPTION, text);
        if (accelerator != null) {
            putValue(ACCELERATOR_KEY, accelerator);
        }
    }
}
