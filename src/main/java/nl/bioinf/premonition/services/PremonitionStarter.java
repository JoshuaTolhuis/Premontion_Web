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

<<<<<<< HEAD
//    @GetMapping(value = "/pyProcessor")
//    public String callPyProcessor(){
//        pyProcessor.runScript();
//        return "complete";
//    }
=======
    @GetMapping(value = "/pyProcessor")
    public String callPyProcessor(){
        return pyProcessor.runScript();
        //return "complete";
    }
>>>>>>> aa5ac46745b5910d707473b615aba29cb96af28b
}