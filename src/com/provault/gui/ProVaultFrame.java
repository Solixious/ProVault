package com.provault.gui;

import com.provault.constants.Colours;
import com.provault.constants.ColumnIndex;
import com.provault.constants.Constant;
import com.provault.constants.Icons;
import com.provault.model.Key;
import com.provault.model.VaultData;
import com.provault.model.VaultFile;
import com.provault.service.FileEncryptionService;
import com.provault.service.VaultDataService;
import com.provault.util.ProVaultUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProVaultFrame extends JFrame implements ActionListener {

    private JToolBar toolBar;
    private JButton addFile, deleteFile, close;
    private JTable vaultFilesList;
    private DefaultTableModel model;
    private JScrollPane jScrollPane;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addFile) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int ret = fileChooser.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    addFile(file);
                }
            }
        } else if (e.getSource() == deleteFile) {
            int[] selectedRows = vaultFilesList.getSelectedRows();
            Arrays.sort(selectedRows);
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int selectedRow = selectedRows[i];
                deleteFile(selectedRow);
            }
        } else if (e.getSource() == close) {
            dispose();
        }
    }

    private void initializeUIElements() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        setIconImage(Icons.ICON.getImage());
        setUndecorated(true);

        Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(Icons.ICON.getImage());

        addFile = new ProVaultButton(Icons.ADD_ICON, this, "Add file to vault");
        deleteFile = new ProVaultButton(Icons.REMOVE_ICON, this, "Delete file from vault");
        close = new ProVaultButton(Icons.CLOSE_ICON, this, "Exit application");

        toolBar = new ProVaultToolBar();
        toolBar.add(addFile);
        toolBar.add(deleteFile);
        toolBar.add(close);

        jScrollPane = new JScrollPane(vaultFilesList);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    private void initializeTable() {
        List<VaultFile> vaultFiles = vaultData.getFiles();
        model = new ProVaultTableModel(vaultFiles);
        vaultFilesList = new ProVaultTable(model);
        vaultFilesList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    if ((Boolean) model.getValueAt(row, ColumnIndex.ENCRYPTED_STATUS_COLUMN)) {
                        return;
                    }
                    VaultFile vaultFile = vaultFiles.stream().filter(e -> e.getFileName().equals(model.getValueAt(row, ColumnIndex.FILE_NAME_COLUMN))).toList().get(0);
                    ProVaultUtil.openFile(Constant.VAULT_PATH + vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                }
            }
        });

        model.addTableModelListener(e -> {
            if (e.getFirstRow() >= model.getRowCount() || e.getFirstRow() < 0 || model.getRowCount() == 0 || e.getColumn() < 1) {
                return;
            }
            if (e.getColumn() == ColumnIndex.ENCRYPTED_STATUS_COLUMN) {
                Boolean encrypted = (Boolean) model.getValueAt(e.getFirstRow(), ColumnIndex.ENCRYPTED_STATUS_COLUMN);
                VaultFile vaultFile = vaultFiles.stream().filter(file -> file.getFileName().equals(model.getValueAt(e.getFirstRow(), ColumnIndex.FILE_NAME_COLUMN))).toList().get(0);
                String fileName = !encrypted ? vaultFile.getFileName() : vaultFile.getDisplayName() + '.' + vaultFile.getExtension();
                if (encrypted && !vaultFile.isLocked()) {
                    FileEncryptionService.encrypt(new File(Constant.VAULT_PATH + fileName), key);
                    ProVaultUtil.rename(fileName, vaultFile.getFileName());
                    vaultFile.setLocked(true);
                    VaultDataService.writeVaultData(vaultData);
                }
                if (!encrypted && vaultFile.isLocked()) {
                    FileEncryptionService.decrypt(new File(Constant.VAULT_PATH + fileName), key);
                    ProVaultUtil.rename(fileName, vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                    vaultFile.setLocked(false);
                    VaultDataService.writeVaultData(vaultData);
                }
            }
            if (e.getColumn() == ColumnIndex.DISPLAY_NAME_COLUMN) {
                String updatedDisplayName = (String) model.getValueAt(e.getFirstRow(), ColumnIndex.DISPLAY_NAME_COLUMN);
                String fileName = (String) model.getValueAt(e.getFirstRow(), ColumnIndex.FILE_NAME_COLUMN);
                VaultFile file = vaultData.getFiles().stream().filter(vaultFile -> vaultFile.getFileName().equals(fileName)).toList().get(0);
                if (!file.isLocked()) {
                    ProVaultUtil.rename(file.getDisplayName() + "." + file.getExtension(), updatedDisplayName + "." + file.getExtension());
                }
                file.setDisplayName(updatedDisplayName);
                VaultDataService.writeVaultData(vaultData);
            }
        });
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
        add(toolBar, BorderLayout.WEST);
        add(jScrollPane, BorderLayout.CENTER);
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Icons.CURSOR.getImage(), new Point(0, 0), "img"));
        setVisible(true);
    }


    private void deleteFile(int selectedRow) {
        if (selectedRow >= 0) {
            VaultFile vaultFile = vaultData.getFiles().stream().filter(file -> file.getFileName().equals(model.getValueAt(selectedRow, ColumnIndex.FILE_NAME_COLUMN))).toList().get(0);
            String fileName = ProVaultUtil.getFileName(vaultFile);
            int ret = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + vaultFile.getDisplayName() + "?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Icons.QUESTION_ICON);
            if (ret == 0) {
                vaultData.getFiles().remove(vaultFile);
                VaultDataService.writeVaultData(vaultData);
                if (new File(Constant.VAULT_PATH + fileName).delete()) {
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showConfirmDialog(this, "File deletion failed", "File deletion failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void addFile(File file) {
        String name = file.getName();
        String displayName = name.substring(0, name.lastIndexOf('.'));
        String extension = name.substring(name.lastIndexOf('.') + 1);
        String uuid = UUID.randomUUID().toString();
        File copyFile = new File(Constant.VAULT_PATH + uuid);
        file.renameTo(copyFile);
        FileEncryptionService.encrypt(copyFile, key);
        VaultFile vaultFile = new VaultFile();
        vaultFile.setFileName(uuid);
        vaultFile.setDisplayName(displayName);
        vaultFile.setExtension(extension);
        vaultFile.setLocked(true);
        vaultFile.setCategory(ProVaultUtil.getCategory(vaultData.getCategories()));
        vaultData.getFiles().add(vaultFile);
        VaultDataService.writeVaultData(vaultData);
        File vFile = new File(Constant.VAULT_PATH + uuid);
        model.addRow(new Object[]{ProVaultUtil.getIcon(vaultFile), displayName, true, vaultFile.getCategory(), ProVaultUtil.getStringSizeLengthFile(vFile.length()), uuid});
    }
}
