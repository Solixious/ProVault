package com.provault.gui;

import com.provault.constants.Constant;
import com.provault.model.VaultData;
import com.provault.model.VaultFile;
import com.provault.service.FileEncryptionService;
import com.provault.service.VaultDataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProVaultFrame implements ActionListener {

    private static final String ICON = "img/icon.png";

    private JFrame frame;
    private JToolBar toolBar;
    private JButton addFile, deleteFile;
    private JTable vaultFilesList;
    private DefaultTableModel model;
    private JFileChooser fileChooser;
    private final VaultData vaultData;

    public ProVaultFrame(final VaultData vaultData) {
        this.vaultData = vaultData;

        initializeUI();
        initializeTableUI();

        frame.setVisible(true);
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(new JScrollPane(vaultFilesList), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void initializeUI() {
        ImageIcon icon = new ImageIcon(ICON);

        frame = new JFrame("Pro Vault");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setIconImage(icon.getImage());

        fileChooser = new JFileChooser();

        Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(icon.getImage());

        addFile = new JButton(new ImageIcon("img/add.png"));
        addFile.addActionListener(this);
        deleteFile = new JButton(new ImageIcon("img/remove.png"));
        deleteFile.addActionListener(this);

        toolBar = new JToolBar();
        toolBar.add(addFile);
        toolBar.add(deleteFile);
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
                    addFile(fileChooser, file);
                }
            }
        } else if (e.getSource() == deleteFile) {
            int[] selectedRows = vaultFilesList.getSelectedRows();
            Arrays.sort(selectedRows);
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int selectedRow = selectedRows[i];
                deleteFile(selectedRow);
            }
        }
    }

    private void initializeTableUI() {
        List<VaultFile> vaultFiles = vaultData.getFiles();
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return !(column <= 1);
            }
        };
        model.addColumn("Icon");
        model.addColumn("File Name");
        model.addColumn("Encrypted");
        model.addColumn("File Size");
        model.addColumn("");
        for (VaultFile vaultFile : vaultFiles) {
            String fileName = getFileName(vaultFile);
            File vFile = new File(Constant.VAULT_PATH + fileName);
            model.addRow(new Object[]{fileChooser.getUI().getFileView(fileChooser).getIcon(vFile), vaultFile.getDisplayName(), vaultFile.isLocked(), getStringSizeLengthFile(vFile.length()), vaultFile.getFileName()});
        }
        vaultFilesList = new JTable(model) {
            @Override
            public Class getColumnClass(int column) {
                return switch (column) {
                    case 0 -> Icon.class;
                    case 2 -> Boolean.class;
                    default -> String.class;
                };
            }
        };
        vaultFilesList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    if ((Boolean) model.getValueAt(row, 2)) {
                        return;
                    }
                    VaultFile vaultFile = vaultFiles.stream().filter(e -> e.getFileName().equals(model.getValueAt(row, 4))).toList().get(0);
                    openFile(Constant.VAULT_PATH + vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                }
            }
        });
        vaultFilesList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        vaultFilesList.getTableHeader().setResizingAllowed(false);
        vaultFilesList.getColumnModel().getColumn(0).setPreferredWidth(50);
        vaultFilesList.getColumnModel().getColumn(1).setPreferredWidth(300);
        vaultFilesList.getColumnModel().getColumn(2).setPreferredWidth(100);
        vaultFilesList.getColumnModel().getColumn(4).setPreferredWidth(0);
        vaultFilesList.getTableHeader().setReorderingAllowed(false);
        model.addTableModelListener(e -> {
            if (e.getFirstRow() >= model.getRowCount() || e.getFirstRow() < 0 || model.getRowCount() == 0 || e.getColumn() < 1) {
                return;
            }
            Boolean encrypted = (Boolean) model.getValueAt(e.getFirstRow(), 2);
            VaultFile vaultFile = vaultFiles.stream().filter(file -> file.getFileName().equals(model.getValueAt(e.getFirstRow(), 4))).toList().get(0);
            String fileName = !encrypted ? vaultFile.getFileName() : vaultFile.getDisplayName() + '.' + vaultFile.getExtension();
            if (encrypted && !vaultFile.isLocked()) {
                FileEncryptionService.encrypt(new File(Constant.VAULT_PATH + fileName));
                rename(fileName, vaultFile.getFileName());
                vaultFile.setLocked(true);
                updateVaultData();
            }
            if (!encrypted && vaultFile.isLocked()) {
                FileEncryptionService.decrypt(new File(Constant.VAULT_PATH + fileName));
                rename(fileName, vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                vaultFile.setLocked(false);
                updateVaultData();
            }
        });
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

    private void updateVaultData() {
        try {
            VaultDataService.writeVaultData(vaultData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteFile(int selectedRow) {
        if (selectedRow >= 0) {
            VaultFile vaultFile = vaultData.getFiles().stream().filter(file -> file.getFileName().equals(model.getValueAt(selectedRow, 4))).toList().get(0);
            String fileName = getFileName(vaultFile);
            vaultData.getFiles().remove(vaultFile);
            updateVaultData();
            new File(Constant.VAULT_PATH + fileName).delete();
            model.removeRow(selectedRow);
        }
    }

    private void addFile(JFileChooser fileChooser, File file) {
        String name = file.getName();
        String displayName = name.substring(0, name.lastIndexOf('.'));
        String extension = name.substring(name.lastIndexOf('.') + 1);
        String uuid = UUID.randomUUID().toString();
        File copyFile = new File(Constant.VAULT_PATH/* + File.separator*/ + uuid);
        file.renameTo(copyFile);
        FileEncryptionService.encrypt(copyFile);
        VaultFile vaultFile = new VaultFile();
        vaultFile.setFileName(uuid);
        vaultFile.setDisplayName(displayName);
        vaultFile.setExtension(extension);
        vaultFile.setLocked(true);
        vaultData.getFiles().add(vaultFile);
        updateVaultData();
        File vFile = new File(Constant.VAULT_PATH + uuid);
        model.addRow(new Object[]{fileChooser.getUI().getFileView(fileChooser).getIcon(
                vFile), displayName, true, getStringSizeLengthFile(vFile.length()), uuid});
    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb)
            return df.format(size / sizeKb) + " KB";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " GB";

        return "";
    }
}
