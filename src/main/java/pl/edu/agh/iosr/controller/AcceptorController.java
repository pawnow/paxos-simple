package pl.edu.agh.iosr.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.cdm.ProposalRepository;

import java.util.List;
import java.util.Optional;

@RequestMapping("/acceptor")
@RestController
public class AcceptorController {

    @Autowired
    private ProposalRepository proposalRepository;


    @Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/accept")
    public List<Proposal> getAcceptedProposal() {
        return Lists.newArrayList(proposalRepository.findAll());
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/accept")
    public Proposal accept(@RequestBody Proposal newProposal) {
        Optional<Proposal> previouslyAcceptedProposal = proposalRepository.getByMaxId();

        if (shouldAccept(newProposal, previouslyAcceptedProposal)) {
            acceptNewProposal(newProposal);
            return previouslyAcceptedProposal.orElseGet(() -> null);
        }
        return null;
    }

    private boolean shouldAccept(Proposal newProposal, Optional<Proposal> previouslyAcceptedProposal) {
        return newProposal.getId() > previouslyAcceptedProposal.map(Proposal::getId).orElseGet(() -> (-1L));
    }

    private void acceptNewProposal(Proposal newProposal) {
        proposalRepository.save(newProposal);
    }

}