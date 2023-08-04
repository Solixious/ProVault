package com.provault.constants;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public interface Constant {

    String VAULT_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + ".vault" + File.separator;
    String DATA_FILE = VAULT_PATH + ".data";
    String KEY_FILE = VAULT_PATH + ".key";
    ImageIcon ADD_ICON = new ImageIcon("img/add.png");
    ImageIcon CLOSE_ICON = new ImageIcon("img/close.png");
    ImageIcon ICON = new ImageIcon("img/icon.png");
    ImageIcon LOCKED_ICON = new ImageIcon("img/locked.png");
    ImageIcon QUESTION_ICON = new ImageIcon("img/question.png");
    ImageIcon REMOVE_ICON = new ImageIcon("img/remove.png");
    ImageIcon UNLOCKED_ICON = new ImageIcon("img/unlocked.png");
}
