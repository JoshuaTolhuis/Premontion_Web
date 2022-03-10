package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.services.HomepageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value="homepage")
public class HomepageController {
    private HomepageService homepageService;
    @GetMapping
    public String getGreeting(Model model) {
        model.addAttribute("greeting", "Welcome to the homepage");
        return "homepage";
    }

    @Autowired
    public HomepageController(HomepageService homepageService) {
        this.homepageService = homepageService;
    }
}