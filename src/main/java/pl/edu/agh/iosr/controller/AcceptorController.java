package pl.edu.agh.iosr.controller;

import com.google.common.collect.Lists;
import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.cdm.ProposalRepository;
import pl.edu.agh.iosr.service.AcceptorService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RequestMapping("/acceptor")
@RestController
public class AcceptorController {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private AcceptorService acceptorService;

    final static Logger logger = LoggerFactory.getLogger(AcceptorController.class);

    @Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/propose")
    public List<Proposal> getAcceptedProposal() {
        return Lists.newArrayList(proposalRepository.findAll());
    }

    @Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/propose/{key}")
    public List<Proposal> getAcceptedProposalForKey(@PathVariable(value = "key") String key) {
        return proposalRepository.getProposalsForKey(key);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/propose")
    public Proposal propose(HttpServletRequest request, @RequestBody Proposal newProposal) {

        Optional<Proposal> previouslyAcceptedProposal = Optional.ofNullable(proposalRepository.getByMaxIdForKey(newProposal.getKey()));
        String url = request.getRequestURL().toString().split("//")[1].split("/")[0];

        if (shouldAccept(newProposal, previouslyAcceptedProposal)) {

            Proposal oldProposal = previouslyAcceptedProposal.orElseGet(() -> null);

            if (oldProposal != null){
                newProposal.setHighestAcceptedProposalId(oldProposal.getId());
            }
            newProposal.setServer(url);
            acceptNewProposal(newProposal);
            logger.info("Accepted proposal propose: " + newProposal);

            if (oldProposal == null) {
                oldProposal = Proposal.builder().id(newProposal.getId())
                        .key(newProposal.getKey())
                        .server(url)
                        .highestAcceptedProposalId(null).build();
            } else {
                oldProposal.setHighestAcceptedProposalId(oldProposal.getId());
                oldProposal.setServer(url);
            }

            informProposers(oldProposal);
            return null;
        }
        logger.info("Rejected proposal propose: " + newProposal);
        return null;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/accept")
    public Proposal accept(@RequestBody Proposal newProposal) {
        Optional<Proposal> previouslyAcceptedProposal = Optional.ofNullable(proposalRepository.getByMaxIdForKey(newProposal.getKey()));
        if (shouldAccept(newProposal, previouslyAcceptedProposal)) {
            acceptNewProposal(newProposal);
            informLearnersAndProposers(newProposal);
            logger.info("Accepted proposal accept: " + newProposal);
            return previouslyAcceptedProposal.orElseGet(() -> null);
        }
        logger.info("Rejected proposal accept: " + newProposal);
        return null;
    }

    private void informLearnersAndProposers(Proposal newProposal) {
        acceptorService.informLearnersAndProposers(newProposal);
    }

    private void informProposers(Proposal previouslyAcceptedProposal) {
        acceptorService.informProposers(previouslyAcceptedProposal);
    }

    private boolean shouldAccept(Proposal newProposal, Optional<Proposal> previouslyAcceptedProposal) {
        return newProposal.getId() > previouslyAcceptedProposal.map(Proposal::getId).orElseGet(() -> (-1L));
    }

    private void acceptNewProposal(Proposal newProposal) {
        proposalRepository.save(newProposal);
    }

}