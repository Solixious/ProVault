package com.provault.gui;

import com.provault.constants.Colours;
import com.provault.constants.Constant;
import com.provault.constants.Icons;
import com.provault.model.Key;
import com.provault.model.VaultData;
import com.provault.model.VaultFile;
import com.provault.service.FileEncryptionService;
import com.provault.service.VaultDataService;
import com.provault.util.ProVaultUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProVaultFrame implements ActionListener {

    private static final Integer ICON_COLUMN = 0;
    private static final Integer DISPLAY_NAME_COLUMN = 1;
    private static final Integer ENCRYPTED_STATUS_COLUMN = 2;
    private static final Integer CATEGORY_COLUMN = 3;
    private static final Integer FILE_SIZE_COLUMN = 4;
    private static final Integer FILE_NAME_COLUMN = 5;

    private JFrame frame;
    private JToolBar toolBar;
    private JButton addFile, deleteFile, close;
    private JTable vaultFilesList;
    private DefaultTableModel model;
    private JScrollPane jScrollPane;
    private final VaultData vaultData;
    private final Key key;

    public ProVaultFrame(final VaultData vaultData, Key key) {
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
            int ret = fileChooser.showOpenDialog(frame);
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
            frame.dispose();
        }
    }

    private void initializeUIElements() {
        frame = new JFrame(Constant.TITLE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setIconImage(Icons.ICON.getImage());
        frame.setUndecorated(true);

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
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == ENCRYPTED_STATUS_COLUMN
                        || column == DISPLAY_NAME_COLUMN;
            }
        };
        model.addColumn("Icon");
        model.addColumn("File Name");
        model.addColumn("Lock");
        model.addColumn("Category");
        model.addColumn("File Size");
        model.addColumn("");
        for (VaultFile vaultFile : vaultFiles) {
            String fileName = getFileName(vaultFile);
            File vFile = new File(Constant.VAULT_PATH + fileName);
            model.addRow(new Object[]{ProVaultUtil.getIcon(vaultFile), vaultFile.getDisplayName(), vaultFile.isLocked(), vaultFile.getCategory(), ProVaultUtil.getStringSizeLengthFile(vFile.length()), vaultFile.getFileName()});
        }
        vaultFilesList = new ProVaultTable(model);
        vaultFilesList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    if ((Boolean) model.getValueAt(row, ENCRYPTED_STATUS_COLUMN)) {
                        return;
                    }
                    VaultFile vaultFile = vaultFiles.stream().filter(e -> e.getFileName().equals(model.getValueAt(row, FILE_NAME_COLUMN))).toList().get(0);
                    openFile(Constant.VAULT_PATH + vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                }
            }
        });
        vaultFilesList.getColumnModel().getColumn(ICON_COLUMN).setPreferredWidth(50);
        vaultFilesList.getColumnModel().getColumn(DISPLAY_NAME_COLUMN).setPreferredWidth(300);
        vaultFilesList.getColumnModel().getColumn(ENCRYPTED_STATUS_COLUMN).setPreferredWidth(42);
        vaultFilesList.getColumnModel().getColumn(CATEGORY_COLUMN).setPreferredWidth(200);
        vaultFilesList.getColumnModel().getColumn(FILE_SIZE_COLUMN).setPreferredWidth(200);
        vaultFilesList.getColumnModel().getColumn(FILE_NAME_COLUMN).setPreferredWidth(0);
        vaultFilesList.getColumnModel().removeColumn(vaultFilesList.getColumnModel().getColumn(FILE_NAME_COLUMN));

        Font tableFont = new Font("Serif", Font.PLAIN, 16);
        vaultFilesList.setFont(tableFont);

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
        vaultFilesList.getTableHeader().setDefaultRenderer(renderer);
        vaultFilesList.setDefaultRenderer(String.class, cellRenderer);
        vaultFilesList.getTableHeader().setBackground(Colours.COLOR_2);

        DefaultCellEditor defaultCellEditor = (DefaultCellEditor) vaultFilesList.getDefaultEditor(Boolean.class);
        JCheckBox checkBox = (JCheckBox) defaultCellEditor.getComponent();
        checkBox.setIcon(Icons.UNLOCKED_ICON);
        checkBox.setSelectedIcon(Icons.LOCKED_ICON);

        TableCellRenderer tableCellRenderer = vaultFilesList.getDefaultRenderer(Boolean.class);
        checkBox = (JCheckBox) tableCellRenderer;
        checkBox.setIcon(Icons.UNLOCKED_ICON);
        checkBox.setSelectedIcon(Icons.LOCKED_ICON);

        model.addTableModelListener(e -> {
            if (e.getFirstRow() >= model.getRowCount() || e.getFirstRow() < 0 || model.getRowCount() == 0 || e.getColumn() < 1) {
                return;
            }
            if (e.getColumn() == ENCRYPTED_STATUS_COLUMN) {
                Boolean encrypted = (Boolean) model.getValueAt(e.getFirstRow(), ENCRYPTED_STATUS_COLUMN);
                VaultFile vaultFile = vaultFiles.stream().filter(file -> file.getFileName().equals(model.getValueAt(e.getFirstRow(), FILE_NAME_COLUMN))).toList().get(0);
                String fileName = !encrypted ? vaultFile.getFileName() : vaultFile.getDisplayName() + '.' + vaultFile.getExtension();
                if (encrypted && !vaultFile.isLocked()) {
                    FileEncryptionService.encrypt(new File(Constant.VAULT_PATH + fileName), key);
                    rename(fileName, vaultFile.getFileName());
                    vaultFile.setLocked(true);
                    VaultDataService.writeVaultData(vaultData);
                }
                if (!encrypted && vaultFile.isLocked()) {
                    FileEncryptionService.decrypt(new File(Constant.VAULT_PATH + fileName), key);
                    rename(fileName, vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                    vaultFile.setLocked(false);
                    VaultDataService.writeVaultData(vaultData);
                }
            }
            if (e.getColumn() == DISPLAY_NAME_COLUMN) {
                String updatedDisplayName = (String) model.getValueAt(e.getFirstRow(), DISPLAY_NAME_COLUMN);
                String fileName = (String) model.getValueAt(e.getFirstRow(), FILE_NAME_COLUMN);
                VaultFile file = vaultData.getFiles().stream().filter(vaultFile -> vaultFile.getFileName().equals(fileName)).toList().get(0);
                if (!file.isLocked()) {
                    rename(file.getDisplayName() + "." + file.getExtension(), updatedDisplayName + "." + file.getExtension());
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
        frame.add(toolBar, BorderLayout.WEST);
        frame.add(jScrollPane, BorderLayout.CENTER);
        frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Icons.CURSOR.getImage(), new Point(0, 0), "img"));
        frame.setVisible(true);
    }

    private String getFileName(VaultFile vaultFile) {
        return vaultFile.isLocked() ? vaultFile.getFileName() : vaultFile.getDisplayName() + '.' + vaultFile.getExtension();
    }

    private void rename(String source, String newFileName) {
        Path sourcePath = Paths.get(Constant.VAULT_PATH + source);
        try {
            Files.move(sourcePath, sourcePath.resolveSibling(newFileName));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void openFile(String fileName) {
        try {
            Desktop.getDesktop().open(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(int selectedRow) {
        if (selectedRow >= 0) {
            VaultFile vaultFile = vaultData.getFiles().stream().filter(file -> file.getFileName().equals(model.getValueAt(selectedRow, FILE_NAME_COLUMN))).toList().get(0);
            String fileName = getFileName(vaultFile);
            int ret = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete " + vaultFile.getDisplayName() + "?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Icons.QUESTION_ICON);
            if (ret == 0) {
                vaultData.getFiles().remove(vaultFile);
                VaultDataService.writeVaultData(vaultData);
                if (new File(Constant.VAULT_PATH + fileName).delete()) {
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showConfirmDialog(frame, "File deletion failed", "File deletion failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
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
