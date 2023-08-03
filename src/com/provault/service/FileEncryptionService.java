package com.provault.service;

import com.provault.model.Key;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.*;
import java.io.*;

public class FileEncryptionService {

    //private static final String KEY = "You're an idiot!";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void encrypt(final File inputFile, Key key) {
        File encryptedFile = new File(inputFile.getAbsolutePath() + ".encrypted");
        encryptToNewFile(inputFile, encryptedFile, key);
        renameToOldFilename(inputFile, encryptedFile);
    }

    public static void decrypt(final File inputFile, Key key) {
        File decryptedFile = new File(inputFile.getAbsolutePath() + ".decrypted");
        decryptToNewFile(inputFile, decryptedFile, key);
        renameToOldFilename(inputFile, decryptedFile);
    }

    private static void decryptToNewFile(final File input, final File output, Key key) {
        try (FileInputStream inputStream = new FileInputStream(input); FileOutputStream outputStream = new FileOutputStream(output)) {
            SecretKeySpec secretKey = new SecretKeySpec(key.data, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] buff = new byte[1024];
            long streamed = 0;
            for (int readBytes = inputStream.read(buff); readBytes > -1; readBytes = inputStream.read(buff), streamed += 1024) {
                outputStream.write(cipher.update(buff, 0, readBytes));
            }
            outputStream.write(cipher.doFinal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encryptToNewFile(final File inputFile, final File outputFile, Key key) {
        try (FileInputStream inputStream = new FileInputStream(inputFile); FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            SecretKeySpec secretKey = new SecretKeySpec(key.data, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] inputBytes = new byte[4096];
            long streamed = 0;
            for (int n = inputStream.read(inputBytes); n > 0; n = inputStream.read(inputBytes), streamed += 4096) {
                byte[] outputBytes = cipher.update(inputBytes, 0, n);
                outputStream.write(outputBytes);
            }
            byte[] outputBytes = cipher.doFinal();
            outputStream.write(outputBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renameToOldFilename(final File oldFile, final File newFile) {
        if (oldFile.exists()) {
            oldFile.delete();
        }
        newFile.renameTo(oldFile);
    }
}
