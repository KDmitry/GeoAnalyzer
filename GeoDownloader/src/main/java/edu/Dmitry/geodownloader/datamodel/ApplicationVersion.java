package edu.Dmitry.geodownloader.datamodel;

public class ApplicationVersion {
    private boolean newVersion;
    private byte[] applicationPackage;

    public boolean isNewVersion() {
        return newVersion;
    }

    public void setNewVersionStatus(boolean newVersion) {
        this.newVersion = newVersion;
    }

    public byte[] getApplicationPackage() {
        return applicationPackage;
    }

    public void setApplicationPackage(byte[] applicationPackage) {
        this.applicationPackage = applicationPackage;
    }
}