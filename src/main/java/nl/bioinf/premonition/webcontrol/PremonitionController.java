package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.models.PremonitionForm;
import nl.bioinf.premonition.services.PremonitionStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/*
Author: Joshua Tolhuis
 */


@Controller
public class PremonitionController {

    private final PremonitionStarter premonitionStarter;

    @Autowired
    public PremonitionController(PremonitionStarter premonitionStarter) {
        this.premonitionStarter = premonitionStarter;
    }

    @GetMapping(value = "premonition")
    public String premonitionPage(ModelMap modelMap){
        modelMap.addAttribute("result", new PremonitionForm());
        return "premonition";
    }

    @PostMapping(value = "premonition")
    public String uploadFile(@RequestParam MultipartFile[] files, @RequestParam String includeNRCs,
                             @RequestParam String removeEdges, @RequestParam String limited, @RequestParam String test, Model model) {
        // normalize the file path
        model.addAttribute("files", files);
        model.addAttribute("includeNRCs", includeNRCs);
        model.addAttribute("removeEdges", removeEdges);
        model.addAttribute("limited", limited);
        model.addAttribute("test", test);
        premonitionStarter.callPyProcessor();
        return "viewpage";
    }

//    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//    // save the file on the local file system
//        try {
//            String UPLOAD_DIR = "src/main/resources/uploads/";
//            Path path = Paths.get(UPLOAD_DIR + fileName);
//        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//    } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // return success response
//        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
//        return "premonitionview";

}
