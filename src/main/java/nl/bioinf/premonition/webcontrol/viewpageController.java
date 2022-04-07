package nl.bioinf.premonition.webcontrol;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class viewpageController {

    @GetMapping(value="viewpage")
    public String viewPage(){
        return "viewpage";
    }
}
