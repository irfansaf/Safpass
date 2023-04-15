package com.irfansaf.safpass.ui.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import static com.irfansaf.safpass.ui.action.TextComponentActionType.CUT;
import static com.irfansaf.safpass.ui.action.TextComponentActionType.COPY;
import static com.irfansaf.safpass.ui.action.TextComponentActionType.PASTE;
import static com.irfansaf.safpass.ui.action.TextComponentActionType.DELETE;
import static com.irfansaf.safpass.ui.action.TextComponentActionType.CLEAR_ALL;
import static com.irfansaf.safpass.ui.action.TextComponentActionType.SELECT_ALL;

public class TextComponentPopupListener extends MouseAdapter {
    private final JPopupMenu popup;
    private final Map<TextComponentActionType, JMenuItem> items;

    public TextComponentPopupListener() {
        items = Stream.of(CUT,COPY, PASTE, DELETE, CLEAR_ALL, SELECT_ALL)
                .collect(Collectors.toMap(
                        Function.identity(),
                        type -> new JMenuItem(type.getAction()),
                        (o1, o2) -> o1,
                        LinkedHashMap::new));

        this.popup = new JPopupMenu();
        this.popup.add(items.get(CUT));
        this.popup.add(items.get(COPY));
        this.popup.add(items.get(PASTE));
        this.popup.add(items.get(DELETE));
        this.popup.addSeparator();
        this.popup.add(items.get(CLEAR_ALL));
        this.popup.add(items.get(SELECT_ALL));
    }

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && e.getSource() instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) e.getSource();
            if (textComponent.isEnabled() && (textComponent.hasFocus() || textComponent.requestFocusInWindow())) {
                items.forEach((type, item) -> item.setEnabled(type.getAction().isEnabled(textComponent)));
                this.popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
    }
}
