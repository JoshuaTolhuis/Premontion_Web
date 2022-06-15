package nl.bioinf.premonition.webcontrol;

import nl.bioinf.premonition.models.PremonitionForm;
import nl.bioinf.premonition.services.PyProcessor;
import nl.bioinf.premonition.util.SessionUtil;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import javax.servlet.http.HttpSession;


@Controller
public class PremonitionController {

    private final PyProcessor pyProcessor;
    @Value("${data.folder.location}")
    String UPLOAD_DIR;

    @Autowired
    public PremonitionController(PyProcessor pyProcessor) {
        this.pyProcessor = pyProcessor;

    }

    @GetMapping(value = "premonition")
    public String premonitionPage(Model model){
        model.addAttribute("form", new PremonitionForm());
        return "premonition";
    }

    @PostMapping(value = "premonition")
    public String uploadForm(@ModelAttribute MultipartFile file, @ModelAttribute MultipartFile refFile,
                             @ModelAttribute PremonitionForm form,
                             @RequestParam Boolean removeEdges, @RequestParam Boolean includeNRCs,
                             @RequestParam String limited,
                             HttpSession session, Model model) throws IOException {

        /*
        1. appends model
        2. saves users files
        3. getSession ID
        4. starts premonition
        5. returns viewpage
         */

        String userid = SessionUtil.getUserID(session);

//        model.addAttribute("file", file);
//        model.addAttribute("refFile",refFile);
//        model.addAttribute("removeEdges",removeEdges);
//        model.addAttribute("includeNRCs", includeNRCs);
//        model.addAttribute("limited", limited);

        form.setFile(file);
        form.setRefFile(refFile);
        form.setRemoveEdges(removeEdges);
        form.setIncludeNRCs(includeNRCs);
        form.setLimited(limited);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String refFileName = StringUtils.cleanPath(Objects.requireNonNull(refFile.getOriginalFilename()));

        // check if file exists and isn't empty
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        Path refPath = Paths.get(UPLOAD_DIR + refFileName);
        Files.copy(refFile.getInputStream(), refPath, StandardCopyOption.REPLACE_EXISTING);

//        TODO change to validition via BindingResult
//        try {
//            Path path = Paths.get(UPLOAD_DIR + fileName);
//            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//            Path refPath = Paths.get(UPLOAD_DIR + refFileName);
//            Files.copy(refFile.getInputStream(), refPath, StandardCopyOption.REPLACE_EXISTING);
//            // save the file on the local file system
//            BufferedReader br = new BufferedReader(new FileReader(fileName));
//            if(!Objects.equals(br.readLine(), "")){
//                //String FileUploadException = new Exception(FileUploadException);;
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return "error";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        pyProcessor.runScript(form, userid, session);



        return "viewpage";
    }



}
