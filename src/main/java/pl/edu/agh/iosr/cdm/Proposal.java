package pl.edu.agh.iosr.cdm;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@Entity
@ToString
public class Proposal {

    @Id
    private Long id;
    private String key;
    private Integer value;
    private String server;
    private Long highestAcceptedProposalId;

    public Proposal() {
    }

    public Proposal(Long id, String key, Integer value, String server, Long highestAcceptedProposalId) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.server = server;
        this.highestAcceptedProposalId = highestAcceptedProposalId;
    }
}
