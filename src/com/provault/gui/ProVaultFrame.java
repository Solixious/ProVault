package com.provault.gui;

import com.provault.constants.Colours;
import com.provault.constants.Constant;
import com.provault.constants.Icons;
import com.provault.gui.listener.AddButtonActionListener;
import com.provault.gui.listener.DeleteButtonActionListener;
import com.provault.gui.listener.ExitButtonActionListener;
import com.provault.gui.listener.ProVaultTableListener;
import com.provault.model.Key;
import com.provault.model.VaultData;
import com.provault.model.VaultFile;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProVaultFrame extends JFrame {

    private ProVaultToolBar toolBar;
    private ProVaultTable vaultFilesList;
    private ProVaultTableModel model;
    private JScrollPane jScrollPane;
    private JFileChooser fileChooser;
    private final VaultData vaultData;
    private final Key key;

    public ProVaultFrame(final VaultData vaultData, Key key) {
        super(Constant.TITLE);
        this.vaultData = vaultData;
        this.key = key;
        initializeTable();
        initializeUIElements();
        initializeTheme();
        initializeFrame();
    }

    public ProVaultTableModel getModel() {
        return model;
    }

    public VaultData getVaultData() {
        return vaultData;
    }

    public Key getKey() {
        return key;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public ProVaultTable getVaultFilesList() {
        return vaultFilesList;
    }

    private void initializeUIElements() {
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(Icons.ICON.getImage());

        ProVaultButton addFile = new ProVaultButton(Icons.ADD_ICON, new AddButtonActionListener(this), "Add file to vault");
        ProVaultButton deleteFile = new ProVaultButton(Icons.REMOVE_ICON, new DeleteButtonActionListener(this), "Delete file from vault");
        ProVaultButton close = new ProVaultButton(Icons.CLOSE_ICON, new ExitButtonActionListener(this), "Exit application");

        toolBar = new ProVaultToolBar();
        toolBar.add(addFile);
        toolBar.add(deleteFile);
        toolBar.add(close);

        jScrollPane = new JScrollPane(vaultFilesList);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    private void initializeTable() {
        List<VaultFile> vaultFiles = vaultData.getFiles();
        ProVaultTableListener tableListener = new ProVaultTableListener(this);
        model = new ProVaultTableModel(vaultFiles);
        vaultFilesList = new ProVaultTable(model);
        vaultFilesList.addMouseListener(tableListener);
        model.addTableModelListener(tableListener);
    }

    private void initializeTheme() {
        vaultFilesList.setBackground(Colours.COLOR_1);
        vaultFilesList.setForeground(Colours.COLOR_4);
        vaultFilesList.setShowHorizontalLines(false);
        vaultFilesList.setShowVerticalLines(false);
        vaultFilesList.setSelectionBackground(Colours.COLOR_3);
        vaultFilesList.setSelectionForeground(Colours.COLOR_4);
        jScrollPane.getViewport().setBackground(Colours.COLOR_1);
        toolBar.setBackground(Colours.COLOR_2);
    }

    private void initializeFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        setIconImage(Icons.ICON.getImage());
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Icons.CURSOR.getImage(), new Point(0, 0), "img"));
        add(toolBar, BorderLayout.WEST);
        add(jScrollPane, BorderLayout.CENTER);
        setUndecorated(true);
        setVisible(true);
    }
}
