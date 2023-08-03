package com.provault;

import com.provault.constants.Constant;
import com.provault.gui.ProVaultFrame;
import com.provault.model.VaultData;
import com.provault.service.VaultDataService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ProVault {

    public static void main(String[] args) throws Exception {
        File vaultFolder = new File(Constant.VAULT_PATH);
        File dataFile = new File(Constant.DATA_FILE);
        createPathIfMissing(vaultFolder, dataFile);
        VaultData vaultData = VaultDataService.generateVaultData();
        ProVaultFrame frame = new ProVaultFrame(vaultData);
    }

    private static void createPathIfMissing(File vaultFolder, File dataFile) throws IOException {
        if(!vaultFolder.exists()) {
            Files.createDirectory(vaultFolder.toPath());
            System.out.println("Creating vault: " + Constant.VAULT_PATH);
        }
        if(!dataFile.exists()) {
            dataFile.createNewFile();
            System.out.println("Creating data file.");
        }
    }
}
