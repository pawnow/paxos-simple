package pl.edu.agh.iosr.controller;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    final static Logger logger = LoggerFactory.getLogger(AcceptorController.class);

    @Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/propose")
    public List<Proposal> getAcceptedProposal() {
        return Lists.newArrayList(proposalRepository.findAll());
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/propose")
    public Proposal propose(@RequestBody Proposal newProposal) {
        Optional<Proposal> previouslyAcceptedProposal = proposalRepository.getByMaxId();
        if (shouldAccept(newProposal, previouslyAcceptedProposal)) {
            acceptNewProposal(newProposal);
            logger.info("Accepted proposal propose: " + newProposal);
            return previouslyAcceptedProposal.orElseGet(() -> null);
        }
        logger.info("Rejected proposal propose: " + newProposal);
        return null;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/accept")
    public Proposal accept(@RequestBody Proposal newProposal) {
        Optional<Proposal> previouslyAcceptedProposal = proposalRepository.getByMaxId();
        if (shouldAccept(newProposal, previouslyAcceptedProposal)) {
            logger.info("Accepted proposal accept: " + newProposal);
            return previouslyAcceptedProposal.orElseGet(() -> null);
        }
        logger.info("Rejected proposal accept: " + newProposal);
        return null;
    }

    private boolean shouldAccept(Proposal newProposal, Optional<Proposal> previouslyAcceptedProposal) {
        return newProposal.getId() > previouslyAcceptedProposal.map(Proposal::getId).orElseGet(() -> (-1L));
    }

    private void acceptNewProposal(Proposal newProposal) {
        proposalRepository.save(newProposal);
    }

}