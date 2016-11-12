package pl.edu.agh.iosr.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.iosr.cdm.AcceptedProposal;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.controller.LearnerController;
import pl.edu.agh.iosr.utils.ApplicationEndpoints;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Szymon on 2016-11-12.
 */
@Service
public class ClientService {

    final static Logger logger = LoggerFactory.getLogger(LearnerController.class);

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    @Autowired
    private LeaderService leaderService;

    public boolean proposeToLeader(String key, Integer value){
        RestTemplate restTemplate = new RestTemplate();
        boolean leaderExists = false;
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        for (Node node : nodes) {
            if(leaderService.isLeader(node.getNodeUrl())){
                logger.info("Found leader, sending request");
                leaderExists = true;
                MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
                params.set("key", key);
                params.set("value", Integer.toString(value));
                restTemplate.postForEntity("http://" +node.getNodeUrl()+ ApplicationEndpoints.PROPOSER_ACCEPT_URL, params, String.class);
            }
        }
        return leaderExists;
    }

    public Integer retrieveFromLearners(String key){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        RestTemplate restTemplate = new RestTemplate();
        HashMap<Integer, Integer> values = new HashMap<>();
        for (Node node : nodes){
            AcceptedProposal proposal = restTemplate.getForObject("http://" + node.getNodeUrl() + ApplicationEndpoints.LERNER_URL.getEndpoint() + "?key=" + key, AcceptedProposal.class);
            if(values.get(proposal.getValue()) == null){
                values.put(proposal.getValue(), 1);
            } else {
                values.put(proposal.getValue(), values.get(proposal.getValue())+1);
            }
        }
        int max = 0;
        Integer bestValue = null;
        for(Integer localKey : values.keySet()){
            if(values.get(localKey) > max){
                bestValue = localKey;
            }
        }
        return bestValue;

    }

}
