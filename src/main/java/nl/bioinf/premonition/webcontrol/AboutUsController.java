package nl.bioinf.premonition.webcontrol;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutUsController {

    @GetMapping(value="aboutus")
    public String viewPage(){
        return "aboutus";
    }
}