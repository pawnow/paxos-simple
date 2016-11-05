package pl.edu.agh.iosr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.service.AcceptorService;

import java.util.Optional;

@RequestMapping("/learner")
@RestController
public class LearnerController {

    final static Logger logger = LoggerFactory.getLogger(LearnerController.class);

    public static final String LEARN_URL = "/learner/learn";

    private Optional<Proposal> learnedProposal = Optional.empty();

    @RequestMapping(method = RequestMethod.POST, value = "/learn")
    public void learn(@RequestBody Proposal proposal) {
        logger.debug("learn: "+proposal.toString());
        //// TODO: 05.11.16 implement learner here
        learnedProposal = Optional.ofNullable(proposal);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/learn")
    public Proposal getLearnedValue() {
        return learnedProposal.orElseGet(() -> Proposal.builder().build());
    }
}