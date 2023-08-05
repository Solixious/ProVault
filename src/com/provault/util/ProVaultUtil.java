package com.provault.util;

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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.UUID;

public class ProVaultUtil {

    public static Key get16Bytes(String md5) {
        byte[] bytes = md5.getBytes();
        byte[] keyData = new byte[16];
        System.arraycopy(bytes, 0, keyData, 0, 16);
        return new Key(keyData);
    }

    public static void validateKey(File keyFile, String key) {
        try {
            if (!keyFile.exists()) {
                if (!keyFile.createNewFile()) {
                    log("Error creating key file...");
                    System.exit(-1);
                }
                BufferedWriter br = new BufferedWriter(new FileWriter(keyFile));
                br.write(key);
                br.close();
                return;
            }
            BufferedReader br = new BufferedReader(new FileReader(keyFile));
            if (!key.startsWith(br.readLine())) {
                log("Incorrect Password...");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createPathIfMissing(File vaultFolder, File dataFile) {
        try {
            if (!vaultFolder.exists()) {
                Files.createDirectory(vaultFolder.toPath());
                log("Creating vault: " + Constant.VAULT_PATH);
                System.exit(-1);
            }
            if (!dataFile.exists()) {
                if (!dataFile.createNewFile()) {
                    log("Error creating data file...");
                }
                log("Creating data file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getKeyFromUser(String text) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(text);
        JPasswordField pass = new JPasswordField(10);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, text,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        if (option == 0) {
            char[] password = pass.getPassword();
            if (password.length < 4)
                System.exit(0);

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

    public static String getHash(String key, String algorithm) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update(key.getBytes());
        return new String(messageDigest.digest());
    }

    public static void log(String logText) {
        System.out.println(logText);
    }

    public static ImageIcon getIconAsResource(String path) {
        try {
            InputStream inputStream = ProVaultUtil.class.getResourceAsStream(path);
            assert inputStream != null;
            return new ImageIcon(ImageIO.read(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringSizeLengthFile(long size) {
        DecimalFormat df = new DecimalFormat(Constant.DECIMAL_PATTERN);
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

    public static ImageIcon getIcon(VaultFile file) {
        String extension = file.getExtension().toLowerCase();
        return switch (extension) {
            case "png", "jpg", "jpeg", "bmp", "gif" -> Icons.PICTURE_ICON;
            case "mp4", "mov", "wmv", "avi", "flv" -> Icons.VIDEO_ICON;
            default -> Icons.DOCUMENT_ICON;
        };
    }

    public static void openFile(String fileName) {
        try {
            Desktop.getDesktop().open(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rename(String source, String newFileName) {
        Path sourcePath = Paths.get(Constant.VAULT_PATH + source);
        try {
            Files.move(sourcePath, sourcePath.resolveSibling(newFileName));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static  String getFileName(VaultFile vaultFile) {
        return vaultFile.isLocked() ? vaultFile.getFileName() : vaultFile.getDisplayName() + '.' + vaultFile.getExtension();
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
        vaultFile.setCategory(ProVaultUtil.getCategory(vaultData.getCategories()));
        vaultData.getFiles().add(vaultFile);
        VaultDataService.writeVaultData(vaultData);
        File vFile = new File(Constant.VAULT_PATH + uuid);
        model.addRow(new Object[]{ProVaultUtil.getIcon(vaultFile), displayName, true, vaultFile.getCategory(), ProVaultUtil.getStringSizeLengthFile(vFile.length()), uuid});
    }


    public static void deleteFile(int selectedRow, VaultData vaultData, ProVaultTableModel model, ProVaultFrame frame) {
        if (selectedRow >= 0) {
            VaultFile vaultFile = vaultData.getFiles().stream().filter(file -> file.getFileName().equals(model.getValueAt(selectedRow, ColumnIndex.FILE_NAME_COLUMN))).toList().get(0);
            String fileName = ProVaultUtil.getFileName(vaultFile);
            int ret = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " + vaultFile.getDisplayName() + "?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Icons.QUESTION_ICON);
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
}
