package pl.edu.agh.iosr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.AcceptedProposal;
import pl.edu.agh.iosr.cdm.AcceptedProposalRepository;
import pl.edu.agh.iosr.cdm.Proposal;

import java.util.Optional;

@RequestMapping("/learner")
@RestController
public class LearnerController {

    @Autowired
    AcceptedProposalRepository acceptedProposalRepository;

    private Optional<AcceptedProposal> learnedProposal = Optional.empty();

    final static Logger logger = LoggerFactory.getLogger(LearnerController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/learn")
    public void learn(@RequestBody Proposal proposal) {
        logger.info("Learned new value: " + proposal);
        AcceptedProposal acceptedProposal = new AcceptedProposal(proposal);
        acceptedProposalRepository.save(acceptedProposal);
        learnedProposal = Optional.ofNullable(acceptedProposal);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/learn")
    public AcceptedProposal getLearnedValue() {
        return learnedProposal.orElseGet(() -> AcceptedProposal.builder().build());
    }
}