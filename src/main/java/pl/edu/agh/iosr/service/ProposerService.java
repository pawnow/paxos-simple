package pl.edu.agh.iosr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.Proposal;

import java.util.HashMap;

/**
 * Created by Szymon on 2016-11-06.
 */
@Service
public class ProposerService {
    final static Logger logger = LoggerFactory.getLogger(ProposerService.class);

    public void sendProposalToQuorum(HashMap<Node, Boolean> quorum, Proposal proposal){

        RestTemplate restTemplate = new RestTemplate();
        for(Node node: quorum.keySet()){
            try {
                logger.debug("Sending to learner " + "http://" +node.getNodeUrl()+ "/accept");
                restTemplate.postForObject("http://" +node.getNodeUrl()+ "/accept", proposal, String.class);
            } catch (Exception e){ //todo: timeout exception??
                e.printStackTrace();
            }
        }
    }
}
