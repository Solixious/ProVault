package com.provault.util;

import com.provault.constants.Constant;
import com.provault.constants.Icons;
import com.provault.model.Key;
import com.provault.model.VaultFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

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
        if (size < sizeMb) return df.format(size / sizeKb) + " KB";
        else if (size < sizeGb) return df.format(size / sizeMb) + " MB";
        else if (size < sizeTerra) return df.format(size / sizeGb) + " GB";

        return "";
    }

    public static ImageIcon getIcon(VaultFile file) {
        String extension = file.getExtension().toLowerCase();
        return switch (extension) {
            case "png", "jpg", "jpeg", "bmp", "gif" -> Icons.PICTURE_ICON;
            case "mp4", "mov", "wmv", "avi", "flv", "mpeg", "mkv", "webm", "3gp" -> Icons.VIDEO_ICON;
            default -> Icons.DOCUMENT_ICON;
        };
    }

    public static void rename(String source, String newFileName) {
        Path sourcePath = Paths.get(Constant.VAULT_PATH + source);
        try {
            Files.move(sourcePath, sourcePath.resolveSibling(newFileName));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getFileName(VaultFile vaultFile) {
        return vaultFile.isLocked() ? vaultFile.getFileName() : vaultFile.getDisplayName() + '.' + vaultFile.getExtension();
    }
}
