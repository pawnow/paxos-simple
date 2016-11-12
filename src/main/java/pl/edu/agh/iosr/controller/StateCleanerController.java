package pl.edu.agh.iosr.controller;

import org.omg.PortableServer.POAManagerPackage.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.cdm.AcceptedProposalRepository;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.cdm.ProposalRepository;
import pl.edu.agh.iosr.service.StateCleanerService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Szymon on 2016-11-12.
 */

@RequestMapping("/cleaner")
@RestController
public class StateCleanerController {

    @Autowired
    AcceptedProposalRepository acceptedProposalRepository;

    @Autowired
    NodesRegistryRepository nodesRegistryRepository;

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    StateCleanerService stateCleanerService;

    final static Logger logger = LoggerFactory.getLogger(LearnerController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/clean")
    public void clean(HttpServletRequest request){
        logger.info("Cleaning repositories content");
        acceptedProposalRepository.deleteAll();
        nodesRegistryRepository.deleteAll();
        stateCleanerService.clearControllers(request.getRequestURL().toString());

    }


}
