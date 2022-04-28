package nl.bioinf.premonition.models;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

/*
Author: Joshua Tolhuis
 */

@RestController
public class PremonitionForm {
    private MultipartFile file;
    private MultipartFile refFile;
    private String removeEdges;
    private String includeNRCs;
    private String limited;
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public MultipartFile getRefFile() {
        return refFile;
    }

    public void setRefFile(MultipartFile refFile) {
        this.refFile = refFile;
    }
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
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
