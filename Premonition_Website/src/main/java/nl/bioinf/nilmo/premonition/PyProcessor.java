package nl.bioinf.nilmo.premonition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@RestController

public class PyProcessor {
    Process mProcess;


    @Value("${data.folder.location}")
    private String genePath;

    public void runScript(){//String genefile, String proteinfile){
        Process process;
        System.out.println("Executing Python script.");
        System.out.println("genePath " + genePath);


        try{
            String premonitionScript = "src/main/resources/PythonFiles/premonition.py";
            String nisseTestFile = "src/main/resources/DEMOPythonScripts/test.py";              //temp var
            String testFile = "src/main/resources/PythonFiles/testCompare.py";                  //temp var
            String testProtein = "../DEMOProteinFiles/137 rhymic genes.txt";                    //temp var
            String testEvidence = "../DEMOProteinFiles/Sc_4932.protein.links.v11_filtered.tsv"; //temp var


            process = Runtime.getRuntime().exec(new String[]{premonitionScript, testProtein, testEvidence});
            mProcess = process;
        }catch(Exception e) {
            System.out.println("Exception Raised" + e);
        }

        InputStream stdout = mProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
        String line;
        try{
            while((line = reader.readLine()) != null){
                System.out.println("PyProcessor: "+ line);
              // mProcess.getErrorStream()


            }
        }catch(IOException e){
            System.out.println("Exception in reading output"+ e.toString());
        }
    }
}


