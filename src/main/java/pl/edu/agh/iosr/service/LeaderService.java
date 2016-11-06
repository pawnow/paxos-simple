package pl.edu.agh.iosr.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;
import pl.edu.agh.iosr.utils.ApplicationEndpoints;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaderService {

    @Autowired
    private NodesRegistryRepository nodesRegistryRepository;

    public boolean isLeader(String url){
        String localUrl = extractHostAndPortFromUrl(url);
        List<Node> nodes = Lists.newArrayList(nodesRegistryRepository.findAll());
        Optional<Long> serverId = nodes.stream().filter(node -> node.getNodeUrl().equals(localUrl)).map(Node::getId).findAny();
        if(!serverId.isPresent()){
            return false;
        }
        List<Node> possibleLeaders = nodes.stream().filter(node -> node.getId() < serverId.get()).collect(Collectors.toList());
        RestTemplate restTemplate = new RestTemplate();
        for(Node node: possibleLeaders){
            try {
                boolean isOnline = restTemplate.getForObject("http://" + node.getNodeUrl() + ApplicationEndpoints.ONLINE_URL.getEndpoint(), Boolean.class);
                if (isOnline) {
                    return false;
                }
            }
            catch(Exception ignored){
            }
        }
        return true;
    }

    private String extractHostAndPortFromUrl(String url){
        return url.split("//")[1].split("/")[0];
    }
}
