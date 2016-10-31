package pl.edu.agh.iosr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/utils")
@RestController
public class UtilsController {

    private boolean online = true;

    @RequestMapping(method = RequestMethod.POST, value = "/offline")
    public void goOffline() {
        online = false;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/online")
    public void goOnline() {
        online = true;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/status")
    public boolean isOnline() {
        return online;
    }

}