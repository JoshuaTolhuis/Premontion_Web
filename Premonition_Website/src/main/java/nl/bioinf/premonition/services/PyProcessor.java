package nl.bioinf.premonition.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
public class PyProcessor {
    private Process mProcess;


    @Value("${data.folder.location}")
    private String genePath;
    @Value("${python.folder.location}")
    private String pyPath;

    public void runScript(){//String genefile, String proteinfile){
        Process process;

        try{
            String premonitionScript = "premonition.py";
            String testProtein = "137RhymicGenes.txt";                    //temp var
            String testReference = "Sc_4932.protein.links.v11_filtered.tsv"; //temp var

            String cmdline = "python3 " +  pyPath+premonitionScript + " " + genePath+testProtein + " " + genePath+testReference;
            process = Runtime.getRuntime().exec(cmdline);
            mProcess = process;

        }catch(Exception e) {
            System.out.println("Exception Raised" + e);
        }

        InputStream stdout = mProcess.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
        String line;
        try{
            while((line = reader.readLine()) != null){
                System.out.println("PyProcessor: " + line);
                System.out.println(mProcess.getErrorStream());
            }

        }catch(IOException e){
            System.out.println("Exception in reading output"+ e);

        }
    }
}


