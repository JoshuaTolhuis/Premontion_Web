package nl.bioinf.premonition.webcontrol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadControllerTest {
    private PremonitionController uploadController;


    @BeforeEach
    void createUploadTestingObject(){
        System.out.println("Creating new object");
        this.uploadController = new PremonitionController();
    }

    @Test
    void uploadValidFile() {


    }
}