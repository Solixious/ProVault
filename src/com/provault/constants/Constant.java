package com.provault.constants;

import com.provault.util.ProVaultUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

public interface Constant {

    String VAULT_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + ".test-vault" + File.separator;
    String DATA_FILE = VAULT_PATH + ".data";
    String KEY_FILE = VAULT_PATH + ".key";
    Color COLOR_1 = new Color(0xFAF0D7);
    Color COLOR_2 = new Color(0xFFD9C0);
    Color COLOR_3 = new Color(0xCCEEBC);
    Color COLOR_4 = Color.DARK_GRAY;
    ImageIcon ADD_ICON = ProVaultUtil.getIconAsResource("/img/add.png");
    ImageIcon CLOSE_ICON = ProVaultUtil.getIconAsResource("/img/close.png");
    ImageIcon ICON = ProVaultUtil.getIconAsResource("/img/icon.png");
    ImageIcon LOCKED_ICON = ProVaultUtil.getIconAsResource("/img/locked.png");
    ImageIcon QUESTION_ICON = ProVaultUtil.getIconAsResource("/img/question.png");
    ImageIcon REMOVE_ICON = ProVaultUtil.getIconAsResource("/img/remove.png");
    ImageIcon UNLOCKED_ICON = ProVaultUtil.getIconAsResource("/img/unlocked.png");
    ImageIcon VIDEO_ICON = ProVaultUtil.getIconAsResource("/img/video.png");
    ImageIcon DOCUMENT_ICON = ProVaultUtil.getIconAsResource("/img/document.png");
    ImageIcon PICTURE_ICON = ProVaultUtil.getIconAsResource("/img/picture.png");
    ImageIcon CURSOR = ProVaultUtil.getIconAsResource("/img/cursor.png");
}
