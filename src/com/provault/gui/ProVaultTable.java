package com.provault.gui;

import com.provault.constants.Colours;
import com.provault.constants.ColumnIndex;
import com.provault.constants.Icons;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ProVaultTable extends JTable {
    public ProVaultTable(DefaultTableModel model) {
        super(model);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setRowHeight(36);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);
        getColumnModel().getColumn(ColumnIndex.ICON_COLUMN).setPreferredWidth(50);
        getColumnModel().getColumn(ColumnIndex.DISPLAY_NAME_COLUMN).setPreferredWidth(300);
        getColumnModel().getColumn(ColumnIndex.ENCRYPTED_STATUS_COLUMN).setPreferredWidth(42);
        getColumnModel().getColumn(ColumnIndex.CATEGORY_COLUMN).setPreferredWidth(200);
        getColumnModel().getColumn(ColumnIndex.FILE_SIZE_COLUMN).setPreferredWidth(200);
        getColumnModel().getColumn(ColumnIndex.FILE_NAME_COLUMN).setPreferredWidth(0);
        getColumnModel().removeColumn(getColumnModel().getColumn(ColumnIndex.FILE_NAME_COLUMN));
        Font tableFont = new Font("Serif", Font.PLAIN, 16);
        setFont(tableFont);
        final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        final DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);
                return this;
            }
        };
        renderer.setBackground(Colours.COLOR_2);
        renderer.setForeground(Colours.COLOR_4);
        getTableHeader().setDefaultRenderer(renderer);
        setDefaultRenderer(String.class, cellRenderer);
        getTableHeader().setBackground(Colours.COLOR_2);

        DefaultCellEditor defaultCellEditor = (DefaultCellEditor) getDefaultEditor(Boolean.class);
        JCheckBox checkBox = (JCheckBox) defaultCellEditor.getComponent();
        checkBox.setIcon(Icons.UNLOCKED_ICON);
        checkBox.setSelectedIcon(Icons.LOCKED_ICON);

        TableCellRenderer tableCellRenderer = getDefaultRenderer(Boolean.class);
        checkBox = (JCheckBox) tableCellRenderer;
        checkBox.setIcon(Icons.UNLOCKED_ICON);
        checkBox.setSelectedIcon(Icons.LOCKED_ICON);
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
