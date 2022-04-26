package nl.bioinf.premonition.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PyProcessorTest {
    @Autowired
    private PyProcessor processor;

    @Test
    void testPython(){
        System.out.println("JUNIT TEST STARTED: processor test");
        System.out.println(processor.runScript());
    }

}