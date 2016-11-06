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

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/proposer")
@RestController
public class ProposerController {

    final static Logger logger = LoggerFactory.getLogger(ProposerController.class);

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    private static long id = 0;

    private HashMap<Long, HashMap<Node, Boolean>> quorums = new HashMap<>();

    @Autowired
    private LeaderService leaderService;

    @RequestMapping("/propose")
    public Proposal propose(HttpServletRequest request) {
        if(leaderService.isLeader(request.getRequestURL().toString())){
            quorums.put(new Long(id++), getMinimalQuorum());
            logger.debug("created proposal with id: " + id);
            long id = (int) (System.currentTimeMillis() / 1000L) % Lists.newArrayList(nodesRegistryRepository.findAll()).size() + Integer.valueOf(request.getLocalName());
            return Proposal.builder().id(id).server(request.getLocalName()).build();
        }
        else{
            return null;
        }
    }

    @RequestMapping("/accept")
    public Proposal accept(@RequestBody Proposal proposal) {
        HashMap<Node, Boolean> accepted = quorums.get(proposal.getId());
        accepted.get(proposal.getServer());

    }

    private HashMap<Node, Boolean> getMinimalQuorum(){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        Collections.shuffle(nodes);
        List<Node> nodesHalf = nodes.subList(0, (nodes.size()%2));
        HashMap<Node, Boolean> quorum = new HashMap<Node, Boolean>();
        for(Node node : nodesHalf){
            quorum.put(node, false);
        }
        return quorum;
    }

    private HashMap<Node, Boolean> getFullQuorum(){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        Collections.shuffle(nodes);
        HashMap<Node, Boolean> quorum = new HashMap<Node, Boolean>();
        for(Node node : nodes){
            quorum.put(node, false);
        }
        return quorum;
    }
}