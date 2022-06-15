package nl.bioinf.premonition.models;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/*
Author: Joshua Tolhuis
 */

public class PremonitionForm {
    private MultipartFile file;
    private MultipartFile refFile;
    private Boolean removeEdges;
    private Boolean includeNRCs;
    private String limited;



    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getRefFile() {
        return refFile;
    }

    public void setRefFile(MultipartFile refFile) {
        this.refFile = refFile;
    }

    public Boolean getRemoveEdges() {
        return removeEdges;
    }

    public void setRemoveEdges(Boolean removeEdges) {
        this.removeEdges = removeEdges;
    }

    public Boolean getIncludeNRCs() {
        return includeNRCs;
    }

    public void setIncludeNRCs(Boolean includeNRCs) {
        this.includeNRCs = includeNRCs;
    }

    public String getLimited() {
        return limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }

    @Override
    public String toString() {
        return "PremonitionForm{" +
                "file=" + file +
                ", refFile=" + refFile +
                ", removeEdges=" + removeEdges +
                ", includeNRCs=" + includeNRCs +
                ", limited='" + limited + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PremonitionForm that = (PremonitionForm) o;
        return file.equals(that.file) && refFile.equals(that.refFile) && removeEdges.equals(that.removeEdges) && includeNRCs.equals(that.includeNRCs) && limited.equals(that.limited);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, refFile, removeEdges, includeNRCs, limited);
    }


}
