package com.provault;

import com.provault.constants.Constant;
import com.provault.gui.ProVaultFrame;
import com.provault.model.VaultData;
import com.provault.service.VaultDataService;
import com.provault.util.ProVaultUtil;

import javax.swing.*;
import java.io.File;

public class ProVault {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String key = ProVaultUtil.getKeyFromUser("Enter Password:");
            String sha1 = ProVaultUtil.getHash(key, "sha1");
            String md5 = ProVaultUtil.getHash(key, "md5");
            File vaultFolder = new File(Constant.VAULT_PATH);
            File dataFile = new File(Constant.DATA_FILE);
            File keyFile = new File(Constant.KEY_FILE);
            ProVaultUtil.createPathIfMissing(vaultFolder, dataFile);
            ProVaultUtil.validateKey(keyFile, sha1);
            VaultData vaultData = VaultDataService.generateVaultData();
            new ProVaultFrame(vaultData, ProVaultUtil.get16Bytes(md5));
        });
    }
}
