package pl.edu.agh.iosr.service;

import org.springframework.stereotype.Service;

/**
 * Created by domin on 05.11.16.
 */

@Service
public class FaultService {

    private static Boolean isDown = false;

    public Boolean isDown() {
        synchronized (isDown){
            return isDown;
        }
    }

    public void destroyNode(){
        synchronized (isDown){
            isDown = true;
        }
    }

    public void repareNode(){
        synchronized (isDown){
            isDown = false;

        }
    }
}
