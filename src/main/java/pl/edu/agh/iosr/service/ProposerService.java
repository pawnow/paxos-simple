package pl.edu.agh.iosr.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.cdm.Proposal;
import pl.edu.agh.iosr.utils.ApplicationEndpoints;

import java.util.HashMap;

/**
 * Created by Szymon on 2016-11-06.
 */
@Service
public class ProposerService {
    final static Logger logger = LoggerFactory.getLogger(ProposerService.class);

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    public void sendProposalToQuorum(HashMap<Node, Boolean> quorum, Proposal proposal){

        RestTemplate restTemplate = new RestTemplate();
        for(Node node: quorum.keySet()){
            try {
                logger.debug("Sending proposal to accepter " + "http://" +node.getNodeUrl()+ ApplicationEndpoints.ACCEPTOR_URL.getEndpoint());
                restTemplate.postForObject("http://" +node.getNodeUrl()+ ApplicationEndpoints.ACCEPTOR_URL.getEndpoint(), proposal, String.class);
            } catch (Exception e){
                e.printStackTrace();
                logger.error("Error during sending proposal to quorum");
            }
        }
    }

    public void checkForQuorum(HashMap<Node, Boolean> quorum, Proposal proposal) {
        int totalSize = Lists.newArrayList(nodesRegistryRepository.findAll()).size();
        int acceptedSize = 0;
        RestTemplate restTemplate = new RestTemplate();

        for(Boolean accepted : quorum.values()){
            if (accepted)
                acceptedSize++;
        }
        if (acceptedSize*2>totalSize){
            for(Node node : quorum.keySet()){
                if(quorum.get(node)){
                    //TODO: Change to proper url once task has been completed
                    restTemplate.postForObject("http://" +node.getNodeUrl()+ ApplicationEndpoints.ACCEPTOR_URL.getEndpoint(), proposal, String.class);
                    logger.debug("Sending confirmation to accepter " + "http://" +node.getNodeUrl()+ ApplicationEndpoints.ACCEPTOR_URL.getEndpoint());
                }
            }
        }
    }
}
