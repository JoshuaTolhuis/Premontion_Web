package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.models.PremonitionForm;
import nl.bioinf.premonition.services.PyProcessor;
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

    private final PyProcessor pyProcessor;;

    @Autowired
    public PremonitionController(PyProcessor pyProcessor) {
        this.pyProcessor = pyProcessor;
    }

    @GetMapping(value = "premonition")
    public String premonitionPage(){
        return "premonition";
    }

    @PostMapping(value = "premonition")
    public String uploadForm(@RequestParam MultipartFile file, @RequestParam MultipartFile refFile,
                             @RequestParam String includeNRCs,
                             @RequestParam String removeEdges, @RequestParam String limited,
                             @RequestParam String test, PremonitionForm form) {

        /*
        1. appends model
        2. saves users files
        3. starts premonition
        4. returns viewpage
         */

//         normalize the file path

        form.setFile(file);
        form.setRefFile(refFile);
        form.setLimited(limited);
        form.setTest(test);
        form.setRemoveEdges(removeEdges);
        form.setIncludeNRCs(includeNRCs);
        
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

        pyProcessor.runScript(form);

        return "viewpage";
    }



}
