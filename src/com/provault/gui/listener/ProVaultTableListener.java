package com.provault.gui.listener;

import com.provault.constants.ColumnIndex;
import com.provault.constants.Constant;
import com.provault.gui.ProVaultFrame;
import com.provault.model.VaultFile;
import com.provault.service.FileEncryptionService;
import com.provault.service.VaultDataService;
import com.provault.util.ProVaultUtil;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ProVaultTableListener extends MouseAdapter implements TableModelListener {

    private final ProVaultFrame proVaultFrame;

    public ProVaultTableListener(ProVaultFrame proVaultFrame) {
        this.proVaultFrame = proVaultFrame;
    }

    public void mousePressed(MouseEvent mouseEvent) {
        JTable table = (JTable) mouseEvent.getSource();
        Point point = mouseEvent.getPoint();
        int row = table.rowAtPoint(point);
        if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
            if ((Boolean) proVaultFrame.getModel().getValueAt(row, ColumnIndex.ENCRYPTED_STATUS_COLUMN)) {
                return;
            }
            VaultFile vaultFile = proVaultFrame.getVaultData().getFiles().stream().filter(e -> e.getFileName().equals(proVaultFrame.getModel().getValueAt(row, ColumnIndex.FILE_NAME_COLUMN))).toList().get(0);
            ProVaultUtil.openFile(Constant.VAULT_PATH + vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getFirstRow() >= proVaultFrame.getModel().getRowCount() || e.getFirstRow() < 0 || proVaultFrame.getModel().getRowCount() == 0 || e.getColumn() < 1) {
            return;
        }
        if (e.getColumn() == ColumnIndex.ENCRYPTED_STATUS_COLUMN) {
            Boolean encrypted = (Boolean) proVaultFrame.getModel().getValueAt(e.getFirstRow(), ColumnIndex.ENCRYPTED_STATUS_COLUMN);
            VaultFile vaultFile = proVaultFrame.getVaultData().getFiles().stream().filter(file -> file.getFileName().equals(proVaultFrame.getModel().getValueAt(e.getFirstRow(), ColumnIndex.FILE_NAME_COLUMN))).toList().get(0);
            String fileName = !encrypted ? vaultFile.getFileName() : vaultFile.getDisplayName() + '.' + vaultFile.getExtension();
            if (encrypted && !vaultFile.isLocked()) {
                FileEncryptionService.encrypt(new File(Constant.VAULT_PATH + fileName), proVaultFrame.getKey());
                ProVaultUtil.rename(fileName, vaultFile.getFileName());
                vaultFile.setLocked(true);
                VaultDataService.writeVaultData(proVaultFrame.getVaultData());
            }
            if (!encrypted && vaultFile.isLocked()) {
                FileEncryptionService.decrypt(new File(Constant.VAULT_PATH + fileName), proVaultFrame.getKey());
                ProVaultUtil.rename(fileName, vaultFile.getDisplayName() + '.' + vaultFile.getExtension());
                vaultFile.setLocked(false);
                VaultDataService.writeVaultData(proVaultFrame.getVaultData());
            }
        }
        if (e.getColumn() == ColumnIndex.DISPLAY_NAME_COLUMN) {
            String updatedDisplayName = (String) proVaultFrame.getModel().getValueAt(e.getFirstRow(), ColumnIndex.DISPLAY_NAME_COLUMN);
            String fileName = (String) proVaultFrame.getModel().getValueAt(e.getFirstRow(), ColumnIndex.FILE_NAME_COLUMN);
            VaultFile file = proVaultFrame.getVaultData().getFiles().stream().filter(vaultFile -> vaultFile.getFileName().equals(fileName)).toList().get(0);
            if (!file.isLocked()) {
                ProVaultUtil.rename(file.getDisplayName() + "." + file.getExtension(), updatedDisplayName + "." + file.getExtension());
            }
            file.setDisplayName(updatedDisplayName);
            VaultDataService.writeVaultData(proVaultFrame.getVaultData());
        }
    }
}
