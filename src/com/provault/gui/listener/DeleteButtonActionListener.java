package com.provault.gui.listener;

import com.provault.gui.ProVaultFrame;
import com.provault.util.ProVaultUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class DeleteButtonActionListener implements ActionListener {

    private final ProVaultFrame proVaultFrame;

    public DeleteButtonActionListener(ProVaultFrame proVaultFrame) {
        this.proVaultFrame = proVaultFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int[] selectedRows = proVaultFrame.getVaultFilesList().getSelectedRows();
        Arrays.sort(selectedRows);
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int selectedRow = selectedRows[i];
            ProVaultUtil.deleteFile(selectedRow, proVaultFrame.getVaultData(), proVaultFrame.getModel(), proVaultFrame);
        }
    }
}
