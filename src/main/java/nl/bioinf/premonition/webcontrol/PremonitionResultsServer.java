package nl.bioinf.premonition.webcontrol;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



/**
 * @author Joshua Tolhuis
 * @author Nils Mooldijk
 */
@RestController
public class PremonitionResultsServer {

    @Value("${data.out.location}")
    private String dataOutLocation;


    @GetMapping(value="check_results",
    produces = "text/plain")
   // @ResponseBody
    public String viewPage(@RequestParam(name = "user_id") String userId, HttpSession session){
        Response toReturn = new Response();
        toReturn.setMessage("");

        String dataFileLocation = String.format("%1$s%2$s.json", dataOutLocation, userId);
        File f = new File(dataFileLocation);
        if (f.exists()){
            try {
                String content = contentReader(dataFileLocation);
                toReturn.setData(content);
                toReturn.setMessage("Ready!");
                return content;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            //TODO remove the below line.
            String line = "working on it";
            session.setAttribute("status_update", "{\"status\":\""+line+"\"}");
            //TODO remove the above line.

            String status_update = session.getAttribute("status_update").toString();
            toReturn.setMessage(status_update);
        }

        return toReturn.message;
    }

    /**
     * Reads the response body and outputs it.
     * @param fileLocation location of JSON file.
     * @return String of JSON content.
     */
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

    /**
     * Holder results response with status message and JSON data.
     */
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


