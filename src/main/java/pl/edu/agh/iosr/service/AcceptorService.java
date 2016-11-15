package pl.edu.agh.iosr.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.controller.LearnerController;
import pl.edu.agh.iosr.utils.ApplicationEndpoints;

import java.util.List;

/**
 * Created by domin on 05.11.16.
 */
@Service
public class AcceptorService {

    final static Logger logger = LoggerFactory.getLogger(AcceptorService.class);

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    public void informLearnersAndProposers(Proposal proposal){
        RestTemplate restTemplate = new RestTemplate();
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        for (Node node : nodes){
            try {
                logger.debug("Sending learned proposal to learner " + "http://" +node.getNodeUrl()+ ApplicationEndpoints.LERNER_URL);
                restTemplate.postForObject("http://" +node.getNodeUrl()+ ApplicationEndpoints.LERNER_URL, proposal, String.class);
                logger.debug("Sending learned proposal to proposer " + "http://" +node.getNodeUrl()+ ApplicationEndpoints.PROPOSER_ACCEPTED_URL);
                restTemplate.postForObject("http://" +node.getNodeUrl()+ ApplicationEndpoints.PROPOSER_ACCEPTED_URL, proposal, String.class);
            } catch (Exception e){
              e.printStackTrace();
            }
        }
    }

    public void informProposers(Proposal proposal){
        RestTemplate restTemplate = new RestTemplate();
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        for (Node node : nodes){
            try {
                logger.debug("Sending accept to proposer " + "http://" +node.getNodeUrl()+ ApplicationEndpoints.PROPOSER_ACCEPT_URL);
                restTemplate.postForEntity("http://" +node.getNodeUrl()+ ApplicationEndpoints.PROPOSER_ACCEPT_URL, proposal, Proposal.class);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
