package com.provault.gui.listener;

import com.provault.gui.ProVaultFrame;
import com.provault.util.ProVaultUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ExitButtonActionListener implements ActionListener {

    private final ProVaultFrame proVaultFrame;

    public ExitButtonActionListener(ProVaultFrame proVaultFrame) {
        this.proVaultFrame = proVaultFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        proVaultFrame.dispose();
    }
}
