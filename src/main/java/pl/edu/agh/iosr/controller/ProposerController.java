package pl.edu.agh.iosr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Proposal;

@RequestMapping("/proposer")
@RestController
public class ProposerController {

    private static long id = 0;

    @RequestMapping("/propose")
    public Proposal propose() {
        //TODO: send proposal to quorum, receive response, send accept
        return Proposal.builder().id(id++).build();
    }
}