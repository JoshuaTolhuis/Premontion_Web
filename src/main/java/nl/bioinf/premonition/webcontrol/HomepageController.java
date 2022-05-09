package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;

import javax.servlet.http.HttpSession;

/*
Author: Joshua Tolhuis
 */


@Controller
@RequestMapping(value="home")
public class HomepageController {
    @GetMapping
    public String getGreeting(Model model, HttpSession session) {
        model.addAttribute("greeting", "Welcome to the homepage");
        model.addAttribute("ID", String.format("Your session ID is: %s", SessionUtil.getUserID(session)));
        return "homepage";
    }

}