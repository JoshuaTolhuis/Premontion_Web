package nl.bioinf.premonition.models;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class PremonitionForm {
    private MultipartFile[] files;
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

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
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
        return Arrays.equals(files, that.files) && Objects.equals(removeEdges, that.removeEdges) && Objects.equals(includeNRCs, that.includeNRCs) && Objects.equals(limited, that.limited) && Objects.equals(test, that.test);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(removeEdges, includeNRCs, limited, test);
        result = 31 * result + Arrays.hashCode(files);
        return result;
    }

    @Override
    public String toString() {
        return "PremonitionForm{" +
                "files=" + Arrays.toString(files) +
                ", removeEdges='" + removeEdges + '\'' +
                ", includeNRCs='" + includeNRCs + '\'' +
                ", limited='" + limited + '\'' +
                ", test='" + test + '\'' +
                '}';
    }
}
