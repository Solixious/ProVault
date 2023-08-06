package com.provault.util;

import com.provault.constants.Colours;
import com.provault.constants.ColumnIndex;
import com.provault.constants.Constant;
import com.provault.constants.Icons;
import com.provault.gui.ProVaultFrame;
import com.provault.gui.ProVaultTableModel;
import com.provault.model.Key;
import com.provault.model.VaultData;
import com.provault.model.VaultFile;
import com.provault.service.FileEncryptionService;
import com.provault.service.VaultDataService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ProVaultUIUtil {

    public static String getKeyFromUser(String text) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(text);
        JPasswordField pass = new JPasswordField(16);
        panel.add(new JLabel(Icons.LOCK_ICON));
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, text, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (option == 0) {
            char[] password = pass.getPassword();
            if (password.length < 4) System.exit(0);

            return new String(password);
        }
        System.exit(0);
        return "";
    }

    public static String getCategory(Object[] categories) {
        JComboBox<Object> comboBox = new JComboBox<>(categories);
        comboBox.setEditable(true);
        JOptionPane.showMessageDialog(null, comboBox, "Category", JOptionPane.QUESTION_MESSAGE);
        return (String) comboBox.getSelectedItem();
    }

    public static void openFile(String fileName) {
        try {
            Desktop.getDesktop().open(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addFile(File file, Key key, VaultData vaultData, ProVaultTableModel model) {
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
        vaultFile.setCategory(ProVaultUIUtil.getCategory(vaultData.getCategories()));
        vaultData.getFiles().add(vaultFile);
        VaultDataService.writeVaultData(vaultData, key);
        File vFile = new File(Constant.VAULT_PATH + uuid);
        model.addRow(new Object[]{ProVaultUtil.getIcon(vaultFile), displayName, true, vaultFile.getCategory(), ProVaultUtil.getStringSizeLengthFile(vFile.length()), uuid});
    }

    public static void deleteFile(int selectedRow, VaultData vaultData, ProVaultTableModel model, ProVaultFrame frame, Key key) {
        if (selectedRow >= 0) {
            VaultFile vaultFile = vaultData.getFiles().stream().filter(file -> file.getFileName().equals(model.getValueAt(selectedRow, ColumnIndex.FILE_NAME_COLUMN))).toList().get(0);
            String fileName = ProVaultUtil.getFileName(vaultFile);
            int ret = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " + vaultFile.getDisplayName() + "?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Icons.QUESTION_ICON);
            if (ret == 0) {
                vaultData.getFiles().remove(vaultFile);
                VaultDataService.writeVaultData(vaultData, key);
                if (new File(Constant.VAULT_PATH + fileName).delete()) {
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showConfirmDialog(frame, "File deletion failed", "File deletion failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void initializeUIManager() {
        UIManager.put("ToolTip.background", Colours.COLOR_3);
        UIManager.put("ToolTip.foreground", Colours.COLOR_4);
    }
}
