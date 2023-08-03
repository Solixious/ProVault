package com.provault.model;

public class VaultFile {

    private String fileName;
    private String displayName;
    private String extension;
    private boolean locked;

    //@Override
    public int compareTo(VaultFile o) {
        return fileName.compareTo(o.getFileName());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
