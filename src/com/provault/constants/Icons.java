package com.provault.constants;

import com.provault.util.ProVaultUtil;

import javax.swing.*;

/**
 * @author pratyush
 * This interface is responsible for loading and maintaining all the icons used in the application's UI
 */
public interface Icons {
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
