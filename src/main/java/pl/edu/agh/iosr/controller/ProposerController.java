package pl.edu.agh.iosr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.service.LeaderService;
import pl.edu.agh.iosr.service.ProposerService;
import pl.edu.agh.iosr.service.QuorumProviderService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/proposer")
@RestController
public class ProposerController {

    final static Logger logger = LoggerFactory.getLogger(ProposerController.class);

    @Autowired
    private ProposerService proposerService;

    @Autowired
    private QuorumProviderService quorumProviderService;

    @Autowired
    private LeaderService leaderService;

    private ConcurrentHashMap<Long, ConcurrentHashMap<Node, Boolean>> quorums = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, Proposal> bestProposal = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, Integer> valueToSet = new ConcurrentHashMap<>();

    @RequestMapping("/propose")
    public Proposal propose(HttpServletRequest request, @RequestParam("key") String key, @RequestParam("value") Integer value) {
        if (leaderService.isLeader(request.getRequestURL().toString())) {
            String url = request.getRequestURL().toString().split("//")[1].split("/")[0];
            ConcurrentHashMap<Node, Boolean> quorum = quorumProviderService.getMinimalQuorum();
            long id = proposerService.generateProposalId(request.getRequestURL().toString());
            quorums.put(id, quorum);
            valueToSet.put(id, value);
            logger.debug("created proposal with id: " + id + " and key " + key);
            Proposal proposal = Proposal.builder().id(id).key(key).server(url).build();
            proposerService.sendProposalToQuorum(quorum, proposal);
            return proposal;
        } else {
            return null;
        }
    }

    @RequestMapping("/accept")
    public Proposal accept(HttpServletRequest request, @RequestBody Proposal proposal) {
        if (leaderService.isLeader(request.getRequestURL().toString())) {
            ConcurrentHashMap<Node, Boolean> accepted = quorums.get(proposal.getId());
            Proposal currentBest = bestProposal.get(proposal.getId());
            if (proposal.getHighestAcceptedProposalId() != null && (currentBest == null || (currentBest.getHighestAcceptedProposalId() < proposal.getHighestAcceptedProposalId()))) {
                bestProposal.put(proposal.getId(), proposal);
            }
            currentBest = bestProposal.get(proposal.getId());
            accepted.keySet().stream().filter(node -> node.getNodeUrl().equals(proposal.getServer())).forEach(node -> accepted.put(node, true));
            if (proposerService.checkForQuorum(accepted)) {
                if (currentBest == null) {
                    currentBest = Proposal.builder().id(proposal.getId()).key(proposal.getKey()).value(valueToSet.get(proposal.getId())).build();
                }
                proposerService.sendAccept(accepted, currentBest);
                return currentBest;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/accepted")
    public Proposal  accept(@RequestBody Proposal proposal) {
        logger.info("Proposal has been accepted: " + proposal);
        return proposal;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/clean")
    public void clean() {
        quorums.clear();
        bestProposal.clear();
        valueToSet.clear();
        logger.info("ProposerController state has been reset");
    }
}