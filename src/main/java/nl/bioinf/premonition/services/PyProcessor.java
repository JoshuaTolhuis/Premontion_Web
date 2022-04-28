package nl.bioinf.premonition.services;
import nl.bioinf.premonition.models.PremonitionForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.nio.charset.StandardCharsets;

/*
Author: Nils Mooldijk
 */



@RestController
public class PyProcessor {
    private Process mProcess;

    @Value("${data.folder.location}")
    private String genePath;
    @Value("${python.folder.location}")
    private String pyPath;

    /**
     * runs the python Premonotion script and prints any terminal out lines.
     * No returns. Future arguments are any arguments to be given to Premonition in the form of a string.
     */


    public String runScript(PremonitionForm form){
        Process process;
        String toReturn = "";
        try{
            String premonitionScript = "premonition.py";
            String testScript = "test.py -ef hey -rf hoi -no output -co output_cyto";                                //temp var

            String testProtein = StringUtils.cleanPath(form.getFile().getOriginalFilename());;                    //temp var
            String testReference =StringUtils.cleanPath(form.getRefFile().getOriginalFilename()); //temp var

            String cmdline = "python3 " +  pyPath + testScript; //+ " " + genePath+testProtein + " " + genePath+testReference;
            //System.out.println(cmdline);
            process = Runtime.getRuntime().exec(cmdline);
            mProcess = process;
           // toReturn += "\n CMDline: " + cmdline;

        }catch(Exception e) {
            System.out.println("Exception Raised" + e);
            return "Exception Raised" + e;
        }

        InputStream stdout = mProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
        String line;
        try{
            while((line = reader.readLine()) != null) {
                //System.out.println("PyProcessor: " + line);
                //System.out.println(mProcess.getErrorStream());
                toReturn += line+"\n";
            }
            return toReturn;
        }catch(IOException e){
            System.out.println("Exception in reading output "+ e);
            return "Exception in reading output "+ e;
        }

    }
}


