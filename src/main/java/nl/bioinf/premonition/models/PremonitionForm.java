package nl.bioinf.premonition.models;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/*
Author: Joshua Tolhuis
 */

@RestController
public class PremonitionForm {
    private MultipartFile file;
    private MultipartFile refFile;
    private Boolean removeEdges;
    private Boolean includeNRCs;
    private String limited;
    private String test;

    public MultipartFile getRefFile() {
        return refFile;
    }

    public MultipartFile getFile() {
        return file;
    }

    public Boolean getRemoveEdges() {
        return removeEdges;
    }

    public Boolean getIncludeNRCs() {
        return includeNRCs;
    }

    public String getLimited() {
        return limited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PremonitionForm that = (PremonitionForm) o;
        return Objects.equals(file, that.file) && Objects.equals(refFile, that.refFile) && Objects.equals(removeEdges, that.removeEdges) && Objects.equals(includeNRCs, that.includeNRCs) && Objects.equals(limited, that.limited) && Objects.equals(test, that.test);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(file, refFile, removeEdges, includeNRCs, limited, test);
    }

    @Override
    public String toString() {
        return "PremonitionForm{" +
                "file=" + file +
                ", refFile=" + refFile +
                ", removeEdges='" + removeEdges + '\'' +
                ", includeNRCs='" + includeNRCs + '\'' +
                ", limited='" + limited + '\'' +
                ", test='" + test + '\'' +
                '}';
    }
}
