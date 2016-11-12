package pl.edu.agh.iosr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.iosr.controller.ProposerController;
import pl.edu.agh.iosr.utils.ApplicationEndpoints;

/**
 * Created by Szymon on 2016-11-12.
 */
@Service
public class StateCleanerService {

    final static Logger logger = LoggerFactory.getLogger(ProposerController.class);


    public void clearControllers(String url){
        RestTemplate restTemplate = new RestTemplate();
        String extractedUrl = extractHostAndPortFromUrl(url);
        try {
            restTemplate.postForLocation("http://" + extractedUrl+ ApplicationEndpoints.LEARNER_CLEAN_URL.getEndpoint(), String.class);
            restTemplate.postForLocation("http://" + extractedUrl+ ApplicationEndpoints.PROPOSER_CLEAN_URL.getEndpoint(), String.class);
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Error during cleaning controlers");
        }

    }

    private String extractHostAndPortFromUrl(String url){
        return url.split("//")[1].split("/")[0];
    }

}
