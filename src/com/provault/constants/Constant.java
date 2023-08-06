package com.provault.constants;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

/**
 * @author pratyush
 * This interface is responsible for holding all the miscellaneous constants of the application
 */
public interface Constant {
    // This is the vault's folder name in which all the files in vault are moved. Keep it separate for development and production use.
    String VAULT_FOLDER = ".test-vault";
    String VAULT_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + VAULT_FOLDER + File.separator;
    String DATA_FILE = VAULT_PATH + ".data";
    String KEY_FILE = VAULT_PATH + ".key";
    String ALGORITHM = "AES";
    String DECIMAL_PATTERN = "0.00";
    String TITLE = "Pro Vault";
    Integer BUFFER_SIZE = 4096;
    Integer ROW_HEIGHT = 36;
    Integer FONT_SIZE = 16;
    GridLayout OPTION_PANE_GRID_LAYOUT = new GridLayout(2, 1);
}
