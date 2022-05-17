package nl.bioinf.premonition.services;

import nl.bioinf.premonition.models.PremonitionForm;
import nl.bioinf.premonition.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpSession;

@SpringBootTest
class PyProcessorTest {
    @Value("${test.folder.location}")
    private String testDataLocation;

    @Autowired
    private PyProcessor processor;

    @Test
    void testPython(){
        HttpSession session = null;
        PremonitionForm form = new PremonitionForm();
        processor.runScript(form, SessionUtil.getUserID(session), session);
        System.out.println("JUNIT TEST STARTED: processor test");
    }

}