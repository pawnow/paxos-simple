package pl.edu.agh.iosr.controller;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.iosr.cdm.AcceptedProposal;
import pl.edu.agh.iosr.cdm.AcceptedProposalRepository;
import pl.edu.agh.iosr.cdm.Proposal;

import java.util.*;

@RequestMapping("/learner")
@RestController
public class LearnerController {

    @Autowired
    AcceptedProposalRepository acceptedProposalRepository;

    private Map<String, AcceptedProposal> learnedProposal = new HashMap<>();

    final static Logger logger = LoggerFactory.getLogger(LearnerController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/learn")
    public void learn(@RequestBody Proposal proposal) {
        logger.info("Learned new value: " + proposal);
        AcceptedProposal acceptedProposal = new AcceptedProposal(proposal);
        acceptedProposalRepository.save(acceptedProposal);
        learnedProposal.put(proposal.getKey(), acceptedProposal);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/learn")
    public List<AcceptedProposal> getLearnedValue() {
        return Lists.newArrayList(learnedProposal.values());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/learn/{key}")
    public AcceptedProposal getLearnedValue(@PathVariable(name = "key") String key) {
        return Optional.ofNullable(learnedProposal
                .get(key)).orElseGet(() -> AcceptedProposal.builder().build());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/clean")
    public void clean(){
        learnedProposal = new HashMap<>();
        logger.info("LearnerController state has been reset");
    }
}