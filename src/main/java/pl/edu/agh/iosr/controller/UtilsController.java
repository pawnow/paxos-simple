package pl.edu.agh.iosr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.service.FaultService;
import pl.edu.agh.iosr.service.LeaderService;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/utils")
@RestController
public class UtilsController {

    @Autowired
    private FaultService faultService;

    @Autowired
    private LeaderService leaderService;

    final static Logger logger = LoggerFactory.getLogger(UtilsController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/offline")
    public void goOffline() {
        logger.info("Setting status to offline");
        faultService.destroyNode();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/online")
    public void goOnline() {
        logger.info("Setting status to online");
        faultService.repareNode();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/status")
    public boolean isOnline() {
        return !faultService.isDown();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/leader")
    public boolean isLeader(HttpServletRequest request) {
        return leaderService.isLeader(request.getRequestURL().toString());
    }

}