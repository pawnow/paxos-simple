package pl.edu.agh.iosr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.service.FaultService;

@RequestMapping("/utils")
@RestController
public class UtilsController {

    @Autowired
    private FaultService faultService;

    @RequestMapping(method = RequestMethod.POST, value = "/offline")
    public void goOffline() {
        faultService.destroyNode();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/online")
    public void goOnline() {
        faultService.repareNode();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/status")
    public boolean isOnline() {
        return !faultService.isDown();
    }

}