package pl.edu.agh.iosr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.service.LeaderService;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/proposer")
@RestController
public class ProposerController {

    private static long id = 0;

    @Autowired
    private LeaderService leaderService;

    @RequestMapping("/propose")
    public Proposal propose(HttpServletRequest request) {
        if(leaderService.isLeader(request.getRequestURL().toString())){
            //TODO: send proposal to quorum, receive response, send accept
            return Proposal.builder().id(id++).build();
        }
        else{
            return null;
        }
    }
}