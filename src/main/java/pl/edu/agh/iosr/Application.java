package pl.edu.agh.iosr;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.DelegatingFilterProxy;
import pl.edu.agh.iosr.cdm.Node;
import pl.edu.agh.iosr.cdm.NodesRegistryRepository;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public DelegatingFilterProxy configure(){
        return new ServerFaultFilter();

    }
    @Bean
    @Transactional
    public CommandLineRunner prepareRegistry(NodesRegistryRepository nodesRegistryRepository){

        return (args) ->{
            nodesRegistryRepository.deleteAll();
            nodesRegistryRepository.save(new Node(4l, "paxos1.qwxpdbpqdg.eu-west-1.elasticbeanstalk.com:80"));
            nodesRegistryRepository.save(new Node(3l, "localhost:8081"));
            nodesRegistryRepository.save(new Node(2l, "localhost:8082"));
            nodesRegistryRepository.save(new Node(1l, "localhost:8083"));

        };
    }
}
