package nl.bioinf.premonition.models;

import java.io.File;

public class CommandLineArguments {
    private File file;
    private Boolean flatNetwork;
    private Boolean networkLevel;

    public CommandLineArguments(File file, Boolean flatNetwork, Boolean networkLevel) {
        this.file = file;
        this.flatNetwork = flatNetwork;
        this.networkLevel = networkLevel;
    }

    public Boolean getNetworkLevel() {
        return networkLevel;
    }

    public void setNetworkLevel(Boolean networkLevel) {
        this.networkLevel = networkLevel;
    }

    public Boolean getFlatNetwork() {
        return flatNetwork;
    }

    public void setFlatNetwork(Boolean flatNetwork) {
        this.flatNetwork = flatNetwork;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
