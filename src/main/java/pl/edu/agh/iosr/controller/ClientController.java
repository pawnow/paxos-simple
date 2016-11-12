package pl.edu.agh.iosr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.service.ClientService;

/**
 * Created by Szymon on 2016-11-12.
 */
@RequestMapping("/client")
@RestController
public class ClientController {

    @Autowired
    ClientService clientService;

    @RequestMapping("/propose")
    public Proposal propose(@RequestParam("key") String key, @RequestParam("value") Integer value) {
        if(clientService.proposeToLeader(key, value)) {
            return Proposal.builder().key(key).value(value).build();
        } else
            return null;
    }

    @RequestMapping("/retrieve")
    public Integer retrieve(@RequestParam("key") String key){
        return clientService.retrieveFromLearners(key);
    }
}

