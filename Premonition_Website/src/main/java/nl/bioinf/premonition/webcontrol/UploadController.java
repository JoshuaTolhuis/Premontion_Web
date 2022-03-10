package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.models.Premonition;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class UploadController {

    private final String UPLOAD_DIR = "src/main/resources/uploads/";

    @BeforeEach
    public void PremonitionForm(Model model)
    {
        //create a premonition object
        Premonition pre = new Premonition();
        //provide premonition object to the model
        model.addAttribute("Premonition", pre);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "File was found to be empty");
            return "redirect:/";
        }
    // normalize the file path
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());

    // save the file on the local file system
        try {
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
            e.printStackTrace();
        }

        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

        return "redirect:/";
    }

    @GetMapping("/")
    public String homepage(){
        return "homepage";
    }
}
