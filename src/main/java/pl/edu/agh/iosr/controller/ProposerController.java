package pl.edu.agh.iosr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.service.LeaderService;

import javax.servlet.http.HttpServletRequest;
import pl.edu.agh.iosr.cdm.ProposalRepository;
import pl.edu.agh.iosr.service.ProposerService;
import pl.edu.agh.iosr.service.QuorumProviderService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/proposer")
@RestController
public class ProposerController {

    final static Logger logger = LoggerFactory.getLogger(ProposerController.class);

    @Autowired
    private ProposerService proposerService;

    @Autowired
    private QuorumProviderService quorumProviderService;

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    private HashMap<Long, HashMap<Node, Boolean>> quorums = new HashMap<>();

    @Autowired
    private LeaderService leaderService;

    @RequestMapping("/propose")
    public Proposal propose(HttpServletRequest request) {
        if(leaderService.isLeader(request.getRequestURL().toString())){
            String url = request.getRequestURL().toString();
            url = url.split("//")[1].split("/")[0].split(":")[1];
            long id =  System.currentTimeMillis() / Lists.newArrayList(nodesRegistryRepository.findAll()).size() + Integer.valueOf(url);
            HashMap<Node, Boolean> quorum = quorumProviderService.getMinimalQuorum();
            quorums.put(new Long(id), quorum);
            logger.debug("created proposal with id: " + id);
            //TODO: Proposal value?
            Proposal proposal = Proposal.builder().id(id).server(url).build();
            proposerService.sendProposalToQuorum(quorum, proposal);
            return proposal;
        }
        else{
            return null;
        }
    }

    @RequestMapping("/accept")
    public Proposal accept(@RequestBody Proposal proposal) {
        HashMap<Node, Boolean> accepted = quorums.get(proposal.getId());
        for(Node node : accepted.keySet()){
            if (node.getNodeUrl().equals(proposal.getServer())){
                accepted.put(node, true);
            }
        }
        proposerService.checkForQuorum(accepted, proposal);
        return proposal;
    }
}