package nl.bioinf.premonition.webcontrol;
import nl.bioinf.premonition.util.SessionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class DownloadController {

    @Value("${data.out.location}")
    String filelocation;

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(HttpSession session) throws IOException {
        String userid = SessionUtil.getUserID(session);

        // TODO change this to the real code once implemented fully
        // File downloadableFile = new File(filelocation+userid+".json");

        File downloadableFile = new File(filelocation+"123456"+".json");
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