package nl.bioinf.premonition.services;
import nl.bioinf.premonition.models.PremonitionForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
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
    @Value("${test.folder.location}")
    private String testDataLocation;

    /**
     * runs the python Premonotion script and prints any terminal out lines.
     * No returns. Future arguments are any arguments to be given to Premonition in the form of a string.
     */
    public String runScript(PremonitionForm form, String userid, HttpSession session){
        Process process;
        String toReturn = "";

        /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Premonition will add the .json file extension! Only give the session_name as file name!
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        try{
            String premonitionScript = "premonition.py";

            String scriptToExecute = String.format("%1$s -ef %2$s -rf %3$s -no %4$s -co %4$s" + "_COut_" + " -NRCs %5$s -re %6$s",
                    premonitionScript, form.getFile(), form.getRefFile(),
                    userid, form.getIncludeNRCs(), form.getRemoveEdges());

            //TODO Remove below test variables
            String testExecute = String.format("%1$s -ef %2$s -rf %3$s -no %4$s -co %4$s" + "_COut_" + " -NRCs %5$s -re %6$s",
                    premonitionScript, testDataLocation + "genes.txt", testDataLocation + "links.tsv",
                    "JsonOut/"+userid, true, true); //Temp var
            String testScript = "test.py -ef hey -rf hoi -no output -co output_cyto"; //temp var
            //TODO Remove above test variables


            String cmdline = "python3 " +  pyPath + testExecute; //Replace "testScript" variable with "scriptToExecute"
            process = Runtime.getRuntime().exec(cmdline);
            mProcess = process;

        }catch(Exception e) {
            System.out.println("Exception Raised" + e);
            return "Exception Raised" + e;
        }

        InputStream stdout = mProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
        String line;
        String allupdates = "";
        try{
            while((line = reader.readLine()) != null) {
                System.out.println(mProcess.getErrorStream());
                if(!allupdates.contains(line)) {
                    allupdates += line + "\n";
                }

                //Set status update in JSON format.
                session.setAttribute("status_update", "{\"status\":\""+allupdates+"\"}");

                System.out.println(line+"\n");
                toReturn += line+"\n";
            }
            return toReturn;
        }catch(IOException e){
            String error_message = "Exception in reading output "+ e;

            System.out.println(error_message);
            session.setAttribute("status_update", "{\"status\":\""+error_message+"\"}");
            return error_message;
        }

    }
}


