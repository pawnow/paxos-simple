package pl.edu.agh.iosr.controller;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.service.LeaderService;
import pl.edu.agh.iosr.service.ProposerService;
import pl.edu.agh.iosr.service.QuorumProviderService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

    private HashMap<Long, HashMap<Node, Boolean>> quorums = new HashMap<>();

    private HashMap<Long, Proposal> bestProposal = new HashMap<>();

    private Integer valueToSet;

    @Autowired
    private LeaderService leaderService;

    @RequestMapping("/propose")
    public Proposal propose(HttpServletRequest request, @RequestParam("value") Integer value) {
        if(leaderService.isLeader(request.getRequestURL().toString())){
            valueToSet = value;
            String url = request.getRequestURL().toString();
            HashMap<Node, Boolean> quorum = quorumProviderService.getMinimalQuorum();
            long id = proposerService.generateProposalId(url);
            quorums.put(id, quorum);
            logger.debug("created proposal with id: " + id);
            Proposal proposal = Proposal.builder().id(id).key("key-"+id).server(url).build();
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
        Proposal currentBest = bestProposal.get(proposal.getId());
        if(proposal.getHighestAcceptedProposalId()!=null && (currentBest==null || (currentBest.getHighestAcceptedProposalId() < proposal.getHighestAcceptedProposalId()))){
                bestProposal.put(proposal.getId(), proposal);
        }
        accepted.keySet().stream().filter(node -> node.getNodeUrl().equals(proposal.getServer())).forEach(node -> accepted.put(node, true));
        if(proposerService.checkForQuorum(accepted)) {
            if(currentBest == null){
                currentBest = Proposal.builder().id(proposal.getId()).value(valueToSet).build();
            }
            proposerService.sendAccept(accepted, bestProposal.get(proposal.getId()));
            return currentBest;
        } else {
            return null;
        }
    }
}