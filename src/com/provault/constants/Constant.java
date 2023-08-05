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
}
