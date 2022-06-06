package nl.bioinf.premonition.webcontrol;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

/*
Author: Joshua Tolhuis
 */


@Controller
public class viewpageController {

    @GetMapping(value="viewpage")
    public String viewPage(HttpSession session){

        return "viewpage";
    }

    @PostMapping("/viewpage")
    public RedirectView greetingSubmit() {
        return new RedirectView("download");
    }
}
