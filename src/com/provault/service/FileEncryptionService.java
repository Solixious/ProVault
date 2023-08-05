package com.provault.service;

import com.provault.constants.Constant;
import com.provault.model.Key;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;

/**
 * @author pratyush
 * This class is responsible for encrypting and decrypting files using the given key.
 * The algorithm used is AES 256 bit for encryption
 */
public class FileEncryptionService {

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
            SecretKeySpec secretKey = new SecretKeySpec(key.data(), Constant.ALGORITHM);
            Cipher cipher = Cipher.getInstance(Constant.ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] buff = new byte[Constant.BUFFER_SIZE];
            long streamed = 0;
            for (int readBytes = inputStream.read(buff); readBytes > -1; readBytes = inputStream.read(buff), streamed += Constant.BUFFER_SIZE) {
                outputStream.write(cipher.update(buff, 0, readBytes));
            }
            outputStream.write(cipher.doFinal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encryptToNewFile(final File inputFile, final File outputFile, Key key) {
        try (FileInputStream inputStream = new FileInputStream(inputFile); FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            SecretKeySpec secretKey = new SecretKeySpec(key.data(), Constant.ALGORITHM);
            Cipher cipher = Cipher.getInstance(Constant.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] inputBytes = new byte[Constant.BUFFER_SIZE];
            long streamed = 0;
            for (int n = inputStream.read(inputBytes); n > 0; n = inputStream.read(inputBytes), streamed += Constant.BUFFER_SIZE) {
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
