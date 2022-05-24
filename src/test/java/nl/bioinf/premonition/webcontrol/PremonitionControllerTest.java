package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.services.PyProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PremonitionController.class)
class PremonitionControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PyProcessor pyProcessor;


    /*
    tests to be made for file upload:
    1. sunny day scenario
    2. is file empty?
    3. is file readable?
    4. does file "exist"? (is input not null)
     */

    @Test
    void premonitionShouldStartTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        MockMultipartFile refFile = new MockMultipartFile(
                "refFile",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        this.mockMvc.perform(multipart("/premonition")
                .file(file)
                .file(refFile)
                .param("limited", "3")
                .param("includeNRCs", "true")
                .param("removeEdges", "false"))
                .andExpect(status().isOk());
    }

    @Test
    public void fileDoesNotExistE_thenAssertFileNotFoundError() throws Exception{
        assertThrows(FileNotFoundException.class, () -> {
            File file = new File("thisfiledoesntexist");
            BufferedReader br = new BufferedReader(new FileReader(file));
        });
    }

    /*
    public void createFileTest() throws Exception {

    }
     */
}