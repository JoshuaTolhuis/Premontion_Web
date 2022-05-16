package nl.bioinf.premonition.webcontrol;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;



/*
Author: Joshua Tolhuis & Nils Mooldijk
 */


@RestController
public class PremonitionResultsServer {

    @Value("${data.out.location}")
    private String dataOutLocation;


    @GetMapping(value="check_results",
    produces = "text/plain")
   // @ResponseBody
    public String viewPage(@RequestParam(name = "user_id") String userId){
        Response toReturn = new Response();
        toReturn.setMessage("");

        String dataFileLocation = String.format("%1$s%2$s.json", dataOutLocation, userId);
        File f = new File(dataFileLocation);
        if (f.exists()){
            try {
                String content = contentReader(dataFileLocation);
                toReturn.setData(content);
                System.out.println(content);
                toReturn.setMessage("Ready!");
                return content;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            toReturn.setMessage("Data not ready yet!");
        }

        return "toReturn";
    }

    private String contentReader(String fileLocation){
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation)))
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public static class Response{
        String message = "not ready";
        String data;

        public void setMessage(String message) {
            this.message = message;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public String getData() {
            return data;
        }
    }
}


