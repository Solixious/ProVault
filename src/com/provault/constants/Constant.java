package com.provault.constants;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public interface Constant {

    String VAULT_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + ".vault" + File.separator;
    String DATA_FILE = VAULT_PATH + ".data";
}
