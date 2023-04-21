package com.irfansaf.safpass.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import static javax.swing.KeyStroke.getKeyStroke;
import static java.awt.event.KeyEvent.VK_ESCAPE;

/**
 * Class for representing search panel. Search panel is hidden by default.
 *
 * @author Irfan Saf
 */
public class SearchPanel extends JPanel implements ActionListener {
    private static final String CLOSE_BUTTON_ACTION_COMMAND = "close_search_panel_button";
    private static final String SEARCH_PANEL_CLOSE_ACTION = "safpass.search_panel.close";

    private final JLabel label;
    private final JTextField criteriaField;
    private final JButton closeButton;

    /**
     * Creates a new search panel with the given callback object.
     *
     * @param searchCallback the callback used on document updates.
     */
    public SearchPanel(Consumer<Boolean> searchCallback) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(2, 2, 2, 2));
        this.label = new JLabel("Find: ", MessageDialog.getIcon("find"), SwingConstants.LEADING);
        this.criteriaField = TextComponentFactory.newTextField();

        if (searchCallback != null) {
            this.criteriaField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    searchCallback.accept(isEnabled());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                searchCallback.accept(isEnabled());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    searchCallback.accept(isEnabled());
                }
            });
        }
        this.closeButton = new JButton(MessageDialog.getIcon("close"));
        this.closeButton.setBorder(new EmptyBorder(0, 2, 0,2 ));
        this.closeButton.setActionCommand(CLOSE_BUTTON_ACTION_COMMAND);
        this.closeButton.setFocusable(false);
        this.closeButton.addActionListener(this);

        Action closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        this.closeButton.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(VK_ESCAPE, 0), SEARCH_PANEL_CLOSE_ACTION);
        this.closeButton.getActionMap().put(SEARCH_PANEL_CLOSE_ACTION, closeAction);

        add(this.label, BorderLayout.WEST);
        add(this.criteriaField, BorderLayout.CENTER);
        add(this.closeButton, BorderLayout.EAST);

        this.setVisible(false);
    }
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            this.criteriaField.requestFocusInWindow();
        }  else {
            this.criteriaField.setText("");
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.label.setEnabled(enabled);
        this.criteriaField.setEnabled(enabled);
        this.closeButton.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (CLOSE_BUTTON_ACTION_COMMAND.equals(command)); {
            this.setVisible(false);
        }
    }

    /**
     * Get serach criteria.
     *
     * @return get search criteria, non null
     */
    public String getSearchCriteria() {
        String criteria = "";
        if (isVisible() && isEnabled()) {
            criteria = this.criteriaField.getText();
            criteria = criteria == null ? "" : criteria.trim();
        }
        return criteria;
    }
}
