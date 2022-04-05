package nl.bioinf.premonition.models;

import java.io.File;
import java.util.Objects;

public class PremonitionForm {
    private File files;
    private String removeEdges;
    private String includeNRCs;
    private String limited;

    public File getFiles() {
        return files;
    }

    public void setFiles(File files) {
        this.files = files;
    }

    public String getRemoveEdges() {
        return removeEdges;
    }

    public void setRemoveEdges(String removeEdges) {
        this.removeEdges = removeEdges;
    }

    public String getIncludeNRCs() {
        return includeNRCs;
    }

    public void setIncludeNRCs(String includeNRCs) {
        this.includeNRCs = includeNRCs;
    }

    public String getLimited() {
        return limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PremonitionForm that = (PremonitionForm) o;
        return Objects.equals(files, that.files) && Objects.equals(removeEdges, that.removeEdges) && Objects.equals(includeNRCs, that.includeNRCs) && Objects.equals(limited, that.limited);
    }

    @Override
    public int hashCode() {
        return Objects.hash(files, removeEdges, includeNRCs, limited);
    }
}