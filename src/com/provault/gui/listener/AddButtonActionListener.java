package com.provault.gui.listener;

import com.provault.gui.ProVaultFrame;
import com.provault.util.ProVaultUIUtil;
import com.provault.util.ProVaultUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AddButtonActionListener implements ActionListener {

    private final ProVaultFrame proVaultFrame;

    public AddButtonActionListener(ProVaultFrame proVaultFrame) {
        this.proVaultFrame = proVaultFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int ret = proVaultFrame.getFileChooser().showOpenDialog(proVaultFrame);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File[] files = proVaultFrame.getFileChooser().getSelectedFiles();
            for (File file : files) {
                ProVaultUIUtil.addFile(file, proVaultFrame.getKey(), proVaultFrame.getVaultData(), proVaultFrame.getModel());
            }
        }
    }
}
