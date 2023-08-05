package com.provault.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionListener;

public class ProVaultButton extends JButton {

    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

    public ProVaultButton(ImageIcon icon, ActionListener actionListener, String toolTipText) {
        super(icon);
        addActionListener(actionListener);
        setBorder(EMPTY_BORDER);
        setToolTipText(toolTipText);
    }
}
