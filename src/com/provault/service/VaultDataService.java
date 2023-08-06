package com.provault.service;

import com.provault.constants.Constant;
import com.provault.model.Key;
import com.provault.model.VaultData;
import com.provault.model.VaultFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VaultDataService {

    public static final String SEPARATOR = ":::";
    public static final String TRUE = "1";
    public static final String FALSE = "0";

    public static VaultData generateVaultData(Key key) {
        try {
            File dataFile = new File(Constant.DATA_FILE);
            FileEncryptionService.decrypt(dataFile, key);
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            VaultData vaultData = new VaultData();
            List<VaultFile> files = new ArrayList<>();
            vaultData.setFiles(files);
            String data;
            while ((data = br.readLine()) != null) {
                if ("".equals(data)) {
                    break;
                }
                VaultFile vaultFile = getVaultFile(data);
                files.add(vaultFile);
            }
            FileEncryptionService.encrypt(dataFile, key);
            files.sort(Comparator.comparing(VaultFile::getCategory).thenComparing(VaultFile::getDisplayName));
            return vaultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeVaultData(VaultData vaultData, Key key) {
        vaultData.getFiles().sort(Comparator.comparing(VaultFile::getDisplayName));
        try {
            File dataFile = new File(Constant.DATA_FILE);
            BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile, false));
            List<VaultFile> files = vaultData.getFiles();
            for (VaultFile file : files) {
                bw.write(getString(file) + "\n");
            }
            bw.close();
            FileEncryptionService.encrypt(dataFile, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static VaultFile getVaultFile(String data) {
        String[] dataArray = data.split(SEPARATOR);
        VaultFile vaultFile = new VaultFile();
        vaultFile.setFileName(dataArray[0]);
        vaultFile.setDisplayName(dataArray[1]);
        vaultFile.setExtension(dataArray[2]);
        vaultFile.setLocked("1".equals(dataArray[3]));
        vaultFile.setCategory(dataArray[4]);
        return vaultFile;
    }

    private static String getString(VaultFile vaultFile) {
        return vaultFile.getFileName() + SEPARATOR
                + vaultFile.getDisplayName() + SEPARATOR
                + vaultFile.getExtension() + SEPARATOR
                + ((vaultFile.isLocked() ? TRUE : FALSE) + SEPARATOR)
                + vaultFile.getCategory();
    }
}
