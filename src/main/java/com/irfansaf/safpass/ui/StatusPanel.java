package com.irfansaf.safpass.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

/**
 * Class for representing a status bar.
 *
 * @author Irfan Saf
 */
public class StatusPanel extends JPanel {
    private  final JLabel label;
    private final JProgressBar progressBar;

    public StatusPanel() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(2, 2, 2, 2));
        this.label = new JLabel();
        this.progressBar = new JProgressBar();
        add(this.label, BorderLayout.CENTER);
        add(this.progressBar, BorderLayout.EAST);
        setProcessing(false);
    }

    public void setText(final String text) {
        this.label.setText(text);
    }

    public String getText() {
        return this.label.getText();
    }

    public void setProcessing(boolean processing) {
        this.progressBar.setVisible(processing);
        this.progressBar.setIndeterminate(processing);
        setText(processing ? "Processing..." : "");
    }
}
