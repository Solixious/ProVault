package com.provault.gui;

import javax.swing.*;
import java.awt.*;

public class ProVaultToolBar extends JToolBar {

    private static final Dimension SEPARATOR_DIMENSION = new Dimension(0, 16);
    public ProVaultToolBar() {
        super(VERTICAL);
        setFloatable(false);
    }

    public Component add(Component component) {
        if(component instanceof ProVaultButton) {
            super.addSeparator(SEPARATOR_DIMENSION);
            return super.add(component);
        }
        return super.add(component);
    }
}
