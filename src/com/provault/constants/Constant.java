package com.provault.constants;

import com.provault.util.ProVaultUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public interface Constant {

    String VAULT_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + ".vault" + File.separator;
    String DATA_FILE = VAULT_PATH + ".data";
    String KEY_FILE = VAULT_PATH + ".key";
    ImageIcon ADD_ICON = ProVaultUtil.getIconAsResource("/img/add.png");
    ImageIcon CLOSE_ICON = ProVaultUtil.getIconAsResource("/img/close.png");
    ImageIcon ICON = ProVaultUtil.getIconAsResource("/img/icon.png");
    ImageIcon LOCKED_ICON = ProVaultUtil.getIconAsResource("/img/locked.png");
    ImageIcon QUESTION_ICON = ProVaultUtil.getIconAsResource("/img/question.png");
    ImageIcon REMOVE_ICON = ProVaultUtil.getIconAsResource("/img/remove.png");
    ImageIcon UNLOCKED_ICON = ProVaultUtil.getIconAsResource("/img/unlocked.png");
}
