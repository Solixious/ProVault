package com.provault;

import com.provault.constants.Constant;
import com.provault.gui.ProVaultFrame;
import com.provault.service.VaultDataService;
import com.provault.util.ProVaultUIUtil;
import com.provault.util.ProVaultUtil;

import javax.swing.*;
import java.io.File;

public class ProVault {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProVaultUIUtil.initializeUIManager();
            String key = ProVaultUIUtil.getKeyFromUser("Enter Password:");
            ProVaultUtil.createPathIfMissing(new File(Constant.VAULT_PATH), new File(Constant.DATA_FILE));
            ProVaultUtil.validateKey(new File(Constant.KEY_FILE), ProVaultUtil.getHash(key, "sha1"));
            new ProVaultFrame(VaultDataService.generateVaultData(), ProVaultUtil.get16Bytes(ProVaultUtil.getHash(key, "md5")));
        });
    }
}
