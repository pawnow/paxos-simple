package pl.edu.agh.iosr.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Szymon on 2016-11-06.
 */

@Service
public class QuorumProviderService {

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    public ConcurrentHashMap<Node, Boolean> getMinimalQuorum(){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        Collections.shuffle(nodes);
        List<Node> nodesHalf = nodes.subList(0, (int) Math.floor(nodes.size()/2)+1);
        ConcurrentHashMap<Node, Boolean> quorum = new ConcurrentHashMap<Node, Boolean>();
        for(Node node : nodesHalf){
            quorum.put(node, false);
        }
        return quorum;
    }

    public ConcurrentHashMap<Node, Boolean> getFullQuorum(){
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        ConcurrentHashMap<Node, Boolean> quorum = new ConcurrentHashMap<Node, Boolean>();
        for(Node node : nodes){
            quorum.put(node, false);
        }
        return quorum;
    }
}
