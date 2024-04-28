package com.irfansaf.safpass;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.irfansaf.safpass.ui.SafPassFrame;
import com.irfansaf.safpass.util.Configuration;

import javax.swing.UIManager;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public final class Safpass {
    private static final Logger LOG = Logger.getLogger(Safpass.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n") ;
    }

    private Safpass() {
        // Not Instantiated
    }

    public static boolean isPurchaseCodeValid() {
        Preferences prefs = Preferences.userNodeForPackage(Configuration.class);
        String purchaseCode = prefs.get(Configuration.PURCHASE_CODE_KEY, null);
        return purchaseCode != null && !purchaseCode.isEmpty();
    }

    public static void main(final String[] args) {
        try {
            UIManager.put("Button.arc", 4);
            FlatLaf lookAndFeel;
            if (Configuration.getInstance().is("ui.theme.dark.mode.enabled", false)) {
                lookAndFeel = new FlatDarkLaf();
            } else {
                lookAndFeel = new FlatLightLaf();
            }

            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            LOG.log(Level.CONFIG, "Could not set look and feel for the application", e);
        }
        SafPassFrame.getInstance((args.length > 0) ? args[0] : null);
    }
}