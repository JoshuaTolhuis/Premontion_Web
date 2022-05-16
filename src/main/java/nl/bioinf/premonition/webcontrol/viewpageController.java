package nl.bioinf.premonition.webcontrol;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/*
Author: Joshua Tolhuis
 */


@Controller
public class viewpageController {

    @GetMapping(value="viewpage")
    public String viewPage(HttpSession session){

        // Session information
        session.getId();

        // Get the status either 'running' or 'finished', provides data to two different AJAX calls
        //String type = getParameter("status");

        //return page if finished
        
        //check again if running


        return "viewpage";
    }
}
