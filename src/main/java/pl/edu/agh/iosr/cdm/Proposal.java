package pl.edu.agh.iosr.cdm;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@Entity
public class Proposal {

    @Id
    private Long id;
    private Integer value;
    private String server;

    public Proposal() {
    }

    public Proposal(Long id, Integer value, String server) {
        this.id = id;
        this.value = value;
        this.server = server;
    }
}
