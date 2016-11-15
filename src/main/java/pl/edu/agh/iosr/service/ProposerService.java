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
import java.util.List;
import java.util.Optional;

/**
 * Created by Szymon on 2016-11-06.
 */
@Service
public class ProposerService {
    final static Logger logger = LoggerFactory.getLogger(ProposerService.class);

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    @Autowired
    private LeaderService leaderService;

    public void sendProposalToQuorum(HashMap<Node, Boolean> quorum, Proposal proposal){

        RestTemplate restTemplate = new RestTemplate();
        for(Node node: quorum.keySet()){
            try {
                logger.debug("Sending proposal to accepter " + "http://" +node.getNodeUrl()+ ApplicationEndpoints.ACCEPTOR_PROPOSE_URL.getEndpoint());
                restTemplate.postForEntity("http://" +node.getNodeUrl()+ ApplicationEndpoints.ACCEPTOR_PROPOSE_URL.getEndpoint(), proposal, Proposal.class);
            } catch (Exception e){
                e.printStackTrace();
                logger.error("Error during sending proposal to quorum");
            }
        }
    }

    public Boolean checkForQuorum(HashMap<Node, Boolean> quorum) {
        int totalSize = Lists.newArrayList(nodesRegistryRepository.findAll()).size();
        int acceptedSize = 0;
        Boolean flag = false;

        for(Boolean accepted : quorum.values()){
            if (accepted)
                acceptedSize++;
        }
        if (acceptedSize*2>totalSize){
            flag = true;

        }
        return flag;
    }

    public void sendAccept(HashMap<Node, Boolean> quorum, Proposal proposal){
        RestTemplate restTemplate = new RestTemplate();
        quorum.keySet().stream().filter(quorum::get).forEach(node -> {
            restTemplate.postForObject("http://" + node.getNodeUrl() + ApplicationEndpoints.ACCEPTOR_ACCEPT_URL.getEndpoint(), proposal, String.class);
            logger.debug("Sending confirmation to accepter " + "http://" + node.getNodeUrl() + ApplicationEndpoints.ACCEPTOR_ACCEPT_URL.getEndpoint());
        });
    }

    public long generateProposalId(String url){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        Optional<Long> serverId = leaderService.getServerId(nodes, url);
        return System.currentTimeMillis() / nodes.size() + serverId.get();
    }
}
