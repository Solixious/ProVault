package com.provault.gui;

import com.provault.constants.Constant;
import com.provault.model.Key;
import com.provault.model.VaultData;
import com.provault.model.VaultFile;
import com.provault.service.FileEncryptionService;
import com.provault.service.VaultDataService;
import com.provault.util.ProVaultUtil;

import javax.swing.*;
import javax.swing.border.Border;
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
import java.text.DecimalFormat;
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
    private static final String DECIMAL_PATTERN = "0.00";
    private static final String TITLE = "Pro Vault";

    private JFrame frame;
    private JToolBar toolBar;
    private JButton addFile, deleteFile, close;
    private JTable vaultFilesList;
    private DefaultTableModel model;
    private final VaultData vaultData;
    private final Key key;
    private final Border emptyBorder = BorderFactory.createEmptyBorder();

    public ProVaultFrame(final VaultData vaultData, Key key) {
        this.vaultData = vaultData;
        this.key = key;

        initializeUI();
        initializeTableUI();

        JScrollPane jScrollPane = new JScrollPane(vaultFilesList);
        jScrollPane.setBorder(emptyBorder);

        vaultFilesList.setBackground(Constant.COLOR_1);
        vaultFilesList.setForeground(Constant.COLOR_4);
        vaultFilesList.setShowHorizontalLines(false);
        vaultFilesList.setShowVerticalLines(false);
        vaultFilesList.setSelectionBackground(Constant.COLOR_3);
        vaultFilesList.setSelectionForeground(Constant.COLOR_4);
        jScrollPane.getViewport().setBackground(Constant.COLOR_1);
        toolBar.setBackground(Constant.COLOR_2);

        frame.add(toolBar, BorderLayout.WEST);
        frame.add(jScrollPane, BorderLayout.CENTER);
        frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Constant.CURSOR.getImage(), new Point(0, 0), "img"));
        frame.setVisible(true);
    }

    private void initializeUI() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setIconImage(Constant.ICON.getImage());
        frame.setUndecorated(true);

        Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(Constant.ICON.getImage());


        addFile = new JButton(Constant.ADD_ICON);
        addFile.addActionListener(this);
        addFile.setBorder(emptyBorder);
        addFile.setToolTipText("Add file to vault");
        deleteFile = new JButton(Constant.REMOVE_ICON);
        deleteFile.addActionListener(this);
        deleteFile.setBorder(emptyBorder);
        deleteFile.setToolTipText("Delete file from vault");
        close = new JButton(Constant.CLOSE_ICON);
        close.addActionListener(this);
        close.setBorder(emptyBorder);
        close.setToolTipText("Exit application");

        Dimension separatorDimension = new Dimension(0, 16);

        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.addSeparator(separatorDimension);
        toolBar.add(addFile);
        toolBar.addSeparator(separatorDimension);
        toolBar.add(deleteFile);
        toolBar.addSeparator(separatorDimension);
        toolBar.add(close);
        toolBar.addSeparator(separatorDimension);
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

    private void initializeTableUI() {
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
            model.addRow(new Object[]{getIcon(vaultFile), vaultFile.getDisplayName(), vaultFile.isLocked(), vaultFile.getCategory(), getStringSizeLengthFile(vFile.length()), vaultFile.getFileName()});
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
                    if ((Boolean) model.getValueAt(row, ENCRYPTED_STATUS_COLUMN)) {
                        return;
                    }
                    VaultFile vaultFile = vaultFiles.stream().filter(e -> e.getFileName().equals(model.getValueAt(row, FILE_NAME_COLUMN))).toList().get(0);
                    openFile(Constant.VAULT_PATH + vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                }
            }
        });
        vaultFilesList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        vaultFilesList.getTableHeader().setResizingAllowed(false);
        vaultFilesList.getColumnModel().getColumn(ICON_COLUMN).setPreferredWidth(50);
        vaultFilesList.getColumnModel().getColumn(DISPLAY_NAME_COLUMN).setPreferredWidth(300);
        vaultFilesList.getColumnModel().getColumn(ENCRYPTED_STATUS_COLUMN).setPreferredWidth(42);
        vaultFilesList.getColumnModel().getColumn(CATEGORY_COLUMN).setPreferredWidth(200);
        vaultFilesList.getColumnModel().getColumn(FILE_SIZE_COLUMN).setPreferredWidth(200);
        vaultFilesList.getColumnModel().getColumn(FILE_NAME_COLUMN).setPreferredWidth(0);
        vaultFilesList.getColumnModel().removeColumn(vaultFilesList.getColumnModel().getColumn(FILE_NAME_COLUMN));
        vaultFilesList.getTableHeader().setReorderingAllowed(false);
        vaultFilesList.setRowHeight(36);

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
        renderer.setBackground(Constant.COLOR_2);
        renderer.setForeground(Constant.COLOR_4);
        vaultFilesList.getTableHeader().setDefaultRenderer(renderer);
        vaultFilesList.setDefaultRenderer(String.class, cellRenderer);
        vaultFilesList.getTableHeader().setBackground(Constant.COLOR_2);

        DefaultCellEditor defaultCellEditor = (DefaultCellEditor) vaultFilesList.getDefaultEditor(Boolean.class);
        JCheckBox checkBox = (JCheckBox) defaultCellEditor.getComponent();
        checkBox.setIcon(Constant.UNLOCKED_ICON);
        checkBox.setSelectedIcon(Constant.LOCKED_ICON);

        TableCellRenderer tableCellRenderer = vaultFilesList.getDefaultRenderer(Boolean.class);
        checkBox = (JCheckBox) tableCellRenderer;
        checkBox.setIcon(Constant.UNLOCKED_ICON);
        checkBox.setSelectedIcon(Constant.LOCKED_ICON);

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
                    updateVaultData();
                }
                if (!encrypted && vaultFile.isLocked()) {
                    FileEncryptionService.decrypt(new File(Constant.VAULT_PATH + fileName), key);
                    rename(fileName, vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                    vaultFile.setLocked(false);
                    updateVaultData();
                }
            }
            if (e.getColumn() == DISPLAY_NAME_COLUMN) {
                String updatedDisplayName = (String) model.getValueAt(e.getFirstRow(), DISPLAY_NAME_COLUMN);
                String fileName = (String) model.getValueAt(e.getFirstRow(), FILE_NAME_COLUMN);
                VaultFile file = vaultData.getFiles().stream().filter(vaultFile -> vaultFile.getFileName().equals(fileName)).toList().get(0);
                if(!file.isLocked()) {
                    rename(file.getDisplayName() + "." + file.getExtension(), updatedDisplayName + "." + file.getExtension());
                }
                file.setDisplayName(updatedDisplayName);
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
            VaultFile vaultFile = vaultData.getFiles().stream().filter(file -> file.getFileName().equals(model.getValueAt(selectedRow, FILE_NAME_COLUMN))).toList().get(0);
            String fileName = getFileName(vaultFile);
            int ret = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete " + vaultFile.getDisplayName() + "?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Constant.QUESTION_ICON);
            if (ret == 0) {
                vaultData.getFiles().remove(vaultFile);
                updateVaultData();
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
        updateVaultData();
        File vFile = new File(Constant.VAULT_PATH + uuid);
        model.addRow(new Object[]{getIcon(vaultFile), displayName, true, vaultFile.getCategory(), getStringSizeLengthFile(vFile.length()), uuid});
    }

    private ImageIcon getIcon(VaultFile file) {
        String extension = file.getExtension().toLowerCase();
        return switch (extension) {
            case "png", "jpg", "jpeg", "bmp", "gif" -> Constant.PICTURE_ICON;
            case "mp4", "mov", "wmv", "avi", "flv" -> Constant.VIDEO_ICON;
            default -> Constant.DOCUMENT_ICON;
        };
    }

    public static String getStringSizeLengthFile(long size) {
        DecimalFormat df = new DecimalFormat(DECIMAL_PATTERN);
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
