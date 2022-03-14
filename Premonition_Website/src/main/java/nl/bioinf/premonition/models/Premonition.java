package nl.bioinf.premonition.models;

import java.io.File;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "Premonition{" +
                "file=" + file +
                ", flatNetwork=" + flatNetwork +
                ", trimNodes=" + trimNodes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Premonition that = (Premonition) o;
        return Objects.equals(file, that.file) && Objects.equals(flatNetwork, that.flatNetwork) && Objects.equals(trimNodes, that.trimNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, flatNetwork, trimNodes);
    }
}
