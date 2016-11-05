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
import pl.edu.agh.iosr.controller.LearnerController;

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
                logger.debug("Sending to learner " + "http://" +node.getNodeUrl()+ LearnerController.LEARN_URL);
                restTemplate.postForObject("http://" +node.getNodeUrl()+ LearnerController.LEARN_URL, proposal, String.class);
                //// TODO: 05.11.16 : proposer?

            } catch (Exception e){ //todo: timeout exception??
              e.printStackTrace();
            }

        }

    }

}
