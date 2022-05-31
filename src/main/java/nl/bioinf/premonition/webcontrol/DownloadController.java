package nl.bioinf.premonition.webcontrol;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class DownloadController {
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(String param) throws IOException {
        File downloadableFile = new File("/homes/jotolhuis/Documents/Minor/tests/src/main/resources/downloads/testfile.txt");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadableFile));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(downloadableFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}