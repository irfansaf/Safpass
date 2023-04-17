package com.irfansaf.safpass.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import static com.irfansaf.safpass.ui.MessageDialog.getIcon;

public final class SafPassFrame extends JFrame {
    private static final Logger LOG = Logger.getLogger(SafPassFrame.class.getName());

    private JToolBar toolBar;

    private SafPassFrame(String fileName) {
        try {
            setIconImages(Stream.of(16, 20, 32, 40, 64, 80, 128, 160)
                    .map(size -> getIcon("safpass", size, size).getImage())
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            LOG.log(Level.CONFIG, "Could not set application icon.", e);
        }

        this.toolBar = new JToolBar();
        this.toolBar.setFloatable(true);
        this.toolBar.add(MenuActionType.NEW_FILE.getAction());
    }
}
