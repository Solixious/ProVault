package com.provault.gui;

import com.provault.constants.ColumnIndex;
import com.provault.constants.Constant;
import com.provault.model.VaultFile;
import com.provault.util.ProVaultUtil;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.List;

public class ProVaultTableModel extends DefaultTableModel {
    public ProVaultTableModel(List<VaultFile> vaultFiles) {
        super();
        addColumn("Icon");
        addColumn("File Name");
        addColumn("Lock");
        addColumn("Category");
        addColumn("File Size");
        addColumn("");
        for (VaultFile vaultFile : vaultFiles) {
            String fileName = ProVaultUtil.getFileName(vaultFile);
            File vFile = new File(Constant.VAULT_PATH + fileName);
            addRow(new Object[]{ProVaultUtil.getIcon(vaultFile), vaultFile.getDisplayName(), vaultFile.isLocked(), vaultFile.getCategory(), ProVaultUtil.getStringSizeLengthFile(vFile.length()), vaultFile.getFileName()});
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == ColumnIndex.ENCRYPTED_STATUS_COLUMN || column == ColumnIndex.DISPLAY_NAME_COLUMN;
    }
}
