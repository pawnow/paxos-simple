package pl.edu.agh.iosr.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Proposal;

import java.util.Optional;

@RequestMapping("/learner")
@RestController
public class LearnerController {

    private Optional<Proposal> learnedProposal = Optional.empty();

    @RequestMapping(method = RequestMethod.POST, value = "/learn")
    public void learn(@RequestBody Proposal proposal) {
        learnedProposal = Optional.ofNullable(proposal);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/learn")
    public Proposal getLearnedValue() {
        return learnedProposal.orElseGet(() -> Proposal.builder().build());
    }
}