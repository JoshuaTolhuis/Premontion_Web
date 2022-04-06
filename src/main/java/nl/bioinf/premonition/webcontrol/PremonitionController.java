package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.models.PremonitionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PremonitionController {

    @GetMapping(value = "premonition")
    public String premonitionPage(ModelMap modelMap){
        modelMap.addAttribute("premonition", new PremonitionForm());
        return "premonition";
    }

    @PostMapping(value = "premonition")
    public String uploadFile(@RequestParam MultipartFile[] files, @RequestParam String includeNRCs,
                             @RequestParam String removeEdges, @RequestParam String limited, ModelMap modelMap) {
        // normalize the file path
        modelMap.addAttribute("files", files);
        modelMap.addAttribute("incl_NRC's", includeNRCs);
        modelMap.addAttribute("removeEdges", removeEdges);
        modelMap.addAttribute("limited", limited);
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
