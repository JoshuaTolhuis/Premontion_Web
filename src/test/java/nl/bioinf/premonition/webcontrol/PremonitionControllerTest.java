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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PremonitionController.class)
class PremonitionControllerTest {

    private MockMultipartFile file;
    private MockMultipartFile refFile;


    PremonitionControllerTest(MockMultipartFile file, MockMultipartFile refFile) {
        this.file = file;
        this.refFile = refFile;
    }

    @BeforeEach
    public void initEach() {
        System.out.println("i have started");
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
    }

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

        this.mockMvc.perform(multipart("/premonition")
                .file(file)
                .file(refFile)
                .param("limited", "3")
                .param("includeNRCs", "true")
                .param("removeEdges", "false"))
                .andExpect(status().isOk());
    }

    @Test
    public void fileDoesNotExistError() throws Exception{
        this.mockMvc.perform(multipart("/premonition")
                        .file(file) // the refFile isn't included so it should give an error
                        .param("limited", "3")
                        .param("includeNRCs", "true")
                        .param("removeEdges", "false"))
                .andExpect(status().isOk());

    }
}