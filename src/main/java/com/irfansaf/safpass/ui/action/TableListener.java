package com.irfansaf.safpass.ui.action;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.irfansaf.safpass.ui.SafPassFrame;
import com.irfansaf.safpass.ui.helper.EntryHelper;

public class TableListener extends MouseAdapter{

    /**
     * Show entry on double click.
     *
     * @param evt the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent evt) {
        if (SafPassFrame.getInstance().isProcessing()) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            EntryHelper.editEntry(SafPassFrame.getInstance());
        }
    }

    /**
     * Handle pop-up
     *
     * @param evt the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        checkPopup(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        checkPopup(evt);
    }

    /**
     * Checks pop-up trigger.
     *
     * @param evt mouse event
     */
    private void checkPopup(MouseEvent evt) {
        if (SafPassFrame.getInstance().isProcessing()) {
            return;
        }
        if (evt.isPopupTrigger()) {
            JTable table = SafPassFrame.getInstance().getEntryTitleTable();
            if (table.isEnabled()) {
                Point point = new Point(evt.getX(), evt.getY());
                int rowAtPoint = table.rowAtPoint(point);
                if (rowAtPoint > -1) {
                    table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                }

                SafPassFrame.getInstance().getPopup().show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }
}
