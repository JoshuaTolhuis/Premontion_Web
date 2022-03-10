package nl.bioinf.premonition.models;

import java.io.File;

public class Premonition {
    private File file;
    private Boolean flatNetwork;
    private Boolean trimNodes;

    public Boolean getTrimNodes() {
        return trimNodes;
    }

    public void setTrimNodes(Boolean trimNodes) {
        this.trimNodes = trimNodes;
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
