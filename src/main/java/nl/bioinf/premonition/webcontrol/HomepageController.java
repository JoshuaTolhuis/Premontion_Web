package nl.bioinf.premonition.webcontrol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
Author: Joshua Tolhuis
 */


@Controller
@RequestMapping(value="home")
public class HomepageController {
    @GetMapping
    public String getGreeting(Model model) {
        model.addAttribute("greeting", "Welcome to the homepage");
        return "homepage";
    }

}