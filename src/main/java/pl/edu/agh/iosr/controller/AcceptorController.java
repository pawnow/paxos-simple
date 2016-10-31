package pl.edu.agh.iosr.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Proposal;

import java.util.Optional;

@RequestMapping("/acceptor")
@RestController
public class AcceptorController {

    private Optional<Proposal> acceptedProposal = Optional.empty();

    @RequestMapping(method = RequestMethod.GET, value = "/accept")
    public Proposal getAcceptedProposal() {
        return acceptedProposal.orElseGet(() -> null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/accept")
    public Proposal accept(@RequestBody Proposal proposal) {
        if (shouldAccept(proposal)) {
            Optional<Proposal> previouslyAcceptedProposal = acceptedProposal;
            acceptNewProposal(proposal);
            return previouslyAcceptedProposal.orElseGet(() -> null);
        }
        return null;
    }

    private boolean shouldAccept(Proposal newProposal) {
        return newProposal.getId() > acceptedProposal.map(Proposal::getId).orElseGet(() -> (-1L));
    }

    private void acceptNewProposal(Proposal newProposal) {
        acceptedProposal = Optional.of(newProposal);
    }

}