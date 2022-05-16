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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;



/*
Author: Joshua Tolhuis & Nils Mooldijk
 */


@RestController
public class PremonitionResultsServer {

    @Value("${data.out.location}")
    private String dataOutLocation;


    @GetMapping(value="check_results")
   // @ResponseBody
    public Response viewPage(@RequestParam(name = "user_id") String userId){
        Response toReturn = new Response();
        toReturn.setMessage("");

        File f = new File(String.format("%1$s%2$s.json", dataOutLocation, userId));
        if (f.exists()){
            toReturn.setData(f);
            toReturn.setMessage("Ready!");
        }
        else{
            toReturn.setMessage("Data not ready yet!");
        }

        return toReturn;
    }

    public static class Response{
        String message = "not ready";
        Object data;

        public void setMessage(String message) {
            this.message = message;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}


