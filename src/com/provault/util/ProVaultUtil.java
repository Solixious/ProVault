package com.provault.util;

import com.provault.constants.Constant;
import com.provault.model.Key;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProVaultUtil {

    public static Key get16Bytes(String md5) {
        byte[] bytes = md5.getBytes();
        byte[] keyData = new byte[16];
        System.arraycopy(bytes, 0, keyData, 0, 16);
        Key key = new Key();
        key.data = keyData;
        return key;
    }

    public static void validateKey(File keyFile, String key) throws IOException {
        if(!keyFile.exists()) {
            if(!keyFile.createNewFile()) {
                log("Error creating key file...");
                System.exit(-1);
            }
            BufferedWriter br = new BufferedWriter(new FileWriter(keyFile));
            br.write(key);
            br.close();
            return;
        }
        BufferedReader br = new BufferedReader(new FileReader(keyFile));
        if(!key.startsWith(br.readLine())) {
            log("Incorrect Password...");
            System.exit(0);
        }
    }

    public static void createPathIfMissing(File vaultFolder, File dataFile) throws IOException {
        if(!vaultFolder.exists()) {
            Files.createDirectory(vaultFolder.toPath());
            log("Creating vault: " + Constant.VAULT_PATH);
            System.exit(-1);
        }
        if(!dataFile.exists()) {
            if(!dataFile.createNewFile()) {
                log("Error creating data file...");
            }
            log("Creating data file.");
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
        if(option == 0)
        {
            char[] password = pass.getPassword();
            if(password.length < 4)
                System.exit(0);

            return new String(password);
        }
        System.exit(0);
        return "";
    }

    public static String getHash(String key, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(key.getBytes());
        return new String(messageDigest.digest());
    }
    
    public static void log(String logText) {
        System.out.println(logText);
    }
}
