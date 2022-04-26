package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.models.PremonitionForm;
import nl.bioinf.premonition.services.PremonitionStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
public class PremonitionController {

    private final PremonitionStarter premonitionStarter;

    @Autowired
    public PremonitionController(PremonitionStarter premonitionStarter) {
        this.premonitionStarter = premonitionStarter;
    }

    @GetMapping(value = "premonition")
    public String premonitionPage(ModelMap modelMap){
        modelMap.addAttribute("form", new PremonitionForm());
        return "premonition";
    }

    @PostMapping(value = "premonition")
    public String uploadForm(@RequestParam MultipartFile file, @RequestParam MultipartFile refFile,
                             @RequestParam String includeNRCs,
                             @RequestParam String removeEdges, @RequestParam String limited,
                             @RequestParam String test, Model model) {

        /*
        1. appends model
        2. saves users files
        3. starts premonition
        4. returns viewpage
         */

//         normalize the file path
        model.addAttribute("file", file);
        model.addAttribute("Ref_file", refFile);
        model.addAttribute("includeNRCs", includeNRCs);
        model.addAttribute("removeEdges", removeEdges);
        model.addAttribute("limited", limited);
        model.addAttribute("test", test);

        System.out.println(model.getAttribute("test"));
        
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String refFileName = StringUtils.cleanPath(refFile.getOriginalFilename());

        // save the file on the local file system
        try {
            String UPLOAD_DIR = "src/main/resources/uploads/";
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            Path refPath = Paths.get(UPLOAD_DIR + refFileName);
            Files.copy(refFile.getInputStream(), refPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return success response
        // attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

        premonitionStarter.callPyProcessor();

        return "viewpage";
    }



}
