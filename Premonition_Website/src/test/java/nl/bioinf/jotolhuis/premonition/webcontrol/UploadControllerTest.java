package nl.bioinf.jotolhuis.premonition.webcontrol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class UploadControllerTest {
    private UploadController uploadController;


    @BeforeEach
    void createUploadTestingObject(){
        System.out.println("Creating new object");
        this.uploadController = new UploadController();
    }

    @Test
    void uploadValidFile() {


    }
}