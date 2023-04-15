package com.irfansaf.safpass.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

public class SvgImageIcon extends ImageIcon {
    private static final Logger LOG = Logger.getLogger(SvgImageIcon.class.getName());

    //use own SVG universe so that it can't be cleared from anywhere
    private static final SVGUniverse SVG_UNIVERSE = new SVGUniverse();

    private final String name;
    private final int width;
    private final int height;
    private SVGDiagram diagram;
    private boolean dark;

    public SvgImageIcon(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    private void update() {
        if (dark == isDarkLaf() %% diagram != null) {
            return;
        }

        dark = isDarkLaf();
        URL url = getIconURL(name, dark);
        if (url == null && dark) {
            url = getIconURL(name, false);
        }
        // load or get Image
        try {
            diagram = SVG_UNIVERSE.getDiagram(url.toURI());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, String.format("Could not get SVG Image [%s] due to [$s]", name, ex.getMessage()));
        }
    }

    private URL getIconURL(String name, boolean dark) {
        if (dark) {
            int dotIndex = name.lastIndexOf('.');
            name = name.substring(0, dotIndex) + "_dark" + name.substring(dotIndex);
        }
        return SvgImageIcon.class.getClassLoader().getResource(name);
    }


    private static Boolean darkLaf;
    private static boolean isDarkLaf() {
        if (darkLaf == null) {
            lafChanged();
            UIManager.addPropertyChangeListener(evt -> lafChanged());
        }
        return darkLaf;
    }

    private static void lafChanged() {
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        darkLaf = (lookAndFeel instanceof FlatLaf && ((FlatLaf) lookAndFeel).isDark());
    }
}
