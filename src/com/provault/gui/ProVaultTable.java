package com.provault.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProVaultTable extends JTable {
    public ProVaultTable(DefaultTableModel model) {
        super(model);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setRowHeight(36);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0 -> Icon.class;
            case 2 -> Boolean.class;
            default -> String.class;
        };
    }
}
