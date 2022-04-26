package nl.bioinf.premonition.services;

/*
Author: Nils Mooldijk
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PremonitionStarter {

    private final PyProcessor pyProcessor;

    @Autowired
    public PremonitionStarter(PyProcessor pyProcessor){
        this.pyProcessor = pyProcessor;
    }

    @GetMapping(value = "/pyProcessor")
    public String callPyProcessor(){
        return pyProcessor.runScript();
        //return "complete";
    }
}