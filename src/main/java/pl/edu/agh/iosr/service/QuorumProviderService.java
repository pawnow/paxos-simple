package pl.edu.agh.iosr.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.cdm.NotEnoughOnlineNodesException;
import pl.edu.agh.iosr.utils.ApplicationEndpoints;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Szymon on 2016-11-06.
 */

@Service
public class QuorumProviderService {

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    public ConcurrentHashMap<Node, Boolean> getMinimalQuorum() {
        List<Node> allNodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        int numberOfNodesToChoose = (int) Math.floor(allNodes.size()/2)+1;
        List<Node> nodes = new LinkedList<>();
        RestTemplate restTemplate = new RestTemplate();
        Lists.newArrayList(nodesRegistryRepository.findAll()).forEach(node1 ->  {
            try{
                boolean isOnline = restTemplate.getForObject("http://" + node1.getNodeUrl() + ApplicationEndpoints.ONLINE_URL.getEndpoint(), Boolean.class);
                if(isOnline){
                    nodes.add(node1);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
        if(numberOfNodesToChoose > nodes.size()){
            throw new NotEnoughOnlineNodesException();
        }
        Collections.shuffle(nodes);
        List<Node> nodesHalf = nodes.subList(0, numberOfNodesToChoose);
        ConcurrentHashMap<Node, Boolean> quorum = new ConcurrentHashMap<Node, Boolean>();
        for(Node node : nodesHalf){
            quorum.put(node, false);
        }
        return quorum;
    }

    @Deprecated
    public ConcurrentHashMap<Node, Boolean> getMinimalQuorumFromAllNodes(){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        Collections.shuffle(nodes);
        List<Node> nodesHalf = nodes.subList(0, (int) Math.floor(nodes.size()/2)+1);
        ConcurrentHashMap<Node, Boolean> quorum = new ConcurrentHashMap<Node, Boolean>();
        for(Node node : nodesHalf){
            quorum.put(node, false);
        }
        return quorum;
    }

    @Deprecated
    public ConcurrentHashMap<Node, Boolean> getFullQuorum(){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        ConcurrentHashMap<Node, Boolean> quorum = new ConcurrentHashMap<Node, Boolean>();
        for(Node node : nodes){
            quorum.put(node, false);
        }
        return quorum;
    }
}
