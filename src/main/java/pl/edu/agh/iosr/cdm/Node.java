package pl.edu.agh.iosr.cdm;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by domin on 05.11.16.
 */
@Data
@Builder
@Entity
public class Node {

    @Id
    private Long id;

    private String nodeUrl;

    public Node() {
    }
    public Node(Long id, String nodeUrl) {
        this.id = id;
        this.nodeUrl = nodeUrl;
    }
}
